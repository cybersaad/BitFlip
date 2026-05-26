package com.bitflip.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Deep dark glassy palette
val BgPrimary    = Color(0xFF0F111A)
val BgSurface    = Color(0xFF181A25)
val BgSurface2   = Color(0xFF222536)
val BorderColor  = Color(0xFF32364A)
val TextPrimary  = Color(0xFFE2E4F0)
val TextMuted    = Color(0xFF8A8FAD)

// Professional, softer accents
val AccentBlue   = Color(0xFF4FA8E0)
val AccentPurple = Color(0xFF987DDF)
val AccentPink   = Color(0xFFE26F9E)
val AccentGreen  = Color(0xFF3CB371)
val AccentRed    = Color(0xFFE06C75)
val AccentAmber  = Color(0xFFD19A66)

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
fun BitFlipTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

// Glassy Modifier utility
fun Modifier.glassCard(
    cornerRadius: Int = 16,
    alpha: Float = 0.4f,
    borderColor: Color = BorderColor
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius.dp))
    .background(BgSurface.copy(alpha = alpha))
    .border(0.5.dp, borderColor, RoundedCornerShape(cornerRadius.dp))

