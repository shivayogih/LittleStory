package com.littlestory.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.littlestory.core.designsystem.R
import com.littlestory.core.designsystem.theme.LittleStoryTheme

/** Every icon in the design (docs/design/design-system.md §7), as drawable resource ids. */
object LittleStoryIcons {
    @DrawableRes val Home = R.drawable.ic_home
    @DrawableRes val Compass = R.drawable.ic_compass
    @DrawableRes val Plus = R.drawable.ic_plus
    @DrawableRes val Book = R.drawable.ic_book
    @DrawableRes val User = R.drawable.ic_user
    @DrawableRes val Bell = R.drawable.ic_bell
    @DrawableRes val Search = R.drawable.ic_search
    @DrawableRes val Heart = R.drawable.ic_heart
    @DrawableRes val HeartFilled = R.drawable.ic_heart_filled
    @DrawableRes val Download = R.drawable.ic_download
    @DrawableRes val DownloadFilled = R.drawable.ic_download_filled
    @DrawableRes val Play = R.drawable.ic_play
    @DrawableRes val Pause = R.drawable.ic_pause
    @DrawableRes val Back = R.drawable.ic_back
    @DrawableRes val Chevron = R.drawable.ic_chevron
    @DrawableRes val Clock = R.drawable.ic_clock
    @DrawableRes val Check = R.drawable.ic_check
    @DrawableRes val Close = R.drawable.ic_close
    @DrawableRes val Edit = R.drawable.ic_edit
    @DrawableRes val Moon = R.drawable.ic_moon
    @DrawableRes val Settings = R.drawable.ic_settings
    @DrawableRes val Volume = R.drawable.ic_volume
    @DrawableRes val Rewind15 = R.drawable.ic_rewind_15
    @DrawableRes val Forward15 = R.drawable.ic_forward_15

    val all: List<Int> = listOf(
        Home, Compass, Plus, Book, User, Bell, Search, Heart, HeartFilled, Download, DownloadFilled, Play, Pause,
        Back, Chevron, Clock, Check, Close, Edit, Moon, Settings, Volume, Rewind15, Forward15,
    )
}

/**
 * Draws a design icon tinted with [tint]. [contentDescription] is required: pass null only for
 * purely decorative icons next to text that already says the same thing.
 */
@Composable
fun LittleStoryIcon(
    @DrawableRes icon: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = modifier
            .size(LittleStoryTheme.dimens.iconSize)
            .then(if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier),
        colorFilter = ColorFilter.tint(tint),
    )
}
