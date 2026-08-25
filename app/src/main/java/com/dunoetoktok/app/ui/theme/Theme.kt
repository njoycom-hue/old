package com.dunoetoktok.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = Orange40,
    onPrimary = Surface,
    primaryContainer = Orange80,
    onPrimaryContainer = TextPrimary,
    secondary = Orange60,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Background,
    onSurfaceVariant = TextSecondary,
    error = GameAccent.Red,
)

@Composable
fun DunoeTokTokTheme(
    textScaleMultiplier: Float = 1.0f,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = scaledTypography(textScaleMultiplier),
        content = content,
    )
}
