package com.saittan.turkischlernen.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val KidColorScheme = lightColorScheme(
    primary = Color(0xFFFF6B6B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8D8),
    onPrimaryContainer = Color(0xFF410002),
    secondary = Color(0xFF4ECDC4),
    onSecondary = Color.White,
    tertiary = Color(0xFFFFD166),
    background = Color(0xFFFFF8E7),
    onBackground = Color(0xFF2B2B2B),
    surface = Color.White,
    onSurface = Color(0xFF2B2B2B),
    surfaceVariant = Color(0xFFFFE9D6),
    error = Color(0xFFE76F51),
)

private val KidShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp),
)

private val KidTypography = Typography(
    displayLarge = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Black),
    headlineLarge = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    bodyMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
)

@Composable
fun TurkischLernenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KidColorScheme,
        typography = KidTypography,
        shapes = KidShapes,
        content = content,
    )
}
