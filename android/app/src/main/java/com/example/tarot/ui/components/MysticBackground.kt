package com.example.tarot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.tarot.ui.theme.DeepNight
import com.example.tarot.ui.theme.Gold
import com.example.tarot.ui.theme.Lavender
import com.example.tarot.ui.theme.Midnight
import com.example.tarot.ui.theme.NebulaBlue
import com.example.tarot.ui.theme.NebulaRose
import com.example.tarot.ui.theme.NebulaViolet

@Composable
fun MysticBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Midnight,
                        Color(0xFF080513),
                        DeepNight,
                        Color(0xFF06030F),
                    ),
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val violetCenter = Offset(size.width * 0.08f, size.height * 0.08f)
            val blueCenter = Offset(size.width * 0.96f, size.height * 0.3f)
            val roseCenter = Offset(size.width * 0.45f, size.height * 0.98f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NebulaViolet.copy(alpha = 0.32f), Color.Transparent),
                    center = violetCenter,
                    radius = size.width * 0.88f,
                ),
                radius = size.width * 0.88f,
                center = violetCenter,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NebulaBlue.copy(alpha = 0.2f), Color.Transparent),
                    center = blueCenter,
                    radius = size.width * 0.65f,
                ),
                radius = size.width * 0.65f,
                center = blueCenter,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NebulaRose.copy(alpha = 0.14f), Color.Transparent),
                    center = roseCenter,
                    radius = size.width * 0.9f,
                ),
                radius = size.width * 0.9f,
                center = roseCenter,
            )

            // Тонкие орбиты в глубине фона.
            val orbitCenter = Offset(size.width * 0.92f, size.height * 0.12f)
            drawCircle(
                color = Gold.copy(alpha = 0.05f),
                radius = size.width * 0.58f,
                center = orbitCenter,
                style = Stroke(width = 1.dp.toPx()),
            )
            drawCircle(
                color = Lavender.copy(alpha = 0.045f),
                radius = size.width * 0.42f,
                center = orbitCenter,
                style = Stroke(width = 1.dp.toPx()),
            )

            repeat(130) { index ->
                val x = ((index * 37 + 11) % 100) / 100f * size.width
                val y = ((index * 61 + 7) % 100) / 100f * size.height
                val radius = 0.45f + (index % 5) * 0.55f
                val alpha = 0.08f + (index % 6) * 0.045f
                val tint = when {
                    index % 29 == 0 -> Gold
                    index % 17 == 0 -> Lavender
                    else -> Color.White
                }
                drawCircle(
                    color = tint.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y),
                )
            }

            // Небольшое созвездие над главным экраном.
            val constellation = listOf(
                Offset(size.width * 0.58f, size.height * 0.08f),
                Offset(size.width * 0.67f, size.height * 0.12f),
                Offset(size.width * 0.78f, size.height * 0.09f),
                Offset(size.width * 0.86f, size.height * 0.15f),
                Offset(size.width * 0.74f, size.height * 0.19f),
            )
            constellation.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = Gold.copy(alpha = 0.09f),
                    start = start,
                    end = end,
                    strokeWidth = 1.dp.toPx(),
                )
            }
            constellation.forEach { point ->
                drawCircle(
                    color = Gold.copy(alpha = 0.36f),
                    radius = 1.6.dp.toPx(),
                    center = point,
                )
            }

            // Пара ярких звёзд с мягким ореолом.
            listOf(
                Offset(size.width * 0.18f, size.height * 0.26f),
                Offset(size.width * 0.83f, size.height * 0.52f),
                Offset(size.width * 0.34f, size.height * 0.72f),
            ).forEach { point ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Gold.copy(alpha = 0.2f), Color.Transparent),
                        center = point,
                        radius = 18.dp.toPx(),
                    ),
                    radius = 18.dp.toPx(),
                    center = point,
                )
                drawCircle(
                    color = Gold.copy(alpha = 0.55f),
                    radius = 1.4.dp.toPx(),
                    center = point,
                )
            }
        }
        content()
    }
}