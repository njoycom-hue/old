package com.dunoetoktok.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.dunoetoktok.app.model.GameType

/**
 * Each game gets its own two-tone gradient so the home screen reads as distinct "worlds", not a form.
 * Both stops are kept mid-to-dark on purpose (never pale/pastel) so white text stays readable anywhere
 * on the gradient without relying on exact text placement.
 */
fun GameType.gradientColors(): List<Color> = when (this) {
    GameType.MEMORY -> listOf(Color(0xFFF7971E), Color(0xFFFF5858))
    GameType.SEQUENCE -> listOf(Color(0xFF396AFC), Color(0xFF2948FF))
    GameType.MATH -> listOf(Color(0xFF0BA360), Color(0xFF3CBA92))
    GameType.ODD_WORD -> listOf(Color(0xFFFF758C), Color(0xFFB721FF))
}

fun GameType.gradientBrush(): Brush = Brush.linearGradient(gradientColors())

/** The app's own brand gradient, used for the home screen's level/streak header. */
val BrandGradient: Brush = Brush.linearGradient(listOf(Orange40, Orange60))

/** A darker shade of the same color, used as the "3D depth" layer under chunky buttons/cards. */
fun Color.darken(factor: Float = 0.78f): Color = copy(
    red = red * factor,
    green = green * factor,
    blue = blue * factor,
)
