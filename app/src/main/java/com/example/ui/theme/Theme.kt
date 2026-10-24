package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FitAdminLightColorScheme = lightColorScheme(
    primary = CharcoalDark,
    onPrimary = Color.White,
    primaryContainer = CharcoalSurface,
    onPrimaryContainer = Color.White,
    secondary = ElectricLime,
    onSecondary = CharcoalDark,
    secondaryContainer = ElectricLimeContainer,
    onSecondaryContainer = ElectricLimeDark,
    tertiary = AlertRed,
    onTertiary = Color.White,
    tertiaryContainer = AlertRedContainer,
    onTertiaryContainer = AlertRedDark,
    background = BackgroundLight,
    onBackground = TextPrimaryDark,
    surface = SurfaceWhite,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryMuted,
    outline = BorderSubtle,
    error = AlertRed,
    onError = Color.White
)

private val FitAdminDarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = CharcoalDark,
    primaryContainer = CharcoalDark,
    onPrimaryContainer = ElectricLimeLight,
    secondary = ElectricLimeLight,
    onSecondary = CharcoalDark,
    secondaryContainer = CharcoalSurface,
    onSecondaryContainer = ElectricLime,
    tertiary = AlertRed,
    onTertiary = Color.White,
    background = CharcoalDark,
    onBackground = Color.White,
    surface = CharcoalSurface,
    onSurface = Color.White,
    surfaceVariant = CharcoalBorder,
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = CharcoalBorder,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded FitAdmin colors consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) FitAdminDarkColorScheme else FitAdminLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
