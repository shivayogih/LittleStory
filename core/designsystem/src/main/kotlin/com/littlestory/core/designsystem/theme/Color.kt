package com.littlestory.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Raw palette from docs/design/design-tokens.json. This is the only file in the project that may
 * contain colour literals; everything else uses the semantic roles in [LittleStoryColors].
 */
internal object Palette {
    // brand
    val Indigo = Color(0xFF35256F)
    val Indigo2 = Color(0xFF4B388B)
    val Lavender = Color(0xFFA995F5)
    val SoftLavender = Color(0xFFEAE3FF)

    // night
    val Night = Color(0xFF17152F)
    val NightSurface = Color(0xFF242044)

    // surface
    val Cream = Color(0xFFFFF9F0)
    val White = Color(0xFFFFFFFF)
    val Surface = Color(0xFFF8F5FB)
    val Line = Color(0xFFE9E4EC)
    val PageBackdrop = Color(0xFFE8E5ED)

    // shadow ink (#231846, the colour of the card and soft shadows)
    val ShadowInk = Color(0xFF231846)

    // accent
    val Yellow = Color(0xFFFFD977)
    val Peach = Color(0xFFFFC9AD)
    val Moon = Color(0xFFFFF1BD)

    // text
    val Text = Color(0xFF25213A)
    val Muted = Color(0xFF716B80)
    val ReaderBody = Color(0xFF393348)
    val NavInactive = Color(0xFF817B8D)

    // semantic
    val Success = Color(0xFF287A54)
    val SuccessBg = Color(0xFFEAF5EE)
    val Error = Color(0xFFB42318)
    val Favourite = Color(0xFF9D3158)
    val FavouriteBg = Color(0xFFFFEDF2)

    // Colours used in screen specs but missing from design-tokens.json (docs/design/figma-audit.md A6).
    // Values are from the design details doc; verify against the screenshots before P4 screens use them.
    val CultureBanner = Color(0xFFF5DFC0)
    val LessonPanel = Color(0xFFFFF4E8)
    val ReaderBackground = Color(0xFFFFFAF2)
    val SuccessRibbon = Color(0xFFE9F6EE)

    // storyArt gradients (start, end)
    val FriendshipStart = Color(0xFFF6B995)
    val FriendshipEnd = Color(0xFF694B91)
    val AdventureStart = Color(0xFF6252A6)
    val AdventureEnd = Color(0xFFF2A978)
    val BedtimeStart = Color(0xFF32306D)
    val BedtimeEnd = Color(0xFF9F91E9)
    val IndianTalesStart = Color(0xFF47796B)
    val IndianTalesEnd = Color(0xFFEFBB69)
    val SpaceStart = Color(0xFF25285D)
    val SpaceEnd = Color(0xFFDB82A6)
    val FestivalsStart = Color(0xFFB15B54)
    val FestivalsEnd = Color(0xFFF5C568)

    // illustration
    val Skin = Color(0xFFD9986C)
    val SkinLight = Color(0xFFE8C3A5)
    val Hair = Color(0xFF332422)
    val ChildTop = Color(0xFF6652A1)
    val Elephant = Color(0xFF8C82A8)
    val ElephantLight = Color(0xFF9E95B8)
    val ElephantLeg = Color(0xFF81779D)
    val CricketBat = Color(0xFFD9B885)
    val ShirtGold = Color(0xFFE2AA55)
}

/** A two-stop gradient used for story covers. */
@Immutable
data class GradientPair(val start: Color, val end: Color)

/** Story-cover gradients keyed by story category (data-driven, see ADR-010). */
@Immutable
data class StoryArtColors(
    val friendship: GradientPair,
    val adventure: GradientPair,
    val bedtime: GradientPair,
    val indianTales: GradientPair,
    val space: GradientPair,
    val festivals: GradientPair,
)

/** Colours for drawn characters and props. */
@Immutable
data class IllustrationColors(
    val skin: Color,
    val skinLight: Color,
    val hair: Color,
    val childTop: Color,
    val elephant: Color,
    val elephantLight: Color,
    val elephantLeg: Color,
    val cricketBat: Color,
    val shirtGold: Color,
)

/** Semantic colour roles (docs/design/design-system.md §3). Feature code reads only these. */
@Immutable
data class LittleStoryColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryVariantLabel: Color,
    val accent: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val background: Color,
    val pageBackdrop: Color,
    val surface: Color,
    val surfaceMuted: Color,
    val outline: Color,
    val night: Color,
    val nightSurface: Color,
    val onNight: Color,
    val highlight: Color,
    val warmAccent: Color,
    val moon: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textReader: Color,
    val navInactive: Color,
    val success: Color,
    val successContainer: Color,
    val error: Color,
    val favourite: Color,
    val favouriteContainer: Color,
    val cultureBanner: Color,
    val lessonPanel: Color,
    val readerBackground: Color,
    val successRibbon: Color,
    val storyArt: StoryArtColors,
    val illustration: IllustrationColors,
)

internal val LightLittleStoryColors = LittleStoryColors(
    primary = Palette.Indigo,
    onPrimary = Palette.White,
    primaryVariantLabel = Palette.Indigo2,
    accent = Palette.Lavender,
    secondaryContainer = Palette.SoftLavender,
    onSecondaryContainer = Palette.Indigo,
    background = Palette.Cream,
    pageBackdrop = Palette.PageBackdrop,
    surface = Palette.White,
    surfaceMuted = Palette.Surface,
    outline = Palette.Line,
    night = Palette.Night,
    nightSurface = Palette.NightSurface,
    onNight = Palette.White,
    highlight = Palette.Yellow,
    warmAccent = Palette.Peach,
    moon = Palette.Moon,
    textPrimary = Palette.Text,
    textSecondary = Palette.Muted,
    textReader = Palette.ReaderBody,
    // The design's nav-inactive (#817B8D) fails WCAG AA contrast (accessibility-spec AX-01, R-07);
    // muted text passes at 4.87:1 on cream. Palette.NavInactive stays until the owner decides.
    navInactive = Palette.Muted,
    success = Palette.Success,
    successContainer = Palette.SuccessBg,
    error = Palette.Error,
    favourite = Palette.Favourite,
    favouriteContainer = Palette.FavouriteBg,
    cultureBanner = Palette.CultureBanner,
    lessonPanel = Palette.LessonPanel,
    readerBackground = Palette.ReaderBackground,
    successRibbon = Palette.SuccessRibbon,
    storyArt = StoryArtColors(
        friendship = GradientPair(Palette.FriendshipStart, Palette.FriendshipEnd),
        adventure = GradientPair(Palette.AdventureStart, Palette.AdventureEnd),
        bedtime = GradientPair(Palette.BedtimeStart, Palette.BedtimeEnd),
        indianTales = GradientPair(Palette.IndianTalesStart, Palette.IndianTalesEnd),
        space = GradientPair(Palette.SpaceStart, Palette.SpaceEnd),
        festivals = GradientPair(Palette.FestivalsStart, Palette.FestivalsEnd),
    ),
    illustration = IllustrationColors(
        skin = Palette.Skin,
        skinLight = Palette.SkinLight,
        hair = Palette.Hair,
        childTop = Palette.ChildTop,
        elephant = Palette.Elephant,
        elephantLight = Palette.ElephantLight,
        elephantLeg = Palette.ElephantLeg,
        cricketBat = Palette.CricketBat,
        shirtGold = Palette.ShirtGold,
    ),
)
