package com.mars.madereraapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores — Light Mode Minimalista
private val AppColorScheme = lightColorScheme(
    primary            = PrimaryWood,
    onPrimary          = TextOnPrimary,
    primaryContainer   = AccentSoft,
    onPrimaryContainer = PrimaryWoodDark,
    secondary          = PrimaryWoodLight,
    onSecondary        = TextOnPrimary,
    secondaryContainer = AccentWarm,
    onSecondaryContainer = TextPrimary,
    background         = BackgroundLight,
    onBackground       = TextPrimary,
    surface            = SurfaceLight,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceContainer,
    onSurfaceVariant   = TextSecondary,
    outline            = BorderLight,
    outlineVariant     = DividerColor,
    error              = ColorError,
    onError            = TextOnPrimary,
    surfaceContainerLow = BackgroundLight,
    surfaceContainer   = SurfaceContainer,
    surfaceContainerHigh = SurfaceHigh,
)


@Composable
fun MadereraAppTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Light status bar with matching background
            window.statusBarColor = BackgroundLight.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}