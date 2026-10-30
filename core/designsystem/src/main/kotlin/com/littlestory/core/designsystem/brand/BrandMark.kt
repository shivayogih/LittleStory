package com.littlestory.core.designsystem.brand

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.littlestory.core.designsystem.R
import com.littlestory.core.designsystem.theme.LittleStoryTheme

/** Where the mark is drawn: on the cream canvas or on a night surface. */
enum class BrandMarkVariant { Default, Light }

// Geometry of the CSS logo (docs/design/LittleStory_Figma_Design_Details.md §1.5), in dp.
private val MarkWidth = 32.dp
private val MarkHeight = 29.dp
private val MarkStroke = 2.dp
private const val SKEW_DEGREES = -4f
private val CornerTopStart = 5.dp
private val CornerTopEnd = 12.dp
private val CornerBottomEnd = 4.dp
private val CornerBottomStart = 7.dp
private val MoonRadius = 5.dp
private val MoonBiteOffset = 2.5.dp
private val StarRadius = 4.dp
private val MarkWordmarkGap = 8.dp

/** The symbol only: skewed open-book outline, inner "L" curve, crescent moon and a yellow star. */
@Composable
fun BrandSymbol(variant: BrandMarkVariant = BrandMarkVariant.Default, modifier: Modifier = Modifier) {
    val colors = LittleStoryTheme.colors
    val ink = if (variant == BrandMarkVariant.Light) colors.onNight else colors.primary
    val star = colors.highlight
    Canvas(modifier.size(MarkWidth, MarkHeight).clearAndSetSemantics { }) {
        val stroke = Stroke(MarkStroke.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height
        val inset = MarkStroke.toPx() / 2
        drawContext.canvas.save()
        drawContext.canvas.skew(tanDegrees(SKEW_DEGREES), 0f)
        run {
            val body =
                Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = inset,
                            top = inset,
                            right = w - inset,
                            bottom = h - inset,
                            topLeftCornerRadius = CornerRadius(CornerTopStart.toPx()),
                            topRightCornerRadius = CornerRadius(CornerTopEnd.toPx()),
                            bottomRightCornerRadius = CornerRadius(CornerBottomEnd.toPx()),
                            bottomLeftCornerRadius = CornerRadius(CornerBottomStart.toPx()),
                        ),
                    )
                }
            drawPath(body, ink, style = stroke)
            // inner "L": a curve down the spine and along the foot
            val l =
                Path().apply {
                    moveTo(w * 0.34f, h * 0.22f)
                    lineTo(w * 0.34f, h * 0.62f)
                    quadraticTo(w * 0.34f, h * 0.76f, w * 0.5f, h * 0.76f)
                    lineTo(w * 0.7f, h * 0.76f)
                }
            drawPath(l, ink, style = stroke)
        }
        drawContext.canvas.restore()
        // crescent moon at the top-right corner: a disc minus an offset disc
        val moonCenter = Offset(w - MoonRadius.toPx() * 0.4f, MoonRadius.toPx() * 0.9f)
        val disc =
            Path().apply {
                addOval(
                    androidx.compose.ui.geometry
                        .Rect(moonCenter, MoonRadius.toPx()),
                )
            }
        val bite =
            Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        moonCenter + Offset(MoonBiteOffset.toPx(), -MoonBiteOffset.toPx() * 0.4f),
                        MoonRadius.toPx(),
                    ),
                )
            }
        drawPath(Path.combine(PathOperation.Difference, disc, bite), ink)
        // yellow four-point star
        val starCenter = Offset(w * 0.78f, h * 0.55f)
        drawPath(fourPointStar(starCenter, StarRadius.toPx()), star)
    }
}

private fun tanDegrees(degrees: Float): Float = kotlin.math.tan(Math.toRadians(degrees.toDouble())).toFloat()

private fun fourPointStar(center: Offset, radius: Float): Path =
    Path().apply {
        val inner = radius * 0.3f
        moveTo(center.x, center.y - radius)
        quadraticTo(center.x + inner, center.y - inner, center.x + radius, center.y)
        quadraticTo(center.x + inner, center.y + inner, center.x, center.y + radius)
        quadraticTo(center.x - inner, center.y + inner, center.x - radius, center.y)
        quadraticTo(center.x - inner, center.y - inner, center.x, center.y - radius)
        close()
    }

/**
 * Symbol plus the "LittleStory" wordmark. When the wordmark does not fit the space it is given
 * (a long screen title at 200 % font scale), only the symbol shows, so the two never overlap.
 */
@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    variant: BrandMarkVariant = BrandMarkVariant.Default,
) {
    val colors = LittleStoryTheme.colors
    val textColor: Color = if (variant == BrandMarkVariant.Light) colors.onNight else colors.primary
    Layout(
        content = {
            BrandSymbol(variant)
            Text(
                text = stringResource(R.string.brand_name),
                style = LittleStoryTheme.typography.brandName,
                color = textColor,
                maxLines = 1,
                softWrap = false,
            )
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val symbol = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val wordmark = measurables[1].measure(Constraints())
        val gap = MarkWordmarkGap.roundToPx()
        val showWordmark = symbol.width + gap + wordmark.width <= constraints.maxWidth
        val width = if (showWordmark) symbol.width + gap + wordmark.width else symbol.width
        val height = if (showWordmark) maxOf(symbol.height, wordmark.height) else symbol.height
        layout(width, height) {
            symbol.place(0, (height - symbol.height) / 2)
            if (showWordmark) wordmark.place(symbol.width + gap, (height - wordmark.height) / 2)
        }
    }
}
