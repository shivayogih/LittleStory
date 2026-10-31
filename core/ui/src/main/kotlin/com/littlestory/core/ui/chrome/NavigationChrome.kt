package com.littlestory.core.ui.chrome

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.littlestory.core.designsystem.brand.BrandMark
import com.littlestory.core.designsystem.component.ButtonVariant
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme

/**
 * Top bar (68 dp): a back button or the brand mark on the start side, an optional centred title
 * and an optional end action. Handles the status-bar inset for edge-to-edge.
 */
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    backContentDescription: String? = null,
    onBack: (() -> Unit)? = null,
    action: @Composable () -> Unit = {},
) {
    val colors = LittleStoryTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .heightIn(min = LittleStoryTheme.dimens.topBarHeight)
            .padding(horizontal = LittleStoryTheme.spacing.screenHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (onBack != null && backContentDescription != null) {
                LittleStoryIconButton(
                    LittleStoryIcons.Back,
                    backContentDescription,
                    onBack,
                    modifier = Modifier.testTag(ChromeTags.BACK),
                )
            } else {
                BrandMark()
            }
        }
        if (title != null) {
            Text(
                title,
                style = LittleStoryTheme.typography.topBarTitle,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) { action() }
    }
}

/** Test tags for the shared chrome, so journey tests can drive it without matching text. */
object ChromeTags {
    const val ACTION_PRIMARY = "action_primary"
    const val ACTION_SECONDARY = "action_secondary"
    const val LOADING = "loading"
    const val BACK = "action_back"

    fun navTab(index: Int) = "nav_tab_$index"
}

/** One bottom-navigation tab. The Create tab is drawn as the raised tile. */
data class NavTab(
    val label: String,
    @DrawableRes val icon: Int,
    val isCreate: Boolean = false,
)

/**
 * Five-tab bottom navigation. The selected tab gets the soft-lavender pill and is announced as
 * selected; labels wrap rather than clip so longer Kannada text stays visible.
 */
@Composable
fun BottomNav(
    tabs: List<NavTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    Column(modifier.fillMaxWidth().background(colors.surface.copy(alpha = dimens.bottomNavAlpha))) {
        Box(Modifier.fillMaxWidth().height(dimens.borderWidth).background(colors.outline))
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.navigationBars,
                ).heightIn(min = dimens.bottomNavHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, tab ->
                val active = index == selectedIndex
                Column(
                    Modifier
                        .weight(1f)
                        .heightIn(min = dimens.minTouchTarget)
                        .selectable(selected = active, role = Role.Tab, onClick = { onSelect(index) })
                        .testTag(ChromeTags.navTab(index)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(
                            LittleStoryTheme.spacing.xxs,
                            Alignment.CenterVertically,
                        ),
                ) {
                    if (tab.isCreate) {
                        CreateTile(tab, active)
                    } else {
                        Box(
                            Modifier
                                .size(dimens.navPillWidth, dimens.navPillHeight)
                                .clip(LittleStoryTheme.shapes.chip)
                                .background(if (active) colors.secondaryContainer else Color.Transparent),
                            contentAlignment = Alignment.Center,
                        ) {
                            LittleStoryIcon(
                                tab.icon,
                                contentDescription = null,
                                tint = if (active) colors.primary else colors.navInactive,
                            )
                        }
                    }
                    Text(
                        tab.label,
                        style = LittleStoryTheme.typography.navLabel,
                        color = if (active) colors.primary else colors.navInactive,
                        textAlign = TextAlign.Center,
                        maxLines = NAV_LABEL_MAX_LINES,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateTile(tab: NavTab, active: Boolean) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val ring = if (active) colors.accent else colors.surface
    Box(
        Modifier
            .border(dimens.createTileRing, ring, LittleStoryTheme.shapes.medium)
            .size(
                dimens.createTileWidth + dimens.createTileRing * 2,
                dimens.createTileHeight + dimens.createTileRing * 2,
            ).padding(dimens.createTileRing)
            .background(colors.primary, LittleStoryTheme.shapes.small),
        contentAlignment = Alignment.Center,
    ) {
        LittleStoryIcon(tab.icon, contentDescription = null, tint = colors.onPrimary)
    }
}

/** Sticky bottom action bar with one primary action and an optional secondary one. */
@Composable
fun StickyActionBar(
    primaryLabel: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    onSecondary: () -> Unit = {},
    primaryEnabled: Boolean = true,
) {
    val colors = LittleStoryTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = LittleStoryTheme.spacing.screenHorizontal, vertical = LittleStoryTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.sm),
    ) {
        LittleStoryButton(
            primaryLabel,
            onPrimary,
            Modifier.testTag(ChromeTags.ACTION_PRIMARY),
            enabled = primaryEnabled,
        )
        if (secondaryLabel != null) {
            LittleStoryButton(
                secondaryLabel,
                onSecondary,
                Modifier.testTag(ChromeTags.ACTION_SECONDARY),
                variant = ButtonVariant.Ghost,
            )
        }
    }
}

// Kannada tab labels may need a second line at large font scales; never more (LS-019).
private const val NAV_LABEL_MAX_LINES = 2
