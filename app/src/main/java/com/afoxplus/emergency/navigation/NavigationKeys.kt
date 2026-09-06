package com.afoxplus.emergency.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute : EmergencyNavKey

@Serializable
data object HomeRoute : EmergencyNavKey

@Serializable
data object RegisterRoute : EmergencyNavKey
