package com.littlestory.feature.reader.reader

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.isSpecified
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.common.story.StoryImageFiles
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.component.LittleStoryReadingProgress
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.chrome.LoadingIndicator
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.illustration.Sparkle
import com.littlestory.core.ui.story.StoryArt
import com.littlestory.core.ui.story.StoryPicture
import com.littlestory.core.ui.story.rememberPictureExists
import com.littlestory.core.ui.story.storyArtFor
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.feature.reader.R
import com.littlestory.core.ui.R as UiR

object ReaderTags {
    const val PAGE = "reader_page"
    const val TEXT = "reader_text"
    const val PREVIOUS = "reader_previous"
    const val NEXT = "reader_next"
    const val PLAY = "reader_play"
    const val SMALLER = "reader_smaller"
    const val LARGER = "reader_larger"
    const val FAVOURITE = "reader_favourite"
    const val NOT_FOUND = "reader_not_found"
}

// The back chevron points to the start edge; turned half a turn it points to "next".
private const val NEXT_ROTATION_DEGREES = 180f

data class ReaderCallbacks(
    val onBack: () -> Unit = {},
    val onPlay: (storyId: String) -> Unit = {},
    val onPrevious: () -> Unit = {},
    val onNext: () -> Unit = {},
    val onSmaller: () -> Unit = {},
    val onLarger: () -> Unit = {},
    val onFavourite: () -> Unit = {},
)

@Composable
fun ReaderRoute(onBack: () -> Unit, onPlay: (String) -> Unit, viewModel: ReaderViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReaderScreen(
        state,
        ReaderCallbacks(
            onBack = onBack,
            onPlay = onPlay,
            onPrevious = viewModel::onPrevious,
            onNext = viewModel::onNext,
            onSmaller = viewModel::onTextSmaller,
            onLarger = viewModel::onTextLarger,
            onFavourite = viewModel::onFavouriteClick,
        ),
    )
}

@Composable
fun ReaderScreen(state: ReaderUiState, callbacks: ReaderCallbacks, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    SystemBarIcons(darkIcons = true)
    Column(modifier.fillMaxSize().background(colors.readerBackground)) {
        val loaded = (state as? ReaderUiState.Loaded)?.content
        TopBar(
            title = stringResource(R.string.reader_title),
            backContentDescription = stringResource(UiR.string.action_back),
            onBack = callbacks.onBack,
            action = {
                if (loaded != null) {
                    LittleStoryIconButton(
                        icon = if (loaded.favourite) LittleStoryIcons.HeartFilled else LittleStoryIcons.Heart,
                        contentDescription =
                            stringResource(
                                if (loaded.favourite) {
                                    UiR.string.story_favourite_remove
                                } else {
                                    UiR.string.story_favourite_add
                                },
                                loaded.title,
                            ),
                        onClick = callbacks.onFavourite,
                        active = loaded.favourite,
                        modifier = Modifier.testTag(ReaderTags.FAVOURITE),
                    )
                }
            },
        )
        when (state) {
            ReaderUiState.Loading -> LoadingIndicator(Modifier.weight(1f))
            ReaderUiState.NotFound ->
                LittleStoryEmptyState(
                    icon = LittleStoryIcons.Book,
                    title = stringResource(R.string.reader_not_found_title),
                    text = stringResource(R.string.reader_not_found_text),
                    modifier = Modifier.testTag(ReaderTags.NOT_FOUND),
                )
            is ReaderUiState.Loaded -> Loaded(state.content, callbacks)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.Loaded(content: ReaderContent, callbacks: ReaderCallbacks) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val motion = LittleStoryTheme.motion
    val scale by animateFloatAsState(
        content.textScale,
        tween(motion.duration(motion.readerFontSizeMillis)),
        label = "textScale",
    )
    val progress by animateFloatAsState(
        (content.page + 1f) / content.pageCount,
        tween(motion.duration(motion.readingProgressMillis)),
        label = "readingProgress",
    )
    Row(
        Modifier.fillMaxWidth().padding(horizontal = spacing.screenHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(
            stringResource(R.string.reader_page_of, content.page + 1, content.pageCount),
            style = LittleStoryTheme.typography.chip,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f).testTag(ReaderTags.PAGE),
        )
        SizeButton(
            R.string.reader_text_smaller_label,
            R.string.reader_text_smaller,
            content.canShrink,
            callbacks.onSmaller,
            ReaderTags.SMALLER,
        )
        SizeButton(
            R.string.reader_text_larger_label,
            R.string.reader_text_larger,
            content.canGrow,
            callbacks.onLarger,
            ReaderTags.LARGER,
        )
    }
    LittleStoryReadingProgress(progress, Modifier.padding(top = spacing.sm))
    Column(
        Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.screenHorizontal, vertical = spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            content.title,
            style = LittleStoryTheme.typography.heroTitle,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        // Generated stories may have a picture per page (debug prototype); otherwise page 1 shows the cover.
        val pagePicture = StoryImageFiles.page(LocalContext.current.filesDir, content.id, content.page)
        if (rememberPictureExists(pagePicture)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(LittleStoryTheme.dimens.readerCoverHeight)
                    .clip(LittleStoryTheme.shapes.large),
            ) { StoryPicture(pagePicture, Modifier.fillMaxSize()) }
        } else if (content.page == 0) {
            val (gradient, motif) = storyArtFor(content.paletteId, content.motifId)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(LittleStoryTheme.dimens.readerCoverHeight)
                    .clip(LittleStoryTheme.shapes.large),
            ) { StoryArt(gradient, motif, storyId = content.id) }
        }
        val body = LittleStoryTheme.typography.readerBody
        Text(
            content.pages[content.page],
            style =
                body.copy(
                    fontSize = body.fontSize * scale,
                    lineHeight = if (body.lineHeight.isSpecified) body.lineHeight * scale else body.lineHeight,
                ),
            color = colors.textReader,
            modifier = Modifier.fillMaxWidth().testTag(ReaderTags.TEXT),
        )
        Sparkle(color = colors.accent)
    }
    BottomBar(content, callbacks)
}

@Composable
private fun SizeButton(label: Int, description: Int, enabled: Boolean, onClick: () -> Unit, tag: String) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val text = stringResource(description)
    Box(
        Modifier
            .size(dimens.minTouchTarget)
            .alpha(if (enabled) 1f else dimens.disabledAlpha)
            .clip(LittleStoryTheme.shapes.small)
            .border(dimens.borderWidth, colors.outline, LittleStoryTheme.shapes.small)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = text }
            .testTag(tag),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(label), style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
    }
}

@Composable
private fun BottomBar(content: ReaderContent, callbacks: ReaderCallbacks) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val dimens = LittleStoryTheme.dimens
    Column(Modifier.fillMaxWidth().background(colors.readerBackground)) {
        Box(Modifier.fillMaxWidth().height(dimens.borderWidth).background(colors.outline))
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PageButton(
                R.string.reader_previous,
                content.canGoBack,
                callbacks.onPrevious,
                ReaderTags.PREVIOUS,
                next = false,
                Modifier.weight(1f),
            )
            val play = stringResource(R.string.reader_play)
            Box(
                Modifier
                    .size(dimens.readerPlayButtonSize)
                    .clip(LittleStoryTheme.shapes.iconButton)
                    .background(colors.primary)
                    .clickable(role = Role.Button) { callbacks.onPlay(content.id) }
                    .semantics { contentDescription = play }
                    .testTag(ReaderTags.PLAY),
                contentAlignment = Alignment.Center,
            ) { LittleStoryIcon(LittleStoryIcons.Play, contentDescription = null, tint = colors.onPrimary) }
            PageButton(
                R.string.reader_next,
                content.canGoForward,
                callbacks.onNext,
                ReaderTags.NEXT,
                next = true,
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PageButton(
    label: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    tag: String,
    next: Boolean,
    modifier: Modifier,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    Column(
        modifier
            .heightIn(min = dimens.minTouchTarget)
            .widthIn(min = dimens.minTouchTarget)
            .alpha(if (enabled) 1f else dimens.disabledAlpha)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LittleStoryIcon(
            LittleStoryIcons.Back,
            contentDescription = null,
            tint = colors.primary,
            modifier = if (next) Modifier.rotate(NEXT_ROTATION_DEGREES) else Modifier,
        )
        Text(stringResource(label), style = LittleStoryTheme.typography.chip, color = colors.primary)
    }
}
