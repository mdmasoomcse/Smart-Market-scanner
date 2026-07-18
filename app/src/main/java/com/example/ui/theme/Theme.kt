package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FrostedColorScheme = darkColorScheme(
    primary = EmeraldAccent,
    secondary = IndigoAccent,
    tertiary = Color(0xFF10B981),
    background = FrostedBg,
    surface = Color(0x13FFFFFF), // Frosted glass-like translucency
    surfaceVariant = Color(0x22FFFFFF),
    onPrimary = Color(0xFF0F172A),
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = TextSlate,
    outline = FrostedCardBorder,
    error = Color(0xFFF87171)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for the immersive slate experience
    dynamicColor: Boolean = false, // Disable dynamic colors to ensure frosted aesthetic
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = FrostedColorScheme,
        typography = Typography,
        content = content
    )
}
