package com.example.worldkids.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp

private val LightColorScheme = lightColorScheme(
    primary          = NavyBlue,
    onPrimary        = SurfaceWhite,
    primaryContainer = NavyMid,
    secondary        = SkyTeal,
    onSecondary      = SurfaceWhite,
    tertiary         = CoralOrange,
    onTertiary       = SurfaceWhite,
    background       = Cream,
    onBackground     = TextMain,
    surface          = SurfaceWhite,
    onSurface        = TextMain,
    surfaceVariant   = SurfaceCard,
    onSurfaceVariant = TextSub,
    outline          = DividerGray
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val BaseTypography = Typography(
    displayLarge  = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,     fontSize = 28.sp, lineHeight = 34.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,     fontSize = 22.sp, lineHeight = 28.sp),
    headlineMedium= TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge    = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleMedium   = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium    = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 13.sp, lineHeight = 19.sp),
    bodySmall     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 11.sp, lineHeight = 16.sp),
    labelLarge    = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium   = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 16.sp)
)

@Composable
fun WorldKidsTheme(tvMode: Boolean = false, content: @Composable () -> Unit) {
    val scale = if (tvMode) 1.28f else 1f
    val typography = if (tvMode) Typography(
        displayLarge   = BaseTypography.displayLarge.copy(fontSize   = (28 * scale).sp),
        headlineLarge  = BaseTypography.headlineLarge.copy(fontSize  = (22 * scale).sp),
        headlineMedium = BaseTypography.headlineMedium.copy(fontSize = (18 * scale).sp),
        titleLarge     = BaseTypography.titleLarge.copy(fontSize     = (16 * scale).sp),
        titleMedium    = BaseTypography.titleMedium.copy(fontSize    = (14 * scale).sp),
        bodyLarge      = BaseTypography.bodyLarge.copy(fontSize      = (15 * scale).sp),
        bodyMedium     = BaseTypography.bodyMedium.copy(fontSize     = (13 * scale).sp),
        bodySmall      = BaseTypography.bodySmall.copy(fontSize      = (11 * scale).sp),
        labelLarge     = BaseTypography.labelLarge.copy(fontSize     = (13 * scale).sp)
    ) else BaseTypography

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = typography,
        shapes      = AppShapes,
        content     = content
    )
}
