package com.littlestory.feature.reader.player

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.designsystem.component.LittleStoryBottomSheet
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.component.LittleStoryPlaybackSlider
import com.littlestory.core.designsystem.component.SheetOption
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryBreakpoints
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.illustration.NightSky
import com.littlestory.core.ui.story.StoryArt
import com.littlestory.core.ui.story.storyArtFor
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.domain.model.SleepTimer
import com.littlestory.feature.reader.R
import java.text.NumberFormat
import com.littlestory.core.ui.R as UiR

object PlayerTags {
    const val PLAY_PAUSE = "player_play_pause"
    const val ELAPSED = "player_elapsed"
    const val REWIND = "player_rewind"
    const val FORWARD = "player_forward"
    const val PREVIOUS_CHAPTER = "player_previous_chapter"
    const val NEXT_CHAPTER = "player_next_chapter"
    const val SLEEP = "player_sleep"
    const val DOWNLOAD = "player_download"
    const val FAVOURITE = "player_favourite"
    const val SPEED = "player_speed"
    const val STORY_TEXT = "player_story_text"
    const val SIMULATED = "player_simulated"
}

private const val SECONDS_PER_MINUTE = 60
private const val VOLUME_STEPS = 100f

data class PlayerCallbacks(
    val onBack: () -> Unit = {},
    val onStoryText: (storyId: String) -> Unit = {},
    val onPlayPause: () -> Unit = {},
    val onSeek: (Float) -> Unit = {},
    val onRewind: () -> Unit = {},
    val onForward: () -> Unit = {},
    val onPreviousChapter: () -> Unit = {},
    val onNextChapter: () -> Unit = {},
    val onSleepTimer: () -> Unit = {},
    val onSleepSelected: (SleepTimer) -> Unit = {},
    val onSleepDismiss: () -> Unit = {},
    val onDownload: () -> Unit = {},
    val onFavourite: () -> Unit = {},
    val onSpeed: () -> Unit = {},
    val onVolume: (Int) -> Unit = {},
)

@Composable
fun PlayerRoute(onBack: () -> Unit, onStoryText: (String) -> Unit, viewModel: PlayerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PlayerScreen(
        state,
        PlayerCallbacks(
            onBack = onBack,
            onStoryText = onStoryText,
            onPlayPause = viewModel::onPlayPause,
            onSeek = viewModel::onSeek,
            onRewind = viewModel::onRewind,
            onForward = viewModel::onForward,
            onPreviousChapter = viewModel::onPreviousChapter,
            onNextChapter = viewModel::onNextChapter,
            onSleepTimer = viewModel::onSleepTimerClick,
            onSleepSelected = viewModel::onSleepTimerSelected,
            onSleepDismiss = viewModel::onSleepSheetDismiss,
            onDownload = viewModel::onDownloadClick,
            onFavourite = viewModel::onFavouriteClick,
            onSpeed = viewModel::onSpeedClick,
            onVolume = viewModel::onVolumeChange,
        ),
    )
}

@Composable
fun PlayerScreen(state: PlayerUiState, callbacks: PlayerCallbacks, modifier: Modifier = Modifier) {
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = false)
    NightSky(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.xl),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LittleStoryIconButton(LittleStoryIcons.Back, stringResource(UiR.string.action_back), callbacks.onBack)
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (!state.notFound && !state.loading) StoryTextLink { callbacks.onStoryText(state.storyId) }
                }
                Box(Modifier.size(LittleStoryTheme.dimens.minTouchTarget))
            }
            when {
                state.loading -> Unit
                state.notFound ->
                    LittleStoryEmptyState(
                        icon = LittleStoryIcons.Book,
                        title = stringResource(R.string.reader_not_found_title),
                        text = stringResource(R.string.reader_not_found_text),
                    )
                else -> Loaded(state, callbacks)
            }
        }
    }
    if (state.sleepSheetOpen) SleepTimerSheet(state.sleepTimer, callbacks)
}

@Composable
private fun StoryTextLink(onClick: () -> Unit) {
    val colors = LittleStoryTheme.colors
    Row(
        Modifier
            .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
            .clip(LittleStoryTheme.shapes.chip)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = LittleStoryTheme.spacing.md)
            .testTag(PlayerTags.STORY_TEXT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.sm),
    ) {
        LittleStoryIcon(LittleStoryIcons.Book, contentDescription = null, tint = colors.onNight)
        Text(
            stringResource(R.string.player_story_text),
            style = LittleStoryTheme.typography.bodyLarge,
            color = colors.onNight,
        )
    }
}

@Composable
private fun Loaded(state: PlayerUiState, callbacks: PlayerCallbacks) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    Text(
        if (state.heroName != null) {
            stringResource(R.string.player_now_playing_for, state.heroName)
        } else {
            stringResource(R.string.player_now_playing)
        },
        style = LittleStoryTheme.typography.heroKicker,
        color = colors.accent,
        textAlign = TextAlign.Center,
    )
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val compact = maxWidth <= LittleStoryBreakpoints.CompactMaxWidth
        val (gradient, motif) = storyArtFor(state.paletteId, state.motifId)
        Box(
            Modifier
                .size(
                    if (compact) dimens.playerCoverCompactWidth else dimens.playerCoverWidth,
                    if (compact) dimens.playerCoverCompactHeight else dimens.playerCoverHeight,
                ).clip(LittleStoryTheme.shapes.hero),
        ) { StoryArt(gradient, motif) }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text(
            state.title,
            style = LittleStoryTheme.typography.heroTitle,
            color = colors.onNight,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.player_language_narration, state.languageName),
            style = LittleStoryTheme.typography.chip,
            color = colors.secondaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag(PlayerTags.SIMULATED),
        )
    }
    Column(Modifier.fillMaxWidth()) {
        LittleStoryPlaybackSlider(
            value = state.position,
            onValueChange = callbacks.onSeek,
            contentDescription = stringResource(R.string.player_seek),
        )
        Row(Modifier.fillMaxWidth()) {
            Text(
                formatTime(state.elapsedSeconds),
                style = LittleStoryTheme.typography.chip,
                color = colors.secondaryContainer,
                modifier = Modifier.weight(1f).testTag(PlayerTags.ELAPSED),
            )
            Text(
                formatTime(state.totalSeconds),
                style = LittleStoryTheme.typography.chip,
                color = colors.secondaryContainer,
            )
        }
    }
    Controls(state, callbacks)
    Box(Modifier.fillMaxWidth().height(dimens.borderWidth).background(colors.nightSurface))
    SecondaryRow(state, callbacks)
    VolumeRow(state.volumePercent, callbacks.onVolume)
}

@Composable
private fun formatTime(totalSeconds: Int): String {
    val format = NumberFormat.getIntegerInstance()
    val minutes = format.format(totalSeconds / SECONDS_PER_MINUTE)
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    return stringResource(R.string.player_time, minutes, seconds)
}

@Composable
private fun Controls(state: PlayerUiState, callbacks: PlayerCallbacks) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val skipSeconds = AppConfig.Player.SKIP_SECONDS
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NightIconButton(
            LittleStoryIcons.Back,
            stringResource(R.string.player_previous_chapter),
            callbacks.onPreviousChapter,
            PlayerTags.PREVIOUS_CHAPTER,
        )
        NightIconButton(
            LittleStoryIcons.Rewind15,
            pluralStringResource(R.plurals.player_rewind, skipSeconds, skipSeconds),
            callbacks.onRewind,
            PlayerTags.REWIND,
        )
        val playLabel = stringResource(if (state.playing) R.string.player_pause else R.string.player_play)
        Box(
            Modifier
                .size(dimens.playerPlayButtonSize)
                .clip(LittleStoryTheme.shapes.iconButton)
                .background(colors.accent)
                .clickable(role = Role.Button, onClick = callbacks.onPlayPause)
                .semantics { contentDescription = playLabel }
                .testTag(PlayerTags.PLAY_PAUSE),
            contentAlignment = Alignment.Center,
        ) {
            LittleStoryIcon(
                if (state.playing) LittleStoryIcons.Pause else LittleStoryIcons.Play,
                contentDescription = null,
                tint = colors.night,
            )
        }
        NightIconButton(
            LittleStoryIcons.Forward15,
            pluralStringResource(R.plurals.player_forward, skipSeconds, skipSeconds),
            callbacks.onForward,
            PlayerTags.FORWARD,
        )
        NightIconButton(
            LittleStoryIcons.Chevron,
            stringResource(R.string.player_next_chapter),
            callbacks.onNextChapter,
            PlayerTags.NEXT_CHAPTER,
            enabled = state.chapter < state.chapters - 1,
        )
    }
}

@Composable
private fun NightIconButton(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit,
    tag: String,
    enabled: Boolean = true,
) {
    val dimens = LittleStoryTheme.dimens
    Box(
        Modifier
            .size(dimens.minTouchTarget)
            .clip(LittleStoryTheme.shapes.iconButton)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .testTag(tag),
        contentAlignment = Alignment.Center,
    ) {
        LittleStoryIcon(
            icon,
            contentDescription = null,
            tint = LittleStoryTheme.colors.onNight.copy(alpha = if (enabled) 1f else dimens.disabledAlpha),
        )
    }
}

@Composable
private fun SecondaryRow(state: PlayerUiState, callbacks: PlayerCallbacks) {
    val sleepLabel =
        when (val timer = state.sleepTimer) {
            SleepTimer.Off -> stringResource(R.string.player_sleep_timer)
            SleepTimer.EndOfStory -> stringResource(R.string.player_sleep_end_of_story)
            is SleepTimer.Minutes -> pluralStringResource(R.plurals.player_sleep_minutes, timer.value, timer.value)
        }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        SecondaryAction(
            LittleStoryIcons.Moon,
            sleepLabel,
            state.sleepTimer != SleepTimer.Off,
            callbacks.onSleepTimer,
            PlayerTags.SLEEP,
            Modifier.weight(1f),
        )
        SecondaryAction(
            if (state.downloaded) LittleStoryIcons.DownloadFilled else LittleStoryIcons.Download,
            stringResource(if (state.downloaded) R.string.player_downloaded else R.string.player_download),
            state.downloaded,
            callbacks.onDownload,
            PlayerTags.DOWNLOAD,
            Modifier.weight(1f),
        )
        SecondaryAction(
            if (state.favourite) LittleStoryIcons.HeartFilled else LittleStoryIcons.Heart,
            stringResource(if (state.favourite) R.string.player_loved else R.string.player_favourite),
            state.favourite,
            callbacks.onFavourite,
            PlayerTags.FAVOURITE,
            Modifier.weight(1f),
        )
        SpeedAction(state.speed, callbacks.onSpeed, Modifier.weight(1f))
    }
}

@Composable
private fun SecondaryAction(
    @DrawableRes icon: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier,
) {
    val colors = LittleStoryTheme.colors
    val on = stringResource(R.string.player_state_on)
    val off = stringResource(R.string.player_state_off)
    Column(
        modifier
            .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
            .clip(LittleStoryTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { stateDescription = if (selected) on else off }
            .padding(LittleStoryTheme.spacing.xs)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs),
    ) {
        LittleStoryIcon(
            icon,
            contentDescription = null,
            tint = if (selected) colors.highlight else colors.secondaryContainer,
        )
        Text(
            label,
            style = LittleStoryTheme.typography.chip,
            color = colors.secondaryContainer,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SpeedAction(speed: Float, onClick: () -> Unit, modifier: Modifier) {
    val colors = LittleStoryTheme.colors
    val format = NumberFormat.getNumberInstance()
    val speedText = stringResource(R.string.player_speed_value, format.format(speed))
    val description = stringResource(R.string.player_speed_description, format.format(speed))
    Column(
        modifier
            .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
            .clip(LittleStoryTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { contentDescription = description }
            .padding(LittleStoryTheme.spacing.xs)
            .testTag(PlayerTags.SPEED),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs),
    ) {
        Text(speedText, style = LittleStoryTheme.typography.bodyLarge, color = colors.secondaryContainer)
        Text(
            stringResource(R.string.player_speed),
            style = LittleStoryTheme.typography.chip,
            color = colors.secondaryContainer,
        )
    }
}

@Composable
private fun VolumeRow(volume: Int, onVolume: (Int) -> Unit) {
    val colors = LittleStoryTheme.colors
    val label = stringResource(R.string.player_volume)
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.md),
    ) {
        LittleStoryIcon(LittleStoryIcons.Volume, contentDescription = null, tint = colors.secondaryContainer)
        Slider(
            value = volume / VOLUME_STEPS,
            onValueChange = { onVolume((it * VOLUME_STEPS).toInt()) },
            modifier = Modifier.weight(1f).semantics { contentDescription = label },
            colors =
                SliderDefaults.colors(
                    thumbColor = colors.accent,
                    activeTrackColor = colors.accent,
                    inactiveTrackColor = colors.nightSurface,
                ),
        )
    }
}

@Composable
private fun SleepTimerSheet(current: SleepTimer, callbacks: PlayerCallbacks) {
    val options =
        AppConfig.Player.sleepTimerMinutes.map { minutes ->
            SheetOption(
                label = pluralStringResource(R.plurals.player_sleep_minutes, minutes, minutes),
                selected = current == SleepTimer.Minutes(minutes),
                onClick = {
                    callbacks.onSleepSelected(
                        SleepTimer.Minutes(minutes).takeUnless { it == current } ?: SleepTimer.Off,
                    )
                },
            )
        } +
            SheetOption(
                label = stringResource(R.string.player_sleep_end_of_story),
                selected = current == SleepTimer.EndOfStory,
                onClick = {
                    callbacks.onSleepSelected(
                        SleepTimer.EndOfStory.takeUnless { it == current } ?: SleepTimer.Off,
                    )
                },
            )
    LittleStoryBottomSheet(
        title = stringResource(R.string.player_sleep_title),
        subtitle = stringResource(R.string.player_sleep_body),
        options = options,
        cancelLabel = stringResource(R.string.player_sleep_cancel),
        onDismiss = callbacks.onSleepDismiss,
    )
}
