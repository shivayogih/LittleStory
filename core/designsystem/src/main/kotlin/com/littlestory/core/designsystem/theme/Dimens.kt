package com.littlestory.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing scale (4 dp base) and screen padding from design-tokens.json `layout.content-padding`. */
@Immutable
data class LittleStorySpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val screenTop: Dp = 6.dp,
    val screenHorizontal: Dp = 20.dp,
    val screenBottom: Dp = 28.dp,
)

/** Component sizes from design-tokens.json `layout` and docs/design/design-system.md §5. */
@Immutable
data class LittleStoryDimens(
    val buttonMinHeight: Dp = 52.dp,
    val iconButtonVisual: Dp = 42.dp,
    val minTouchTarget: Dp = 48.dp,
    val iconSize: Dp = 24.dp,
    val iconSizeSmall: Dp = 18.dp,
    val topBarHeight: Dp = 68.dp,
    val bottomNavHeight: Dp = 76.dp,
    val inputHeight: Dp = 48.dp,
    val shellMaxWidth: Dp = 430.dp,
    val borderWidth: Dp = 1.dp,
    val selectedBorderWidth: Dp = 2.dp,
    val focusRingWidth: Dp = 3.dp,
    val toggleWidth: Dp = 43.dp,
    val toggleHeight: Dp = 25.dp,
    val toggleKnob: Dp = 19.dp,
    val toggleKnobInset: Dp = 3.dp,
    val segmentedInset: Dp = 4.dp,
    val readingProgressHeight: Dp = 3.dp,
    val progressBarHeight: Dp = 8.dp,
    val sheetHandleWidth: Dp = 40.dp,
    val sheetHandleHeight: Dp = 4.dp,
    val dialogIconSize: Dp = 56.dp,
    val emptyStateIconSize: Dp = 72.dp,
    /** Proposed: the prototype dims behind sheets but the alpha is not in the tokens (confirm with design). */
    val scrimAlpha: Float = 0.5f,
    val storyCardWidth: Dp = 150.dp,
    val storyCardWideWidth: Dp = 220.dp,
    // 156 × 180 px in the 2× design screenshots (LS-124); 156 dp was a px-for-dp slip.
    val listArtWidth: Dp = 78.dp,
    val listArtHeight: Dp = 90.dp,
    val avatarSize: Dp = 56.dp,
    val avatarLargeSize: Dp = 72.dp,
    /** Dark translucent hill on story covers (design `#18243047`: alpha 0x47). */
    val storyHillAlpha: Float = 0.28f,
    val navPillWidth: Dp = 56.dp,
    val navPillHeight: Dp = 32.dp,
    val createTileWidth: Dp = 48.dp,
    val createTileHeight: Dp = 40.dp,
    val createTileRing: Dp = 5.dp,
    /** Bottom bar is drawn as white at 96 % (`#FFFFFFF5`) over the content. */
    val bottomNavAlpha: Float = 0.96f,
    val pagerDotSize: Dp = 7.dp,
    val pagerDotActiveWidth: Dp = 24.dp,
    /** Height of the night illustration in the component catalogue (debug only). */
    val catalogSceneHeight: Dp = 260.dp,
    val categoryTileSize: Dp = 58.dp,
    val categoryTileItemWidth: Dp = 72.dp,
    val heroSceneWidth: Dp = 130.dp,
    val heroMinHeight: Dp = 240.dp,
    val cultureArtWidth: Dp = 96.dp,
    val cultureArtHeight: Dp = 84.dp,
    val homeAvatarSize: Dp = 42.dp,
    val detailCoverHeight: Dp = 355.dp,
    val detailSheetOverlap: Dp = 32.dp,
    val categoryHeroHeight: Dp = 220.dp,
    val categoryHeroArtWidth: Dp = 120.dp,
    val featuredArtWidth: Dp = 110.dp,
    // Create, Generating, Story Ready (SCR-07..09)
    val createOrbSize: Dp = 58.dp,
    val sparkleSize: Dp = 16.dp,
    val promptMinHeight: Dp = 120.dp,
    val magicBookWidth: Dp = 200.dp,
    val magicBookHeight: Dp = 120.dp,
    val stageDiscSize: Dp = 32.dp,
    val readyCoverHeight: Dp = 300.dp,
    val peekRuleWidth: Dp = 3.dp,
    // Reader and Player (SCR-10, SCR-11)
    val readerCoverHeight: Dp = 180.dp,
    val readerPlayButtonSize: Dp = 56.dp,
    val playerCoverWidth: Dp = 265.dp,
    val playerCoverHeight: Dp = 290.dp,
    val playerCoverCompactWidth: Dp = 220.dp,
    val playerCoverCompactHeight: Dp = 235.dp,
    val playerPlayButtonSize: Dp = 76.dp,
    // My Stories, Downloads, Settings (SCR-12..18)
    val recentArtSize: Dp = 64.dp,
    val roundPlaySize: Dp = 44.dp,
    val settingsIconTile: Dp = 40.dp,
    val dayButtonSize: Dp = 40.dp,
    val unreadDotSize: Dp = 8.dp,
    val reminderMoonSize: Dp = 72.dp,
    val subscriptionMoonSize: Dp = 96.dp,
    /** Disabled controls are drawn at 45 % opacity. */
    val disabledAlpha: Float = 0.45f,
)

/** Window-size thresholds (design-tokens.json `layout.breakpoints`), applied via window size, not ad-hoc checks. */
object LittleStoryBreakpoints {
    val CompactMaxWidth: Dp = 360.dp
    val TallMinHeight: Dp = 900.dp
}

internal val DefaultSpacing = LittleStorySpacing()

/** Compact phones (<= 360 dp wide) use 16 dp side padding. */
internal val CompactSpacing = LittleStorySpacing(screenHorizontal = 16.dp)
