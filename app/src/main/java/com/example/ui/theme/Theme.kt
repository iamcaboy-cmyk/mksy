package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PortalNavy,
    onPrimary = Color.White,
    primaryContainer = PortalBlueLight,
    onPrimaryContainer = PortalNavyDark,
    secondary = PortalSaffron,
    onSecondary = Color.White,
    secondaryContainer = PortalSaffronLight,
    onSecondaryContainer = Color(0xFF7C2D12),
    tertiary = PortalGreen,
    onTertiary = Color.White,
    tertiaryContainer = PortalGreenLight,
    onTertiaryContainer = Color(0xFF052E16),
    background = BackgroundLight,
    onBackground = NeutralDark,
    surface = SurfaceLight,
    onSurface = NeutralDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = NeutralMedium,
    outline = BorderStroke,
    error = PortalRed,
    onError = Color.White,
    errorContainer = PortalRedLight,
    onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFFFB923C),
    onSecondary = Color(0xFF431407),
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Color(0xFFFFEDD5),
    tertiary = Color(0xFF4ADE80),
    onTertiary = Color(0xFF052E16),
    background = Color(0xFF0B132B),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1C2541),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF243054),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF3B4D78)
)

@Composable
fun JanSahayataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
