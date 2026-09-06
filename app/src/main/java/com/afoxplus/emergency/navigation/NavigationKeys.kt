package com.afoxplus.emergency.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute : EmergencyNavKey

@Serializable
data object HomeRoute : EmergencyNavKey

@Serializable
data object RegisterRoute : EmergencyNavKey

@Serializable
data object LoginRoute : EmergencyNavKey

@Serializable
data object ContactsRoute : EmergencyNavKey

@Serializable
data object PeriodicCheckRoute : EmergencyNavKey
