package com.littlestory.feature.home.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.designsystem.theme.tokenShadow
import com.littlestory.core.ui.chrome.LoadingIndicator
import com.littlestory.core.ui.illustration.CultureGatewayArt
import com.littlestory.core.ui.illustration.HomeHeroScene
import com.littlestory.core.ui.story.Avatar
import com.littlestory.core.ui.story.ContinueCard
import com.littlestory.core.ui.story.SectionHeader
import com.littlestory.core.ui.story.StoryItemCard
import com.littlestory.core.ui.story.storyArtFor
import com.littlestory.core.ui.system.SystemBarIcons
import com.littlestory.core.ui.text.asString
import com.littlestory.feature.home.R

object HomeTags {
    const val TITLE = "home_title"
    const val BELL = "home_bell"
    const val AVATAR = "home_avatar"
    const val CREATE = "home_create"
    const val CONTINUE_CARD = "home_continue"
    const val SEE_ALL_EXPLORE = "home_see_all_explore"
    const val CULTURE = "home_culture"

    fun category(id: String) = "home_category_$id"

    fun story(id: String) = "home_story_$id"
}

/** Where Home sends the child. Home never knows other features' routes; `:app` supplies these. */
data class HomeActions(
    val onNotifications: () -> Unit = {},
    val onProfiles: () -> Unit = {},
    val onCreate: () -> Unit = {},
    val onResume: (storyId: String) -> Unit = {},
    val onDiscover: () -> Unit = {},
    val onCategory: (categoryId: String) -> Unit = {},
    val onStory: (storyId: String) -> Unit = {},
)

@Composable
fun HomeRoute(actions: HomeActions, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(state, actions, viewModel::onFavouriteClick)
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    actions: HomeActions,
    onFavouriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    SystemBarIcons(darkIcons = true)
    Column(
        modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBars),
        verticalArrangement = Arrangement.spacedBy(spacing.xxl),
    ) {
        Header(state, actions)
        Column(
            Modifier.padding(horizontal = spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(spacing.xxl),
        ) {
            HeroCard(actions.onCreate)
            if (state.loading) {
                LoadingIndicator()
            } else {
                state.continueListening?.let { ContinueSection(it, actions.onResume) }
                if (state.categories.isNotEmpty()) ExploreSection(state.categories, actions)
                if (state.madeFor.isNotEmpty()) MadeForSection(state, actions, onFavouriteClick)
            }
            CultureBanner { actions.onCategory(AppConfig.Home.CULTURE_CATEGORY_ID) }
        }
    }
}

@Composable
private fun Header(state: HomeUiState, actions: HomeActions) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val profilesLabel = stringResource(R.string.home_profiles)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal)
            .padding(top = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(state.greeting.asString(), style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
            Text(
                state.title.asString(),
                style = LittleStoryTheme.typography.homeTitle,
                color = colors.textPrimary,
                modifier = Modifier.testTag(HomeTags.TITLE),
            )
        }
        LittleStoryIconButton(
            icon = LittleStoryIcons.Bell,
            contentDescription = stringResource(R.string.home_notifications),
            onClick = actions.onNotifications,
            modifier = Modifier.testTag(HomeTags.BELL),
        )
        Box(
            Modifier
                .padding(start = spacing.sm)
                .size(LittleStoryTheme.dimens.minTouchTarget)
                .clip(LittleStoryTheme.shapes.avatar)
                .clickable(role = Role.Button, onClick = actions.onProfiles)
                .semantics { contentDescription = profilesLabel }
                .testTag(HomeTags.AVATAR),
            contentAlignment = Alignment.Center,
        ) {
            Avatar(
                tone = state.avatarTone ?: 0,
                selected = false,
                modifier = Modifier.size(LittleStoryTheme.dimens.homeAvatarSize),
            )
        }
    }
}

@Composable
private fun HeroCard(onCreate: () -> Unit) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val shape = LittleStoryTheme.shapes.hero
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = LittleStoryTheme.dimens.heroMinHeight)
            .tokenShadow(LittleStoryTheme.elevation.hero, shape)
            .clip(shape)
            .background(colors.primary),
    ) {
        Box(Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
            HomeHeroScene(Modifier.width(LittleStoryTheme.dimens.heroSceneWidth).fillMaxHeight())
        }
        Column(
            Modifier.fillMaxWidth(HERO_TEXT_FRACTION).padding(spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                stringResource(R.string.home_hero_kicker),
                style = LittleStoryTheme.typography.heroKicker,
                color = colors.accent,
            )
            Text(
                stringResource(R.string.home_hero_title),
                style = LittleStoryTheme.typography.heroTitle,
                color = colors.onPrimary,
            )
            Text(
                stringResource(R.string.home_hero_body),
                style = LittleStoryTheme.typography.body,
                color = colors.onPrimary,
            )
            Row(
                Modifier
                    .padding(top = spacing.sm)
                    .heightIn(min = LittleStoryTheme.dimens.minTouchTarget)
                    .clip(LittleStoryTheme.shapes.button)
                    .background(colors.surface)
                    .clickable(role = Role.Button, onClick = onCreate)
                    .padding(horizontal = spacing.lg)
                    .testTag(HomeTags.CREATE),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                LittleStoryIcon(LittleStoryIcons.Plus, contentDescription = null, tint = colors.primary)
                Text(
                    stringResource(R.string.home_hero_cta),
                    style = LittleStoryTheme.typography.button,
                    color = colors.primary,
                )
            }
        }
    }
}

// The hero text takes the start 60 % of the card; the illustration owns the rest (design: right 40 %).
private const val HERO_TEXT_FRACTION = 0.62f

@Composable
private fun ContinueSection(item: ContinueUi, onResume: (String) -> Unit) {
    val (gradient, motif) = storyArtFor(item.story.paletteId, item.story.motifId)
    Column(verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.md)) {
        SectionHeader(stringResource(R.string.home_continue_title))
        ContinueCard(
            eyebrow = stringResource(R.string.home_continue_eyebrow, item.story.durationMinutes, item.percent),
            title = item.story.title,
            progress = item.percent / PERCENT_SCALE,
            resumeLabel = stringResource(R.string.home_continue_resume),
            gradient = gradient,
            motif = motif,
            onResume = { onResume(item.story.id) },
            storyId = item.story.id,
            modifier = Modifier.testTag(HomeTags.CONTINUE_CARD),
        )
    }
}

private const val PERCENT_SCALE = 100f

@Composable
private fun ExploreSection(categories: List<CategoryTileUi>, actions: HomeActions) {
    Column(verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.md)) {
        SectionHeader(
            title = stringResource(R.string.home_explore_title),
            seeAllLabel = stringResource(R.string.home_see_all),
            onSeeAll = actions.onDiscover,
            modifier = Modifier.testTag(HomeTags.SEE_ALL_EXPLORE),
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs)) {
            items(categories, key = { it.id }) { category ->
                CategoryTile(category, onClick = { actions.onCategory(category.id) })
            }
        }
    }
}

@Composable
private fun CategoryTile(category: CategoryTileUi, onClick: () -> Unit) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val (icon, tint) = tileStyleFor(category.id)
    Column(
        Modifier
            .width(dimens.categoryTileItemWidth)
            // No clip on the whole item: rounded corners cut the ends of a full-width label ("Indian Tales").
            .clickable(role = Role.Button, onClick = onClick)
            .testTag(HomeTags.category(category.id)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.sm),
    ) {
        Box(
            Modifier.size(dimens.categoryTileSize).clip(LittleStoryTheme.shapes.medium).background(tint),
            contentAlignment = Alignment.Center,
        ) {
            LittleStoryIcon(icon, contentDescription = null, tint = colors.primary)
        }
        Text(
            category.name,
            style = LittleStoryTheme.typography.chip,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

/** Icon and pastel for a category tile; categories without a specific look use the lavender book tile. */
@Composable
private fun tileStyleFor(categoryId: String): Pair<Int, Color> {
    val tiles = LittleStoryTheme.colors.categoryTiles

    @DrawableRes val book = LittleStoryIcons.Book
    return when (categoryId) {
        "bedtime" -> LittleStoryIcons.Moon to tiles.lavender
        "animals" -> book to tiles.peach
        "adventure" -> LittleStoryIcons.Compass to tiles.mint
        "friendship" -> LittleStoryIcons.Heart to tiles.pink
        "indian_tales" -> book to tiles.butter
        else -> book to tiles.lavender
    }
}

@Composable
private fun MadeForSection(
    state: HomeUiState,
    actions: HomeActions,
    onFavouriteClick: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.md)) {
        SectionHeader(
            title = state.madeForTitle.asString(),
            seeAllLabel = stringResource(R.string.home_see_all),
            onSeeAll = actions.onDiscover,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.md)) {
            items(state.madeFor, key = { it.id }) { item ->
                StoryItemCard(
                    item = item,
                    onClick = { actions.onStory(item.id) },
                    onFavouriteClick = { onFavouriteClick(item.id) },
                    modifier = Modifier.testTag(HomeTags.story(item.id)),
                )
            }
        }
    }
}

@Composable
private fun CultureBanner(onClick: () -> Unit) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val spacing = LittleStoryTheme.spacing
    Box(
        Modifier
            .fillMaxWidth()
            .clip(LittleStoryTheme.shapes.large)
            .background(colors.cultureBanner)
            .clickable(role = Role.Button, onClick = onClick)
            .testTag(HomeTags.CULTURE),
    ) {
        CultureGatewayArt(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = spacing.lg)
                .size(dimens.cultureArtWidth, dimens.cultureArtHeight),
        )
        Column(Modifier.padding(spacing.xl), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                stringResource(R.string.home_culture_kicker),
                style = LittleStoryTheme.typography.eyebrow,
                color = colors.primaryVariantLabel,
            )
            Text(
                stringResource(R.string.home_culture_title),
                style = LittleStoryTheme.typography.homeTitle,
                color = colors.textPrimary,
                modifier = Modifier.fillMaxWidth(CULTURE_TEXT_FRACTION),
            )
            Text(
                stringResource(R.string.home_culture_body),
                style = LittleStoryTheme.typography.body,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth(CULTURE_TEXT_FRACTION),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.home_culture_link),
                    style = LittleStoryTheme.typography.button,
                    color = colors.primary,
                )
                LittleStoryIcon(LittleStoryIcons.Chevron, contentDescription = null, tint = colors.primary)
            }
        }
    }
}

private const val CULTURE_TEXT_FRACTION = 0.72f
