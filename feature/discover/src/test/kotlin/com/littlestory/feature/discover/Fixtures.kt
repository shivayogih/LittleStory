package com.littlestory.feature.discover

import com.littlestory.core.ui.story.StoryItem
import com.littlestory.core.ui.text.UiText
import com.littlestory.feature.discover.category.CategoryUiState
import com.littlestory.feature.discover.category.SortOptionUi
import com.littlestory.feature.discover.detail.StoryDetailContent
import com.littlestory.feature.discover.detail.StoryDetailUiState
import com.littlestory.feature.discover.discover.DiscoverUiState
import com.littlestory.feature.discover.discover.FeaturedUi
import com.littlestory.feature.discover.discover.FilterChipUi

private fun item(
    id: String,
    title: String,
    category: String,
    palette: String,
    motif: String,
    minutes: Int,
    fav: Boolean = false,
    offline: Boolean = false,
) = StoryItem(id, title, category, minutes, 4, 7, palette, motif, fav, offline)

val elephant =
    item(
        "story_little_elephant",
        "The Little Elephant Who Shared",
        "Friendship",
        "friendship",
        "elephant",
        6,
        fav = true,
    )
val cricket =
    item("story_magical_cricket_bat", "Aarav and the Magical Cricket Bat", "Adventure", "adventure", "cricket", 8)
val garden = item("story_moonlit_garden", "Mira and the Moonlit Garden", "Bedtime", "bedtime", "moon", 7)
val banyan =
    item("story_banyan_secret", "The Banyan Tree's Secret", "Indian Tales", "indian_tales", "forest", 6, offline = true)

val discoverDefault =
    DiscoverUiState(
        filters =
            listOf(FilterChipUi("all", UiText.Res(R.string.discover_filter_all))) +
                listOf("Animals", "Adventure", "Friendship", "Kindness", "Nature", "Space").map {
                    FilterChipUi(it.lowercase(), UiText.Dynamic(it))
                },
        featured = FeaturedUi(elephant, "A tender tale about kindness and a basket of mangoes."),
        results = listOf(elephant, cricket, garden, banyan),
        showViewAll = true,
        bedtime = listOf(garden, elephant),
    )

val discoverSearching =
    discoverDefault.copy(
        query = "cricket",
        featured = null,
        sectionTitle = UiText.Res(R.string.discover_results, listOf("cricket")),
        results = listOf(cricket),
        showViewAll = false,
        bedtime = emptyList(),
    )

val discoverEmpty = discoverSearching.copy(query = "zzzz", results = emptyList())

val categoryState =
    CategoryUiState(
        title = UiText.Dynamic("Indian Tales"),
        description =
            UiText.Dynamic(
                "Warm, wonder-filled adventures inspired by India's many landscapes and storytelling traditions.",
            ),
        storyCount = 18,
        sortOptions =
            listOf(
                SortOptionUi("recommended", UiText.Res(R.string.category_sort_recommended)),
                SortOptionUi("shortest", UiText.Res(R.string.category_sort_shortest)),
                SortOptionUi("newest", UiText.Res(R.string.category_sort_newest)),
            ),
        stories = listOf(banyan, elephant, garden),
    )

val detailState =
    StoryDetailUiState.Loaded(
        StoryDetailContent(
            id = "story_little_elephant",
            title = "The Little Elephant Who Shared",
            categoryName = "Friendship",
            ageMin = 4,
            ageMax = 7,
            durationMinutes = 6,
            languageName = "English",
            synopsis =
                "A gentle adventure about friendship, generosity, and discovering that sharing can make everyone happier.",
            lesson = "Sharing, generosity and caring for friends.",
            paletteId = "friendship",
            motifId = "elephant",
            favourite = true,
            downloaded = false,
        ),
    )
