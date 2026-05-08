package com.mars.madereraapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Único esquema de colores — Dark Mode Premium
private val AppColorScheme = darkColorScheme(
    primary            = PrimaryBlue,
    onPrimary          = TextOnPrimary,
    primaryContainer   = PrimaryDark,
    onPrimaryContainer = TextPrimary,
    secondary          = SecondaryGreen,
    onSecondary        = TextOnPrimary,
    secondaryContainer = Color(0xFF064E3B),  // Emerald 900
    onSecondaryContainer = TextPrimary,
    background         = BackgroundDark,
    onBackground       = TextPrimary,
    surface            = SurfaceDark,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = TextSecondary,
    outline            = BorderColor,
    error              = ColorError,
    onError            = TextOnPrimary,
)

@Composable
fun MadereraAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}