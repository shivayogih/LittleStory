package com.littlestory.core.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.littlestory.core.designsystem.brand.BrandMark
import com.littlestory.core.designsystem.brand.BrandMarkVariant
import com.littlestory.core.designsystem.component.ButtonVariant
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.component.LittleStoryChip
import com.littlestory.core.designsystem.component.LittleStoryEmptyState
import com.littlestory.core.designsystem.component.LittleStoryGenerationProgress
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.component.LittleStoryNotesBanner
import com.littlestory.core.designsystem.component.LittleStoryReadingProgress
import com.littlestory.core.designsystem.component.LittleStorySearchField
import com.littlestory.core.designsystem.component.LittleStorySegmentedControl
import com.littlestory.core.designsystem.component.LittleStoryStorageBar
import com.littlestory.core.designsystem.component.LittleStoryTabs
import com.littlestory.core.designsystem.component.LittleStoryTextField
import com.littlestory.core.designsystem.component.LittleStoryToggle
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.R
import com.littlestory.core.ui.chrome.BottomNav
import com.littlestory.core.ui.chrome.NavTab
import com.littlestory.core.ui.chrome.StickyActionBar
import com.littlestory.core.ui.chrome.TopBar
import com.littlestory.core.ui.story.Avatar
import com.littlestory.core.ui.story.ContinueCard
import com.littlestory.core.ui.story.ListStoryRow
import com.littlestory.core.ui.story.SectionHeader
import com.littlestory.core.ui.story.StoryArt
import com.littlestory.core.ui.story.StoryArtCategory
import com.littlestory.core.ui.story.StoryArtMotif
import com.littlestory.core.ui.story.StoryCard
import com.littlestory.core.ui.story.gradientFor

/** Debug-only catalogue of every design-system and shared component (LS-020); not in release builds. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentCatalog(modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    var text by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var segmented by remember { mutableIntStateOf(1) }
    var tab by remember { mutableIntStateOf(0) }
    var toggle by remember { mutableStateOf(true) }
    var fav by remember { mutableStateOf(true) }
    var nav by remember { mutableIntStateOf(0) }
    val options =
        listOf(R.string.catalog_option_one, R.string.catalog_option_two, R.string.catalog_option_three).map {
            stringResource(it)
        }
    Column(
        modifier.background(colors.background).padding(spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        TopBar(title = stringResource(R.string.catalog_section))
        TopBar(backContentDescription = stringResource(R.string.catalog_back), onBack = {
        }, title = stringResource(R.string.catalog_section))
        Row(Modifier.background(colors.night).padding(spacing.md)) { BrandMark(variant = BrandMarkVariant.Light) }
        LittleStoryButton(stringResource(R.string.catalog_primary), {})
        LittleStoryButton(stringResource(R.string.catalog_secondary), {
        }, variant = ButtonVariant.Secondary, leadingIcon = LittleStoryIcons.Plus)
        LittleStoryButton(stringResource(R.string.catalog_secondary), {}, variant = ButtonVariant.Ghost)
        LittleStoryButton(stringResource(R.string.catalog_primary), {}, enabled = false)
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            LittleStoryIconButton(LittleStoryIcons.Heart, stringResource(R.string.catalog_favourite), {})
            LittleStoryIconButton(
                LittleStoryIcons.HeartFilled,
                stringResource(R.string.catalog_favourite),
                {},
                active = true,
            )
            LittleStoryIconButton(
                LittleStoryIcons.Download,
                stringResource(R.string.catalog_favourite),
                {},
                enabled = false,
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            LittleStoryIcons.all.forEach { LittleStoryIcon(it, contentDescription = null, tint = colors.textPrimary) }
        }
        LittleStoryTextField(
            text,
            {
                text = it
            },
            label =
                stringResource(
                    R.string.catalog_label_name,
                ),
            placeholder = stringResource(R.string.catalog_placeholder),
        )
        LittleStorySearchField(search, {
            search = it
        }, stringResource(R.string.catalog_search), stringResource(R.string.catalog_clear))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            options.forEachIndexed {
                i,
                o,
                ->
                LittleStoryChip(o, selected = i == segmented, onClick = { segmented = i })
            }
        }
        LittleStorySegmentedControl(options, segmented, { segmented = it })
        LittleStoryTabs(options, tab, { tab = it })
        LittleStoryToggle(toggle, { toggle = it }, stringResource(R.string.catalog_toggle))
        LittleStoryNotesBanner(stringResource(R.string.catalog_note))
        LittleStoryEmptyState(
            LittleStoryIcons.Book,
            stringResource(R.string.catalog_empty_title),
            stringResource(R.string.catalog_empty_text),
        )
        LittleStoryReadingProgress(0.42f)
        LittleStoryGenerationProgress(0.6f)
        LittleStoryStorageBar(0.3f)
        SectionHeader(stringResource(R.string.catalog_section), seeAllLabel = stringResource(R.string.catalog_see_all))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
            StoryCard(
                stringResource(
                    R.string.catalog_title,
                ),
                stringResource(
                    R.string.catalog_meta,
                ),
                colors.gradientFor(
                    StoryArtCategory.Friendship,
                ),
                StoryArtMotif.Elephant,
                fav,
                stringResource(R.string.catalog_favourite),
                {
                    fav =
                        !fav
                },
                {},
            )
            StoryCard(
                stringResource(
                    R.string.catalog_title_long,
                ),
                stringResource(
                    R.string.catalog_meta,
                ),
                colors.gradientFor(
                    StoryArtCategory.Adventure,
                ),
                StoryArtMotif.Cricket,
                false,
                stringResource(R.string.catalog_favourite),
                {
                },
                {},
            )
        }
        ListStoryRow(
            stringResource(
                R.string.catalog_meta,
            ),
            stringResource(
                R.string.catalog_title_long,
            ),
            stringResource(R.string.catalog_meta),
            colors.gradientFor(StoryArtCategory.Bedtime),
            StoryArtMotif.Moon,
            {
            },
        )
        ContinueCard(
            stringResource(
                R.string.catalog_eyebrow,
            ),
            stringResource(
                R.string.catalog_title,
            ),
            0.42f,
            stringResource(
                R.string.catalog_primary,
            ),
            colors.gradientFor(StoryArtCategory.Space),
            StoryArtMotif.Space,
            {
            },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
            listOf(StoryArtMotif.Forest, StoryArtMotif.Festival).forEachIndexed { i, motif ->
                Box(Modifier.size(LittleStoryTheme.dimens.listArtHeight)) {
                    StoryArt(
                        colors.gradientFor(
                            if (i ==
                                0
                            ) {
                                StoryArtCategory.IndianTales
                            } else {
                                StoryArtCategory.Festivals
                            },
                        ),
                        motif,
                    )
                }
            }
            Avatar(0, selected = true)
            Avatar(1, selected = false)
            Avatar(2, selected = false, large = true)
        }
        BottomNav(
            tabs =
                listOf(
                    NavTab(stringResource(R.string.catalog_tab_home), LittleStoryIcons.Home),
                    NavTab(stringResource(R.string.catalog_tab_discover), LittleStoryIcons.Compass),
                    NavTab(stringResource(R.string.catalog_tab_create), LittleStoryIcons.Plus, isCreate = true),
                    NavTab(stringResource(R.string.catalog_tab_stories), LittleStoryIcons.Book),
                    NavTab(stringResource(R.string.catalog_tab_profile), LittleStoryIcons.User),
                ),
            selectedIndex = nav,
            onSelect = { nav = it },
        )
        StickyActionBar(stringResource(R.string.catalog_primary), {
        }, secondaryLabel = stringResource(R.string.catalog_secondary))
    }
}

@Preview(widthDp = 430, heightDp = 3200)
@Composable
private fun ComponentCatalogPreview() {
    LittleStoryTheme { ComponentCatalog() }
}
