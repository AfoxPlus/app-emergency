package com.afoxplus.emergency.presentation.ui.theme

import androidx.compose.ui.graphics.Color

object EmergencyColors {
    val Surface = Color(0xFFF8F9FB)
    val SurfaceDim = Color(0xFFD9DADC)
    val SurfaceBright = Color(0xFFF8F9FB)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF3F4F6)
    val SurfaceContainer = Color(0xFFEDEEF0)
    val SurfaceContainerHigh = Color(0xFFE7E8EA)
    val SurfaceContainerHighest = Color(0xFFE1E2E4)
    val OnSurface = Color(0xFF191C1E)
    val OnSurfaceVariant = Color(0xFF454652)
    val InverseSurface = Color(0xFF2E3132)
    val InverseOnSurface = Color(0xFFF0F1F3)
    val Outline = Color(0xFF767683)
    val OutlineVariant = Color(0xFFC6C5D4)
    val SurfaceTint = Color(0xFF4C56AF)
    val Primary = Color(0xFF000666)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFF1A237E)
    val OnPrimaryContainer = Color(0xFF8690EE)
    val InversePrimary = Color(0xFFBDC2FF)
    val Secondary = Color(0xFF006B5E)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFF94F0DF)
    val OnSecondaryContainer = Color(0xFF006F62)
    val Tertiary = Color(0xFF400003)
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFF670007)
    val OnTertiaryContainer = Color(0xFFFF635A)
    val Error = Color(0xFFBA1A1A)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF93000A)
    val Background = Surface
    val OnBackground = OnSurface

    // Compatibility aliases for feature code.
    val Brand = Error
    val BrandDark = OnErrorContainer
    val BrandLight = OnErrorContainer
    val BrandContainer = ErrorContainer
    val Accent = Tertiary
    val AccentContainer = TertiaryContainer
    val Success = Secondary
    val SuccessContainer = SecondaryContainer
    val Warning = Tertiary
    val WarningContainer = TertiaryContainer
}
