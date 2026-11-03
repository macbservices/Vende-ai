package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VendePrimary,
    onPrimary = Color(0xFF00354E),
    primaryContainer = Color(0xFF004D70),
    onPrimaryContainer = Color(0xFFC2E8FF),
    secondary = VendeSecondary,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = VendeSuccess,
    onTertiary = Color(0xFF00391F),
    background = VendeBackgroundDark,
    onBackground = VendeOnBackgroundDark,
    surface = VendeSurfaceDark,
    onSurface = VendeOnSurfaceDark,
    surfaceVariant = VendeSurfaceVariantDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = VendeOutlineDark,
    error = VendeError
)

private val LightColorScheme = lightColorScheme(
    primary = VendePrimaryDark,
    onPrimary = Color.White,
    secondary = VendeSecondary,
    onSecondary = Color.White,
    tertiary = VendeSuccess,
    background = VendeBackgroundLight,
    onBackground = VendeOnBackgroundLight,
    surface = VendeSurfaceLight,
    onSurface = VendeOnSurfaceLight,
    surfaceVariant = VendeSurfaceVariantLight,
    outline = VendeOutlineLight,
    error = VendeError
)

@Composable
fun VendeIaTheme(
    darkTheme: Boolean = true, // Default to dark theme as requested by user prompt
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
