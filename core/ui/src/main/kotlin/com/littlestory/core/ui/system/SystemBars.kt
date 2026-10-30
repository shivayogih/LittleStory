package com.littlestory.core.ui.system

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Sets status and navigation bar icon colour for the current screen (LS-052): dark icons on cream
 * screens, light icons on night screens (Onboarding, Generating, Player, Subscription hero). The
 * previous style is restored when the screen leaves composition.
 */
@Composable
fun SystemBarIcons(darkIcons: Boolean, darkNavigationIcons: Boolean = darkIcons) {
    val view = LocalView.current
    if (view.isInEditMode) return
    DisposableEffect(view, darkIcons, darkNavigationIcons) {
        val window = view.context.findActivity()?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val previousStatus = controller?.isAppearanceLightStatusBars
        val previousNavigation = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = darkIcons
        controller?.isAppearanceLightNavigationBars = darkNavigationIcons
        onDispose {
            if (previousStatus != null) controller.isAppearanceLightStatusBars = previousStatus
            if (previousNavigation != null) controller.isAppearanceLightNavigationBars = previousNavigation
        }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
