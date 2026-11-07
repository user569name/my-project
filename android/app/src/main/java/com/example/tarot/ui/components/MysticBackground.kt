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
import com.example.tarot.ui.theme.DeepNight
import com.example.tarot.ui.theme.Midnight

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
                    colors = listOf(Midnight, DeepNight, Midnight),
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(70) { index ->
                val x = ((index * 37) % 100) / 100f * size.width
                val y = ((index * 61) % 100) / 100f * size.height
                val radius = 0.8f + (index % 4) * 0.7f
                val alpha = 0.10f + (index % 5) * 0.04f
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y),
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6D5AA8).copy(alpha = 0.22f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.82f, size.height * 0.08f),
                    radius = size.width * 0.75f,
                ),
                radius = size.width * 0.75f,
                center = Offset(size.width * 0.82f, size.height * 0.08f),
            )
        }
        content()
    }
}
