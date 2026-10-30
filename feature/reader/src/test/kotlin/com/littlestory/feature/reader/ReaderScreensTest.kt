package com.littlestory.feature.reader

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.feature.reader.player.PlayerCallbacks
import com.littlestory.feature.reader.player.PlayerScreen
import com.littlestory.feature.reader.player.PlayerTags
import com.littlestory.feature.reader.reader.ReaderCallbacks
import com.littlestory.feature.reader.reader.ReaderScreen
import com.littlestory.feature.reader.reader.ReaderTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en-w430dp-h932dp-xxhdpi")
class ReaderScreensTest {
    @get:Rule
    val rule = createComposeRule()

    private val calls = mutableListOf<String>()

    @Test
    fun readerRoutesPagingTextSizeAndPlay() {
        rule.setContent {
            LittleStoryTheme {
                ReaderScreen(
                    readerState(page = 1),
                    ReaderCallbacks(
                        onPlay = { calls += "play:$it" },
                        onPrevious = { calls += "previous" },
                        onNext = { calls += "next" },
                        onSmaller = { calls += "smaller" },
                        onLarger = { calls += "larger" },
                        onFavourite = { calls += "fav" },
                    ),
                )
            }
        }
        rule.onNodeWithTag(ReaderTags.PREVIOUS).performClick()
        rule.onNodeWithTag(ReaderTags.NEXT).performClick()
        rule.onNodeWithTag(ReaderTags.SMALLER).performClick()
        rule.onNodeWithTag(ReaderTags.LARGER).performClick()
        rule.onNodeWithTag(ReaderTags.FAVOURITE).performClick()
        rule.onNodeWithTag(ReaderTags.PLAY).performClick()
        assertEquals(listOf("previous", "next", "smaller", "larger", "fav", "play:s1"), calls)
    }

    @Test
    fun readerDisablesPreviousOnTheFirstPage() {
        rule.setContent { LittleStoryTheme { ReaderScreen(readerState(page = 0), ReaderCallbacks()) } }
        rule.onNodeWithTag(ReaderTags.PREVIOUS).assertIsNotEnabled()
    }

    @Test
    fun playerRoutesControls() {
        rule.setContent {
            LittleStoryTheme {
                PlayerScreen(
                    playerState,
                    PlayerCallbacks(
                        onStoryText = { calls += "text:$it" },
                        onPlayPause = { calls += "playpause" },
                        onRewind = { calls += "rewind" },
                        onForward = { calls += "forward" },
                        onPreviousChapter = { calls += "prevchapter" },
                        onNextChapter = { calls += "nextchapter" },
                        onSleepTimer = { calls += "sleep" },
                        onSpeed = { calls += "speed" },
                    ),
                )
            }
        }
        listOf(
            PlayerTags.PLAY_PAUSE,
            PlayerTags.REWIND,
            PlayerTags.FORWARD,
            PlayerTags.PREVIOUS_CHAPTER,
            PlayerTags.NEXT_CHAPTER,
            PlayerTags.SLEEP,
            PlayerTags.SPEED,
            PlayerTags.STORY_TEXT,
        ).forEach { rule.onNodeWithTag(it).performClick() }
        assertEquals(
            listOf("playpause", "rewind", "forward", "prevchapter", "nextchapter", "sleep", "speed", "text:s1"),
            calls,
        )
    }
}
