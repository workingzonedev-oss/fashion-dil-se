package com.fashiondilse.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = DeepGreen,
    onPrimary = WarmWhite,
    primaryContainer = ForestGreen.copy(alpha = 0.18f),
    onPrimaryContainer = PitchBlack,
    secondary = PitchBlack,
    onSecondary = WarmWhite,
    secondaryContainer = Mist,
    onSecondaryContainer = PitchBlack,
    tertiary = SoftGold,
    onTertiary = PitchBlack,
    background = WarmWhite,
    onBackground = PitchBlack,
    surface = CardWhite,
    onSurface = PitchBlack,
    surfaceVariant = Mist,
    onSurfaceVariant = SoftGray,
    outline = OutlineBeige,
    error = SoftRed,
)

private val DarkColors = darkColorScheme(
    primary = ForestGreen,
    onPrimary = WarmWhite,
    secondary = SoftGold,
    onSecondary = PitchBlack,
    background = PitchBlack,
    onBackground = WarmWhite,
    surface = PitchBlack.copy(alpha = 0.92f),
    onSurface = WarmWhite,
    surfaceVariant = ForestGreen.copy(alpha = 0.18f),
    onSurfaceVariant = WarmWhite.copy(alpha = 0.72f),
    outline = SoftGold.copy(alpha = 0.28f),
    error = SoftRed,
)

private val FashionShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
)

class ThemeState {
    var isDarkMode by mutableStateOf(false)
}

val LocalThemeState = compositionLocalOf { ThemeState() }

@Composable
fun FashionDilSeTheme(
    themeState: ThemeState = LocalThemeState.current,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (themeState.isDarkMode) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FashionTypography,
        shapes = FashionShapes,
        content = content,
    )
}
