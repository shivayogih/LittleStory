package com.littlestory.core.ui.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.component.LittleStoryIconButton
import com.littlestory.core.designsystem.component.LittleStoryReadingProgress
import com.littlestory.core.designsystem.icon.LittleStoryIcon
import com.littlestory.core.designsystem.icon.LittleStoryIcons
import com.littlestory.core.designsystem.theme.GradientPair
import com.littlestory.core.designsystem.theme.LittleStoryTheme

/** Cover art, title and "Category · N min", with a floating favourite toggle. */
@Composable
fun StoryCard(
    title: String,
    meta: String,
    gradient: GradientPair,
    motif: StoryArtMotif,
    favourite: Boolean,
    favouriteContentDescription: String,
    onFavouriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    wide: Boolean = false,
) {
    val dimens = LittleStoryTheme.dimens
    val colors = LittleStoryTheme.colors
    val width = if (wide) dimens.storyCardWideWidth else dimens.storyCardWidth
    Column(
        modifier
            .width(width)
            .clickable(role = Role.Button, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs),
    ) {
        Box {
            Box(Modifier.fillMaxWidth().aspectRatio(CARD_ART_RATIO).clip(LittleStoryTheme.shapes.medium)) {
                StoryArt(gradient, motif)
            }
            LittleStoryIconButton(
                icon = if (favourite) LittleStoryIcons.HeartFilled else LittleStoryIcons.Heart,
                contentDescription = favouriteContentDescription,
                onClick = onFavouriteClick,
                active = favourite,
                modifier = Modifier.align(Alignment.TopEnd).padding(LittleStoryTheme.spacing.xs),
            )
        }
        Column(Modifier.semantics(mergeDescendants = true) { }) {
            Text(
                title,
                style = LittleStoryTheme.typography.bodyLarge,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(meta, style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
        }
    }
}

// Cover art is 4:5 portrait in the prototype's cards.
private const val CARD_ART_RATIO = 0.8f

/** Horizontal row for lists (Category, My Stories, Downloads): art tile, eyebrow, title, meta and a trailing action. */
@Composable
fun ListStoryRow(
    eyebrow: String,
    title: String,
    meta: String,
    gradient: GradientPair,
    motif: StoryArtMotif,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    offlineLabel: String? = null,
    trailing: @Composable () -> Unit = {},
) {
    val dimens = LittleStoryTheme.dimens
    val colors = LittleStoryTheme.colors
    Row(
        modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(dimens.listArtWidth, dimens.listArtHeight).clip(LittleStoryTheme.shapes.medium)) {
            StoryArt(gradient, motif)
        }
        Column(
            Modifier.weight(1f).semantics(mergeDescendants = true) {
            },
            verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xxs),
        ) {
            Text(eyebrow, style = LittleStoryTheme.typography.eyebrow, color = colors.primaryVariantLabel)
            Text(title, style = LittleStoryTheme.typography.bodyLarge, color = colors.textPrimary)
            Text(meta, style = LittleStoryTheme.typography.chip, color = colors.textSecondary)
            if (offlineLabel != null) OfflineBadge(offlineLabel)
        }
        trailing()
    }
}

/** Resume card: art, "8 MIN · 42 % COMPLETE", title, progress bar and a resume button. */
@Composable
fun ContinueCard(
    eyebrow: String,
    title: String,
    progress: Float,
    resumeLabel: String,
    gradient: GradientPair,
    motif: StoryArtMotif,
    onResume: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    Column(
        modifier
            .fillMaxWidth()
            .background(colors.surface, LittleStoryTheme.shapes.large)
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(LittleStoryTheme.dimens.listArtHeight).clip(LittleStoryTheme.shapes.medium)) {
                StoryArt(gradient, motif)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                Text(eyebrow, style = LittleStoryTheme.typography.eyebrow, color = colors.primaryVariantLabel)
                Text(title, style = LittleStoryTheme.typography.topBarTitle, color = colors.textPrimary)
            }
        }
        LittleStoryReadingProgress(progress)
        LittleStoryButton(text = resumeLabel, onClick = onResume)
    }
}

/** Section title with an optional "See all" link. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    seeAllLabel: String? = null,
    onSeeAll: () -> Unit = {},
) {
    val colors = LittleStoryTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            style = LittleStoryTheme.typography.homeTitle,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (seeAllLabel != null) {
            Row(
                Modifier
                    .clip(
                        LittleStoryTheme.shapes.chip,
                    ).clickable(role = Role.Button, onClick = onSeeAll)
                    .padding(LittleStoryTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(seeAllLabel, style = LittleStoryTheme.typography.chip, color = colors.primary)
                LittleStoryIcon(
                    LittleStoryIcons.Chevron,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(LittleStoryTheme.dimens.iconSizeSmall),
                )
            }
        }
    }
}

/** Child avatar: skin-tone rounded square, hair cap and two dot eyes; [selected] adds a lavender outline. */
@Composable
fun Avatar(
    tone: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    large: Boolean = false,
) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val shape = if (large) LittleStoryTheme.shapes.avatarLarge else LittleStoryTheme.shapes.avatar
    val skin =
        listOf(
            colors.illustration.skin,
            colors.illustration.skinLight,
            colors.warmAccent,
        )[tone.mod(AVATAR_TONES)]
    val size = if (large) dimens.avatarLargeSize else dimens.avatarSize
    Box(
        modifier
            .size(size)
            .then(if (selected) Modifier.border(dimens.focusRingWidth, colors.accent, shape) else Modifier)
            .clip(shape)
            .background(skin),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .align(
                    Alignment.TopCenter,
                ).fillMaxWidth()
                .height(size * HAIR_FRACTION)
                .background(colors.illustration.hair),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(size * EYE_GAP_FRACTION),
            modifier =
                Modifier.padding(
                    top =
                        size * EYE_TOP_FRACTION,
                ),
        ) {
            repeat(EYE_COUNT) {
                Box(
                    Modifier
                        .size(
                            size * EYE_FRACTION,
                        ).background(colors.illustration.hair, LittleStoryTheme.shapes.iconButton),
                )
            }
        }
    }
}

private const val AVATAR_TONES = 3
private const val EYE_COUNT = 2
private const val HAIR_FRACTION = 0.32f
private const val EYE_FRACTION = 0.1f
private const val EYE_GAP_FRACTION = 0.22f
private const val EYE_TOP_FRACTION = 0.15f
