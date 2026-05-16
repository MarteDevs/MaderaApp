package com.mars.madereraapp.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────
//  Industrial Excellence Palette — Madera Poltand
// ─────────────────────────────────────────────────

// Backgrounds & Surfaces
val BackgroundDark   = Color(0xFF131313)   // Deep charcoal
val SurfaceDark      = Color(0xFF1B1B1C)   // Surface Low
val SurfaceContainer = Color(0xFF202020)   // Surface Normal
val SurfaceHigh      = Color(0xFF2A2A2A)   // Surface High

// Brand / Accents
val PrimaryAmber     = Color(0xFFFFBF00)   // Industrial Amber
val PrimaryGold      = Color(0xFFD4AF37)   // Premium Gold
val AccentAmber      = Color(0xFFFFE2AB)   // Light Amber

// Text
val TextPrimary      = Color(0xFFE5E2E1)   // Light gray
val TextSecondary    = Color(0xFFD4C5AB)   // Muted sand/gold
val TextOnPrimary    = Color(0xFF402D00)   // Dark contrast for amber

// Glass Effects
val GlassWhite       = Color(0x33FFFFFF)   // 20% white for borders
val GlassSurface     = Color(0x4D1B1B1C)   // 30% semi-transparent surface

// Status
val ColorPending     = Color(0xFFFFBF00)   // Amber
val ColorApproved    = Color(0xFF81C784)   // Soft green
val ColorRejected    = Color(0xFFE57373)   // Soft red
val ColorError       = Color(0xFFFFB4AB)   // Red 500

// Status Chips (for backwards compatibility if needed)
val StatusPendiente  = PrimaryAmber
val StatusParcial    = Color(0xFFFBC02D)   // Darker amber
val StatusCompletado = ColorApproved