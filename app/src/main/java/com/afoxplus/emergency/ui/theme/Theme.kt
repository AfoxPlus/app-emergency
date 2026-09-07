package com.afoxplus.emergency.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmergencyColors.InversePrimary,
    onPrimary = EmergencyColors.OnPrimary,
    primaryContainer = EmergencyColors.PrimaryContainer,
    onPrimaryContainer = EmergencyColors.OnPrimaryContainer,
    secondary = EmergencyColors.Secondary,
    onSecondary = EmergencyColors.OnSecondary,
    secondaryContainer = EmergencyColors.SecondaryContainer,
    onSecondaryContainer = EmergencyColors.OnSecondaryContainer,
    tertiary = EmergencyColors.Tertiary,
    onTertiary = EmergencyColors.OnTertiary,
    tertiaryContainer = EmergencyColors.TertiaryContainer,
    onTertiaryContainer = EmergencyColors.OnTertiaryContainer,
    background = Color(0xFF101113),
    onBackground = EmergencyColors.InverseOnSurface,
    surface = Color(0xFF101113),
    onSurface = EmergencyColors.InverseOnSurface,
    surfaceVariant = Color(0xFF45474A),
    onSurfaceVariant = EmergencyColors.OnSurfaceVariant,
    outline = EmergencyColors.OutlineVariant,
    error = EmergencyColors.Error,
    onError = EmergencyColors.OnError
)

private val LightColorScheme = lightColorScheme(
    primary = EmergencyColors.Primary,
    onPrimary = EmergencyColors.OnPrimary,
    primaryContainer = EmergencyColors.PrimaryContainer,
    onPrimaryContainer = EmergencyColors.OnPrimaryContainer,
    secondary = EmergencyColors.Secondary,
    onSecondary = EmergencyColors.OnSecondary,
    secondaryContainer = EmergencyColors.SecondaryContainer,
    onSecondaryContainer = EmergencyColors.OnSecondaryContainer,
    tertiary = EmergencyColors.Tertiary,
    onTertiary = EmergencyColors.OnTertiary,
    tertiaryContainer = EmergencyColors.TertiaryContainer,
    onTertiaryContainer = EmergencyColors.OnTertiaryContainer,
    background = EmergencyColors.Background,
    onBackground = EmergencyColors.OnBackground,
    surface = EmergencyColors.Surface,
    onSurface = EmergencyColors.OnSurface,
    surfaceVariant = EmergencyColors.SurfaceVariant,
    onSurfaceVariant = EmergencyColors.OnSurfaceVariant,
    outline = EmergencyColors.Outline,
    error = EmergencyColors.Error,
    onError = EmergencyColors.OnError
)

@Composable
fun AppemergencyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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