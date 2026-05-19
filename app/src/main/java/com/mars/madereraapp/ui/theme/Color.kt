package com.mars.madereraapp.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────
//  Minimalist Light Palette — Madera Poltand
// ─────────────────────────────────────────────────

// Backgrounds & Surfaces
val BackgroundLight    = Color(0xFFF8F7F4)   // Warm white
val SurfaceLight       = Color(0xFFFFFFFF)   // Pure white cards
val SurfaceContainer   = Color(0xFFF0EFEC)   // Secondary surface
val SurfaceHigh        = Color(0xFFE8E6E3)   // Dividers / elevated

// Brand / Accents
val PrimaryWood        = Color(0xFF8B6914)   // Warm wood brown
val PrimaryWoodDark    = Color(0xFF6B4F0E)   // Darker variant
val PrimaryWoodLight   = Color(0xFFC49A2A)   // Lighter gold-wood
val AccentSoft         = Color(0xFFEDE6D6)   // Soft warm accent bg
val AccentWarm         = Color(0xFFFFF8E8)   // Very light warm bg

// Text
val TextPrimary        = Color(0xFF1A1A1A)   // Near-black
val TextSecondary      = Color(0xFF6B6B6B)   // Medium gray
val TextTertiary       = Color(0xFF9E9E9E)   // Light gray
val TextOnPrimary      = Color(0xFFFFFFFF)   // White on primary

// Borders & Dividers
val DividerColor       = Color(0xFFE8E6E3)   // Subtle divider
val BorderLight        = Color(0xFFD9D6D0)   // Light border

// Status
val ColorPending       = Color(0xFFF59E0B)   // Warm amber
val ColorApproved      = Color(0xFF10B981)   // Emerald green
val ColorRejected      = Color(0xFFEF4444)   // Soft red
val ColorError         = Color(0xFFDC2626)   // Error red

// Status Chips (backwards compat)
val StatusPendiente    = ColorPending
val StatusParcial      = Color(0xFFE88B00)   // Darker amber
val StatusCompletado   = ColorApproved

// ──── Legacy aliases for gradual migration ────
// These map old names to new colors so references don't break
val BackgroundDark     = BackgroundLight
val SurfaceDark        = SurfaceLight
val PrimaryAmber       = PrimaryWood
val PrimaryGold        = PrimaryWoodLight
val AccentAmber        = AccentSoft
val TextOnPrimaryLegacy = Color(0xFFFFFFFF)
val GlassWhite         = BorderLight
val GlassSurface       = SurfaceContainer