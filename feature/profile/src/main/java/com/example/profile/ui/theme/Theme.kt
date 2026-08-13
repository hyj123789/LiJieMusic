package com.example.profile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColors(
    primary = AccentGreen,
    onPrimary = Color(0xFFFFFFFF),
    secondary = AccentGreen,
    background = PlayerBackgroundLight,
    surface = PlayerBackgroundLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
)

private val DarkColorScheme = darkColors(
    primary = AccentGreen,
    onPrimary = Color(0xFF000000),
    secondary = AccentGreen,
    background = PlayerBackgroundDark,
    surface = PlayerBackgroundDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
)

@Composable
fun LiJieMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}