package com.littlestory.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.littlestory.core.designsystem.R

/**
 * Text styles from docs/design/design-system.md §4. Sizes are sp so system font scale applies.
 * Two sets exist: the design set (DM Sans + Manrope, Latin) and the Kannada set (Noto Sans Kannada
 * with the larger line heights from docs/localization/kannada-typography.md §3).
 */
@Immutable
data class LittleStoryTypography(
    val displayOnboarding: TextStyle,
    val displaySetup: TextStyle,
    val heroTitle: TextStyle,
    val homeTitle: TextStyle,
    val topBarTitle: TextStyle,
    val brandName: TextStyle,
    val bodyLarge: TextStyle,
    val body: TextStyle,
    val readerBody: TextStyle,
    val button: TextStyle,
    val chip: TextStyle,
    val eyebrow: TextStyle,
    val heroKicker: TextStyle,
    val navLabel: TextStyle,
    /** Eyebrows and kickers are uppercased only for scripts that have case. */
    val uppercaseEyebrows: Boolean,
)

/** Which typography set to use; chosen from the app locale's script. */
enum class TypographyScript { Latin, Kannada }

// Variable fonts: one file per family, weight selected through the 'wght' axis (API 26+).
@OptIn(ExperimentalTextApi::class)
private fun variableFamily(resId: Int, weights: List<FontWeight>): FontFamily = FontFamily(
    weights.map { weight ->
        Font(
            resId = resId,
            weight = weight,
            variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
        )
    },
)

private val ExtraBold = FontWeight.ExtraBold

internal val DmSans = variableFamily(
    R.font.dm_sans,
    listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold, ExtraBold),
)
internal val Manrope = variableFamily(
    R.font.manrope,
    listOf(FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold, ExtraBold),
)
internal val NotoSansKannada = variableFamily(
    R.font.noto_sans_kannada,
    listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold, ExtraBold),
)

private fun style(
    family: FontFamily,
    weight: FontWeight,
    size: TextUnit,
    lineHeight: TextUnit = TextUnit.Unspecified,
    tracking: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = tracking,
)

// CSS letter-spacing in px converted to em at the style's size (1 CSS px = 1 dp/sp, design-system.md §1).
internal val LatinTypography = LittleStoryTypography(
    displayOnboarding = style(Manrope, ExtraBold, 29.sp, (29 * 1.12).sp, (-0.8 / 29).em),
    displaySetup = style(Manrope, ExtraBold, 29.sp, (29 * 1.18).sp, (-0.8 / 29).em),
    heroTitle = style(Manrope, ExtraBold, 25.sp, (25 * 1.12).sp, (-0.7 / 25).em),
    homeTitle = style(Manrope, ExtraBold, 21.sp, tracking = (-0.5 / 21).em),
    topBarTitle = style(Manrope, ExtraBold, 17.sp),
    brandName = style(Manrope, ExtraBold, 18.sp, tracking = (-0.5 / 18).em),
    bodyLarge = style(DmSans, FontWeight.Normal, 15.sp, (15 * 1.55).sp),
    // body and button sizes are not in design-tokens.json: proposed values, confirm in LS-016.
    body = style(DmSans, FontWeight.Normal, 14.sp, (14 * 1.5).sp),
    readerBody = style(Manrope, FontWeight.Medium, 16.sp, (16 * 1.95).sp),
    button = style(DmSans, FontWeight.Bold, 15.sp),
    chip = style(DmSans, FontWeight.SemiBold, 12.sp),
    eyebrow = style(DmSans, ExtraBold, 10.sp, tracking = (1.6 / 10).em),
    heroKicker = style(DmSans, ExtraBold, 9.sp, tracking = (1.4 / 9).em),
    navLabel = style(DmSans, FontWeight.SemiBold, 10.sp),
    uppercaseEyebrows = true,
)

// Kannada: no negative tracking, line height >= 1.4 for titles, eyebrow/kicker >= 11 sp, no case transform.
internal val KannadaTypography = LittleStoryTypography(
    displayOnboarding = style(NotoSansKannada, FontWeight.Bold, 29.sp, (29 * 1.4).sp),
    displaySetup = style(NotoSansKannada, FontWeight.Bold, 29.sp, (29 * 1.4).sp),
    heroTitle = style(NotoSansKannada, FontWeight.Bold, 25.sp, (25 * 1.4).sp),
    homeTitle = style(NotoSansKannada, FontWeight.Bold, 21.sp, (21 * 1.4).sp),
    topBarTitle = style(NotoSansKannada, FontWeight.Bold, 17.sp, (17 * 1.4).sp),
    brandName = style(NotoSansKannada, FontWeight.Bold, 18.sp, (18 * 1.4).sp),
    bodyLarge = style(NotoSansKannada, FontWeight.Normal, 15.sp, (15 * 1.7).sp),
    body = style(NotoSansKannada, FontWeight.Normal, 14.sp, (14 * 1.7).sp),
    readerBody = style(NotoSansKannada, FontWeight.Medium, 16.sp, (16 * 2.0).sp),
    button = style(NotoSansKannada, FontWeight.Bold, 15.sp, (15 * 1.5).sp),
    chip = style(NotoSansKannada, FontWeight.SemiBold, 12.sp, (12 * 1.5).sp),
    eyebrow = style(NotoSansKannada, FontWeight.Bold, 11.sp, (11 * 1.5).sp),
    heroKicker = style(NotoSansKannada, FontWeight.Bold, 11.sp, (11 * 1.5).sp),
    navLabel = style(NotoSansKannada, FontWeight.SemiBold, 11.sp, (11 * 1.5).sp),
    uppercaseEyebrows = false,
)

fun typographyFor(script: TypographyScript): LittleStoryTypography = when (script) {
    TypographyScript.Latin -> LatinTypography
    TypographyScript.Kannada -> KannadaTypography
}
