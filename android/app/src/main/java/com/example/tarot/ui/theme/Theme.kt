package com.example.tarot.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val MysticColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Midnight,
    primaryContainer = GoldDark,
    onPrimaryContainer = Cream,
    secondary = Lavender,
    onSecondary = Midnight,
    tertiary = Rose,
    onTertiary = Midnight,
    background = Midnight,
    onBackground = Cream,
    surface = DeepSurface,
    onSurface = Cream,
    surfaceVariant = CardSurface,
    onSurfaceVariant = MutedLavender,
    error = Rose,
    onError = Midnight,
    errorContainer = RoseContainer,
    onErrorContainer = Cream,
    outline = Gold.copy(alpha = 0.38f),
    outlineVariant = Lavender.copy(alpha = 0.16f),
    scrim = Midnight,
)

private val MysticShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
)

@Composable
fun MysticTarotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MysticColorScheme,
        typography = MysticTypography,
        shapes = MysticShapes,
        content = content,
    )
}
