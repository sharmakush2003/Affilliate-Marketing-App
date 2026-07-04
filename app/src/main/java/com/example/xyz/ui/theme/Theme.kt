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
    primary             = AmazonOrange,
    onPrimary           = NavyDark,
    primaryContainer    = OrangeLight,
    onPrimaryContainer  = GoldHex,
    secondary           = DarkGreen,
    onSecondary         = White,
    secondaryContainer  = LightGreen,
    onSecondaryContainer = DarkGreen,
    background          = GrayBackground,
    surface             = CardSurface,
    surfaceVariant      = SectionBg,
    onBackground        = TextDark,
    onSurface           = TextDark,
    onSurfaceVariant    = TextGray,
    outline             = BorderColor,
    error               = ErrorRed,
)

private val DarkColorScheme = darkColorScheme(
    primary             = AmazonOrange,
    onPrimary           = NavyDark,
    secondary           = DarkGreen,
    onSecondary         = White,
    background          = Color(0xFF0F1111),
    surface             = Color(0xFF1A1D1D),
    onBackground        = White,
    onSurface           = White,
    outline             = Color(0xFF3D4343),
    error               = ErrorRed,
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
            window.statusBarColor = NavyDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
