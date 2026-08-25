package com.dunoetoktok.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Base sizes are already larger than Material defaults (senior-friendly baseline);
 * [scaledTypography] multiplies every size further by the user's chosen [TextScale].
 */
private val baseTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 22.sp),
)

fun scaledTypography(multiplier: Float): Typography = Typography(
    headlineMedium = baseTypography.headlineMedium.scaled(multiplier),
    headlineSmall = baseTypography.headlineSmall.scaled(multiplier),
    titleLarge = baseTypography.titleLarge.scaled(multiplier),
    titleMedium = baseTypography.titleMedium.scaled(multiplier),
    bodyLarge = baseTypography.bodyLarge.scaled(multiplier),
    bodyMedium = baseTypography.bodyMedium.scaled(multiplier),
    labelLarge = baseTypography.labelLarge.scaled(multiplier),
)

private fun TextStyle.scaled(multiplier: Float): TextStyle = copy(
    fontSize = fontSize * multiplier,
    lineHeight = lineHeight * multiplier,
)
