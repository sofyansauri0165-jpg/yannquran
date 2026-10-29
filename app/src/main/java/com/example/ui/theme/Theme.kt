package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = ForestDark,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = ForestDark,
    background = BackgroundDark,
    onBackground = WarmWhite,
    surface = SurfaceDarkGreen,
    onSurface = WarmWhite,
    surfaceVariant = ForestDark,
    onSurfaceVariant = DarkMutedText,
    outline = AyahBorderGold
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = WarmWhite,
    primaryContainer = ForestDark,
    onPrimaryContainer = GoldLight,
    secondary = GoldAccent,
    onSecondary = ForestDark,
    background = WarmWhite,
    onBackground = ForestDark,
    surface = Color.White,
    onSurface = ForestDark,
    surfaceVariant = CreamSurface,
    onSurfaceVariant = MutedText,
    outline = AyahBorderGold
)

@Composable
fun YannQuranTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
