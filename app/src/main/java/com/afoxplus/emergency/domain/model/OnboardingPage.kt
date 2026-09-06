package com.afoxplus.emergency.domain.model

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
