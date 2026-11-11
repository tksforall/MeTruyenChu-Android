package com.example.metruyenchu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    secondary = md_theme_dark_secondary,
    tertiary = md_theme_dark_tertiary,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    onPrimary = md_theme_dark_tertiary,
    onSecondary = Color.White,
    onTertiary = md_theme_dark_onTertiary,
    onBackground = md_theme_dark_onBackground,
    onSurface = md_theme_dark_onSurface,
)

@Composable
fun MeTruyenChuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}