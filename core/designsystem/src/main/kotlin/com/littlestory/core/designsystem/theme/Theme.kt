package com.littlestory.core.designsystem.theme

import android.provider.Settings
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LocalColors = staticCompositionLocalOf { LightLittleStoryColors }
private val LocalTypography = staticCompositionLocalOf { LatinTypography }
private val LocalShapes = staticCompositionLocalOf { LittleStoryShapes() }
private val LocalSpacing = staticCompositionLocalOf { DefaultSpacing }
private val LocalDimens = staticCompositionLocalOf { LittleStoryDimens() }
private val LocalElevation = staticCompositionLocalOf { LittleStoryElevation() }
private val LocalMotion = staticCompositionLocalOf { LittleStoryMotion() }

/**
 * LittleStory theme: a single light theme with night surfaces (ADR-013). Typography follows the
 * app locale's script; spacing follows the window width; motion honours the system animation scale.
 */
@Composable
fun LittleStoryTheme(
    script: TypographyScript = currentTypographyScript(),
    reducedMotion: Boolean = systemAnimationsDisabled(),
    content: @Composable () -> Unit,
) {
    val colors = LightLittleStoryColors
    val typography = typographyFor(script)
    val compact = LocalConfiguration.current.screenWidthDp.dp <= LittleStoryBreakpoints.CompactMaxWidth
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides typography,
        LocalSpacing provides if (compact) CompactSpacing else DefaultSpacing,
        LocalMotion provides LittleStoryMotion(reduced = reducedMotion),
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography = typography.toMaterialTypography(),
            content = content,
        )
    }
}

/** Accessors for the current theme tokens, e.g. `LittleStoryTheme.colors.primary`. */
object LittleStoryTheme {
    val colors: LittleStoryColors
        @Composable @ReadOnlyComposable
        get() = LocalColors.current
    val typography: LittleStoryTypography
        @Composable @ReadOnlyComposable
        get() = LocalTypography.current
    val shapes: LittleStoryShapes
        @Composable @ReadOnlyComposable
        get() = LocalShapes.current
    val spacing: LittleStorySpacing
        @Composable @ReadOnlyComposable
        get() = LocalSpacing.current
    val dimens: LittleStoryDimens
        @Composable @ReadOnlyComposable
        get() = LocalDimens.current
    val elevation: LittleStoryElevation
        @Composable @ReadOnlyComposable
        get() = LocalElevation.current
    val motion: LittleStoryMotion
        @Composable @ReadOnlyComposable
        get() = LocalMotion.current
}

private const val KANNADA_LANGUAGE = "kn"

/** Picks the Kannada typography when the current locale's language is Kannada. */
@Composable
@ReadOnlyComposable
fun currentTypographyScript(): TypographyScript {
    val locales = LocalConfiguration.current.locales
    val language = if (locales.isEmpty) "" else locales[0].language
    return if (language == KANNADA_LANGUAGE) TypographyScript.Kannada else TypographyScript.Latin
}

@Composable
private fun systemAnimationsDisabled(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

private fun LittleStoryColors.toMaterialColorScheme(): ColorScheme =
    lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = accent,
        background = background,
        onBackground = textPrimary,
        surface = surface,
        onSurface = textPrimary,
        surfaceVariant = surfaceMuted,
        onSurfaceVariant = textSecondary,
        outline = outline,
        outlineVariant = outline,
        error = error,
        onError = onPrimary,
    )

private fun LittleStoryTypography.toMaterialTypography(): Typography =
    Typography(
        displayLarge = displayOnboarding,
        displayMedium = displaySetup,
        headlineLarge = heroTitle,
        headlineMedium = homeTitle,
        titleLarge = brandName,
        titleMedium = topBarTitle,
        bodyLarge = bodyLarge,
        bodyMedium = body,
        labelLarge = button,
        labelMedium = chip,
        labelSmall = navLabel,
    )
