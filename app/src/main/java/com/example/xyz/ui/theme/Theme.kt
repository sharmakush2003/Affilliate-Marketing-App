package com.example.xyz.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    onPrimary = White,
    secondary = LightGreen,
    onSecondary = DarkGreen,
    background = GrayBackground,
    surface = White,
    onBackground = TextDark,
    onSurface = TextDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    onPrimary = White,
    secondary = LightGreen,
    onSecondary = DarkGreen,
    background = TextDark,
    surface = Color(0xFF2C2C2C),
    onBackground = White,
    onSurface = White,
)

@Composable
fun XYZTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
