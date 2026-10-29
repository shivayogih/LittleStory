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
