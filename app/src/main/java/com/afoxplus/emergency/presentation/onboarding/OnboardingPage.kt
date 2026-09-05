package com.afoxplus.emergency.presentation.onboarding

/**
 * Content displayed on a single onboarding page/card.
 */
data class OnboardingPage(
    val icon: String,
    val brand: String,
    val tagline: String,
    val featureTitle: String,
    val featureDescription: String
)

/**
 * Ordered onboarding content shown to first-time users.
 */
val onboardingPages: List<OnboardingPage> = listOf(
    OnboardingPage(
        icon = "\uD83D\uDEE1\uFE0F",
        brand = "SafeGuard",
        tagline = "Tu seguridad, siempre contigo.",
        featureTitle = "Protección Activa",
        featureDescription = "SafeGuard activa alertas automáticamente cuando necesitas " +
            "ayuda, manteniéndote conectado con tus contactos de emergencia."
    ),
    OnboardingPage(
        icon = "\uD83D\uDCDE",
        brand = "SafeGuard",
        tagline = "Ayuda a un toque de distancia.",
        featureTitle = "Contactos de Emergencia",
        featureDescription = "Guarda a tus familiares y amigos de confianza para notificarlos " +
            "de forma inmediata ante cualquier situación de riesgo."
    ),
    OnboardingPage(
        icon = "\uD83D\uDD14",
        brand = "SafeGuard",
        tagline = "Siempre alerta, siempre seguro.",
        featureTitle = "Alertas en Tiempo Real",
        featureDescription = "Comparte tu ubicación en tiempo real para que la ayuda llegue " +
            "exactamente a donde la necesitas."
    )
)
