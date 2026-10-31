package com.littlestory.feature.reader.player

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.domain.browse.BrowseContext
import com.littlestory.domain.browse.observeBrowseContext
import com.littlestory.domain.browse.observeStory
import com.littlestory.domain.model.ReadingProgress
import com.littlestory.domain.model.SleepTimer
import com.littlestory.domain.player.Narrator
import com.littlestory.domain.player.SimulatedPlayback
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import com.littlestory.domain.repository.StoryStores
import com.littlestory.feature.reader.navigation.PlayerRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Optional
import javax.inject.Inject

@Immutable
data class PlayerUiState(
    val loading: Boolean = true,
    val notFound: Boolean = false,
    val storyId: String = "",
    val title: String = "",
    val heroName: String? = null,
    val languageName: String = "",
    val paletteId: String = "",
    val motifId: String = "",
    val durationMinutes: Int = 1,
    val chapters: Int = 1,
    val position: Float = 0f,
    val playing: Boolean = false,
    val speed: Float = AppConfig.Player.DEFAULT_SPEED,
    val volumePercent: Int = AppConfig.Player.DEFAULT_VOLUME_PERCENT,
    val sleepTimer: SleepTimer = SleepTimer.Off,
    val sleepSheetOpen: Boolean = false,
    val favourite: Boolean = false,
    val downloaded: Boolean = false,
) {
    val totalSeconds: Int get() = SimulatedPlayback.totalSeconds(durationMinutes)
    val elapsedSeconds: Int get() = (position * totalSeconds).toInt()
    val chapter: Int get() = SimulatedPlayback.chapterOf(position, chapters)
}

/**
 * SCR-11a/11b (LS-092..LS-094): a simulated narration player. Nothing is played; the position
 * advances on a timer and is saved as listening progress so Home and My Stories can resume it.
 * When a [Narrator] is bound (debug prototype: the phone's voice), it reads the pages aloud and
 * its page events move the position instead of the timer.
 */
@HiltViewModel
class PlayerViewModel
    @Inject
    constructor(
        savedState: SavedStateHandle,
        profiles: ProfilesRepository,
        private val preferences: PreferencesRepository,
        stores: StoryStores,
        narrator: Optional<Narrator>,
    ) : ViewModel() {
        private val narrator: Narrator? = narrator.orElse(null)
        private var pages: List<String> = emptyList()
        private var languageTag: String = SupportedLanguages.default.tag

        private val catalog = stores.catalog
        private val library = stores.library
        private val favourites = stores.favourites
        private val downloads = stores.downloads
        private val progress = stores.progress

        private val storyId = savedState.toRoute<PlayerRoute>().storyId
        private val browse = observeBrowseContext(profiles, preferences)

        private val _state = MutableStateFlow(PlayerUiState(storyId = storyId))
        val state: StateFlow<PlayerUiState> = _state.asStateFlow()

        private var ticker: Job? = null

        /** Simulated seconds left before the sleep timer stops playback; null when it is not counting. */
        private var sleepRemaining: Float? = null

        init {
            viewModelScope.launch {
                val ctx = browse.first()
                val saved = progress.progress(ctx.childId).first().firstOrNull { it.storyId == storyId }
                val speed = preferences.reading.first().playbackSpeed
                _state.update {
                    it.copy(
                        position = saved?.listenFraction?.takeIf { fraction -> fraction < 1f } ?: 0f,
                        speed =
                            speed.takeIf { s -> s in AppConfig.Player.speedOptions } ?: AppConfig.Player.DEFAULT_SPEED,
                    )
                }
                observe(ctx)
            }
        }

        private suspend fun observe(ctx: BrowseContext) {
            combine(
                observeStory(catalog, library, storyId, ctx.languageTag),
                favourites.favouriteIds(ctx.childId),
                downloads.downloads.map { rows -> rows.map { it.storyId }.toSet() },
            ) { story, favouriteIds, downloadedIds -> Triple(story, favouriteIds, downloadedIds) }
                .collect { (story, favouriteIds, downloadedIds) ->
                    _state.update {
                        if (story == null) {
                            it.copy(loading = false, notFound = true)
                        } else {
                            pages = story.pages.map { page -> page.text }
                            languageTag = story.resolvedLanguage
                            it.copy(
                                loading = false,
                                notFound = false,
                                title = story.title,
                                heroName = ctx.child?.firstName,
                                languageName = SupportedLanguages.fromTag(story.resolvedLanguage).nativeName,
                                paletteId = story.art.paletteId,
                                motifId = story.art.motifId,
                                durationMinutes = story.durationMinutes,
                                chapters = story.pages.size.coerceAtLeast(1),
                                favourite = story.id in favouriteIds,
                                downloaded = story.id in downloadedIds,
                            )
                        }
                    }
                }
        }

        fun onPlayPause() = if (_state.value.playing) pause() else play()

        fun onSeek(fraction: Float) {
            _state.update { it.copy(position = fraction.coerceIn(0f, 1f)) }
            saveProgress()
        }

        fun onRewind() =
            _state.update {
                it.copy(
                    position = SimulatedPlayback.skip(it.position, it.durationMinutes, -1),
                )
            }

        fun onForward() =
            _state.update {
                it.copy(
                    position = SimulatedPlayback.skip(it.position, it.durationMinutes, 1),
                )
            }

        fun onPreviousChapter() =
            _state.update {
                val chapterStart = SimulatedPlayback.chapterStart(it.chapter, it.chapters)
                // Like most players: the first tap returns to the start of the chapter, the next one goes back a chapter.
                val target = if (it.position - chapterStart > CHAPTER_RESTART_SLACK) it.chapter else it.chapter - 1
                it.copy(position = SimulatedPlayback.chapterStart(target.coerceAtLeast(0), it.chapters))
            }

        fun onNextChapter() =
            _state.update {
                val next = it.chapter + 1
                if (next >= it.chapters) it else it.copy(position = SimulatedPlayback.chapterStart(next, it.chapters))
            }

        fun onSpeedClick() {
            val speed = SimulatedPlayback.nextSpeed(_state.value.speed)
            _state.update { it.copy(speed = speed) }
            viewModelScope.launch {
                preferences.setReading(preferences.reading.first().copy(playbackSpeed = speed))
            }
        }

        fun onVolumeChange(percent: Int) =
            _state.update { it.copy(volumePercent = percent.coerceIn(0, AppConfig.Player.VOLUME_MAX_PERCENT)) }

        fun onSleepTimerClick() = _state.update { it.copy(sleepSheetOpen = true) }

        fun onSleepSheetDismiss() = _state.update { it.copy(sleepSheetOpen = false) }

        fun onSleepTimerSelected(timer: SleepTimer) {
            sleepRemaining = SimulatedPlayback.sleepAfterSeconds(timer)?.toFloat()
            _state.update { it.copy(sleepTimer = timer, sleepSheetOpen = false) }
        }

        fun onFavouriteClick() {
            viewModelScope.launch {
                val childId = browse.first().childId
                favourites.toggle(childId, storyId)
            }
        }

        fun onDownloadClick() {
            viewModelScope.launch {
                val downloaded = downloads.downloads.first().any { it.storyId == storyId }
                if (downloaded) downloads.remove(storyId) else downloads.start(storyId)
            }
        }

        private fun play() {
            if (_state.value.position >= 1f) _state.update { it.copy(position = 0f) }
            _state.update { it.copy(playing = true) }
            ticker?.cancel()
            val voice = narrator
            if (voice != null && pages.isNotEmpty()) {
                ticker = viewModelScope.launch { narrate(voice) }
                return
            }
            ticker =
                viewModelScope.launch {
                    var ticks = 0
                    while (isActive && _state.value.playing) {
                        delay(AppConfig.Player.SIMULATED_TICK_MS)
                        tick()
                        ticks++
                        if (ticks % AppConfig.Player.PROGRESS_SAVE_EVERY_TICKS == 0) saveProgress()
                    }
                }
        }

        private suspend fun narrate(voice: Narrator) {
            val current = _state.value
            voice.narrate(pages, current.chapter, languageTag, current.speed).collect { page ->
                _state.update { it.copy(position = SimulatedPlayback.chapterStart(page, it.chapters)) }
            }
            _state.update { it.copy(position = 1f, playing = false) }
            saveProgress()
        }

        private fun tick() {
            val current = _state.value
            val position = SimulatedPlayback.tick(current.position, current.speed)
            val remaining =
                sleepRemaining?.minus(
                    SimulatedPlayback.secondsPerTick(current.durationMinutes, current.speed),
                )
            sleepRemaining = remaining
            val sleepFired = remaining != null && remaining <= 0f
            val ended = position >= 1f
            _state.update {
                it.copy(
                    position = position,
                    playing = !(ended || sleepFired),
                    sleepTimer =
                        if (sleepFired ||
                            (ended && it.sleepTimer == SleepTimer.EndOfStory)
                        ) {
                            SleepTimer.Off
                        } else {
                            it.sleepTimer
                        },
                )
            }
            if (sleepFired) sleepRemaining = null
            if (ended || sleepFired) saveProgress()
        }

        private fun pause() {
            ticker?.cancel()
            _state.update { it.copy(playing = false) }
            saveProgress()
        }

        private fun saveProgress() {
            val position = _state.value.position
            viewModelScope.launch {
                val childId = browse.first().childId
                val existing = progress.progress(childId).first().firstOrNull { it.storyId == storyId }
                progress.update(
                    ReadingProgress(childId, storyId, existing?.page ?: 0, position, System.currentTimeMillis()),
                )
            }
        }

        private companion object {
            /** How far into a chapter "previous" still restarts it instead of going back one. */
            const val CHAPTER_RESTART_SLACK = 0.01f
        }
    }
