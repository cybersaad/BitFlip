package com.numberconverter.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// AMOLED dark palette
val BgPrimary    = Color(0xFF0D0E14)
val BgSurface    = Color(0xFF13141C)
val BgSurface2   = Color(0xFF1A1C27)
val BorderColor  = Color(0xFF2A2D3E)
val TextPrimary  = Color(0xFFE8EAF4)
val TextMuted    = Color(0xFF7B7F9E)

val AccentBlue   = Color(0xFF6EE7FF)
val AccentPurple = Color(0xFFA78BFA)
val AccentPink   = Color(0xFFF472B6)
val AccentGreen  = Color(0xFF34D399)
val AccentRed    = Color(0xFFF87171)

private val DarkColorScheme = darkColorScheme(
    primary        = AccentBlue,
    secondary      = AccentPurple,
    tertiary       = AccentPink,
    background     = BgPrimary,
    surface        = BgSurface,
    onPrimary      = BgPrimary,
    onSecondary    = BgPrimary,
    onBackground   = TextPrimary,
    onSurface      = TextPrimary,
    outline        = BorderColor,
    error          = AccentRed,
)

@Composable
fun NumberConverterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
