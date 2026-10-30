package com.littlestory.core.ui.story

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.GradientPair
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.R
import com.littlestory.domain.model.Story

/**
 * What a story card or row needs, resolved from the catalogue. [categoryName] is already in the
 * content language; every label around it comes from string resources.
 */
@Immutable
data class StoryItem(
    val id: String,
    val title: String,
    val categoryName: String,
    val durationMinutes: Int,
    val ageMin: Int,
    val ageMax: Int,
    val paletteId: String,
    val motifId: String,
    val favourite: Boolean,
    val downloaded: Boolean,
)

fun Story.toItem(
    categoryNames: Map<String, String>,
    favouriteIds: Set<String>,
    downloadedIds: Set<String> = emptySet(),
): StoryItem =
    StoryItem(
        id = id,
        title = title,
        categoryName = categoryNames[categoryId].orEmpty(),
        durationMinutes = durationMinutes,
        ageMin = ageMin,
        ageMax = ageMax,
        paletteId = art.paletteId,
        motifId = art.motifId,
        favourite = id in favouriteIds,
        downloaded = id in downloadedIds,
    )

/** Gradient and motif for a cover; unknown ids use the Friendship gradient and the Moon motif as a placeholder. */
@Composable
fun storyArtFor(paletteId: String, motifId: String): Pair<GradientPair, StoryArtMotif> {
    val category =
        when (paletteId) {
            "friendship" -> StoryArtCategory.Friendship
            "adventure" -> StoryArtCategory.Adventure
            "bedtime" -> StoryArtCategory.Bedtime
            "indian_tales" -> StoryArtCategory.IndianTales
            "space" -> StoryArtCategory.Space
            "festivals" -> StoryArtCategory.Festivals
            else -> StoryArtCategory.Friendship
        }
    val motif =
        when (motifId) {
            "elephant" -> StoryArtMotif.Elephant
            "cricket" -> StoryArtMotif.Cricket
            "forest" -> StoryArtMotif.Forest
            "space" -> StoryArtMotif.Space
            "festival" -> StoryArtMotif.Festival
            else -> StoryArtMotif.Moon
        }
    return LittleStoryTheme.colors.gradientFor(category) to motif
}

/** [StoryCard] fed from a [StoryItem]: "Category · N min" with a favourite toggle. */
@Composable
fun StoryItemCard(
    item: StoryItem,
    onClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    wide: Boolean = false,
) {
    val (gradient, motif) = storyArtFor(item.paletteId, item.motifId)
    StoryCard(
        title = item.title,
        meta = stringResource(R.string.story_card_meta, item.categoryName, item.durationMinutes),
        gradient = gradient,
        motif = motif,
        favourite = item.favourite,
        favouriteContentDescription = favouriteDescription(item),
        onFavouriteClick = onFavouriteClick,
        onClick = onClick,
        modifier = modifier,
        wide = wide,
    )
}

/** [ListStoryRow] fed from a [StoryItem]: ages and duration, offline badge and a favourite toggle. */
@Composable
fun StoryItemRow(
    item: StoryItem,
    onClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (gradient, motif) = storyArtFor(item.paletteId, item.motifId)
    ListStoryRow(
        eyebrow = item.categoryName,
        title = item.title,
        meta = stringResource(R.string.story_list_meta, item.ageMin, item.ageMax, item.durationMinutes),
        gradient = gradient,
        motif = motif,
        onClick = onClick,
        modifier = modifier,
        offlineLabel = if (item.downloaded) stringResource(R.string.story_offline) else null,
        trailing = {
            LittleStoryIconButton(
                icon = if (item.favourite) LittleStoryIcons.HeartFilled else LittleStoryIcons.Heart,
                contentDescription = favouriteDescription(item),
                onClick = onFavouriteClick,
                active = item.favourite,
            )
        },
    )
}

@Composable
private fun favouriteDescription(item: StoryItem): String =
    if (item.favourite) {
        stringResource(R.string.story_favourite_remove, item.title)
    } else {
        stringResource(R.string.story_favourite_add, item.title)
    }

/** Green "Available offline" line shown under a row's metadata. */
@Composable
internal fun OfflineBadge(label: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = LittleStoryTheme.typography.chip, color = LittleStoryTheme.colors.success)
    }
}
