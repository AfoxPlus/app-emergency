package com.afoxplus.emergency.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmergencyColors.Brand,
    onPrimary = EmergencyColors.OnPrimary,
    primaryContainer = EmergencyColors.BrandDark,
    onPrimaryContainer = EmergencyColors.OnPrimary,
    secondary = EmergencyColors.Secondary,
    onSecondary = EmergencyColors.OnSecondary,
    secondaryContainer = EmergencyColors.SecondaryContainer,
    onSecondaryContainer = EmergencyColors.OnSecondary,
    tertiary = EmergencyColors.Accent,
    onTertiary = EmergencyColors.OnSurface,
    background = EmergencyColors.Background,
    onBackground = EmergencyColors.OnBackground,
    surface = EmergencyColors.Surface,
    onSurface = EmergencyColors.OnSurface,
    surfaceVariant = EmergencyColors.SurfaceVariant,
    onSurfaceVariant = EmergencyColors.OnSurfaceVariant,
    outline = EmergencyColors.Outline,
    error = EmergencyColors.Error,
    onError = EmergencyColors.OnPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = EmergencyColors.Primary,
    onPrimary = EmergencyColors.OnPrimary,
    primaryContainer = EmergencyColors.PrimaryContainer,
    onPrimaryContainer = EmergencyColors.OnSurface,
    secondary = EmergencyColors.Secondary,
    onSecondary = EmergencyColors.OnSecondary,
    secondaryContainer = EmergencyColors.SecondaryContainer,
    onSecondaryContainer = EmergencyColors.OnSurface,
    tertiary = EmergencyColors.Accent,
    onTertiary = EmergencyColors.OnSurface,
    background = EmergencyColors.Background,
    onBackground = EmergencyColors.OnBackground,
    surface = EmergencyColors.Surface,
    onSurface = EmergencyColors.OnSurface,
    surfaceVariant = EmergencyColors.SurfaceVariant,
    onSurfaceVariant = EmergencyColors.OnSurfaceVariant,
    outline = EmergencyColors.Outline,
    error = EmergencyColors.Error,
    onError = EmergencyColors.OnPrimary
)

@Composable
fun AppemergencyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EmergencyTypography,
        shapes = AppShapes,
        content = content
    )
}