package com.mars.madereraapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Único esquema de colores — Dark Mode Premium
private val AppColorScheme = darkColorScheme(
    primary            = PrimaryAmber,
    onPrimary          = TextOnPrimary,
    primaryContainer   = PrimaryGold,
    onPrimaryContainer = TextOnPrimary,
    secondary          = PrimaryGold,
    onSecondary        = TextOnPrimary,
    secondaryContainer = SurfaceContainer,
    onSecondaryContainer = TextPrimary,
    background         = BackgroundDark,
    onBackground       = TextPrimary,
    surface            = SurfaceDark,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceContainer,
    onSurfaceVariant   = TextSecondary,
    outline            = GlassWhite,
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