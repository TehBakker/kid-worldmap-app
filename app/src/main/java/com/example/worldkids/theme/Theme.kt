package com.example.worldkids.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColorScheme = lightColorScheme(
    primary = OceanDeep,
    secondary = GrassGreen,
    tertiary = AccentOrange,
    background = CreamBackground,
    surface = CardWhite,
    onPrimary = CardWhite,
    onSecondary = CardWhite,
    onBackground = TextDark,
    onSurface = TextDark
)

private val BaseTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
)

@Composable
fun WorldKidsTheme(tvMode: Boolean = false, content: @Composable () -> Unit) {
    val scale = if (tvMode) 1.35f else 1f
    val scaledTypography = Typography(
        displayLarge = BaseTypography.displayLarge.copy(fontSize = (32 * scale).sp),
        headlineLarge = BaseTypography.headlineLarge.copy(fontSize = (24 * scale).sp),
        headlineMedium = BaseTypography.headlineMedium.copy(fontSize = (20 * scale).sp),
        titleLarge = BaseTypography.titleLarge.copy(fontSize = (18 * scale).sp),
        titleMedium = BaseTypography.titleMedium.copy(fontSize = (16 * scale).sp),
        bodyLarge = BaseTypography.bodyLarge.copy(fontSize = (16 * scale).sp),
        bodyMedium = BaseTypography.bodyMedium.copy(fontSize = (14 * scale).sp),
        bodySmall = BaseTypography.bodySmall.copy(fontSize = (12 * scale).sp),
        labelLarge = BaseTypography.labelLarge.copy(fontSize = (14 * scale).sp)
    )
    MaterialTheme(colorScheme = LightColorScheme, typography = scaledTypography, content = content)
}
