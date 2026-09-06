package com.afoxplus.emergency.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.afoxplus.emergency.R
import com.afoxplus.emergency.domain.model.OnboardingPage

/**
 * Default, non-localized onboarding content mirroring the strings in `strings.xml`.
 * Used as the [OnboardingViewModel]'s fallback and in unit tests/previews.
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

/**
 * Builds the localized onboarding pages from string resources, so the copy shown to the
 * user (unlike the default [onboardingPages] used for previews/tests) can be translated.
 */
@Composable
fun rememberOnboardingPages(): List<OnboardingPage> {
    val brand = stringResource(R.string.onboarding_brand)
    val page1Tagline = stringResource(R.string.onboarding_page1_tagline)
    val page1Title = stringResource(R.string.onboarding_page1_feature_title)
    val page1Description = stringResource(R.string.onboarding_page1_feature_description)
    val page2Tagline = stringResource(R.string.onboarding_page2_tagline)
    val page2Title = stringResource(R.string.onboarding_page2_feature_title)
    val page2Description = stringResource(R.string.onboarding_page2_feature_description)
    val page3Tagline = stringResource(R.string.onboarding_page3_tagline)
    val page3Title = stringResource(R.string.onboarding_page3_feature_title)
    val page3Description = stringResource(R.string.onboarding_page3_feature_description)

    return remember(
        brand,
        page1Tagline, page1Title, page1Description,
        page2Tagline, page2Title, page2Description,
        page3Tagline, page3Title, page3Description
    ) {
        listOf(
            OnboardingPage(
                icon = "\uD83D\uDEE1\uFE0F",
                brand = brand,
                tagline = page1Tagline,
                featureTitle = page1Title,
                featureDescription = page1Description
            ),
            OnboardingPage(
                icon = "\uD83D\uDCDE",
                brand = brand,
                tagline = page2Tagline,
                featureTitle = page2Title,
                featureDescription = page2Description
            ),
            OnboardingPage(
                icon = "\uD83D\uDD14",
                brand = brand,
                tagline = page3Tagline,
                featureTitle = page3Title,
                featureDescription = page3Description
            )
        )
    }
}
