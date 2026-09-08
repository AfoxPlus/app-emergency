package com.afoxplus.emergency.domain.model

/**
 * Geographic coordinates captured from the device's current location.
 *
 * [accuracyMeters] is the estimated GPS precision radius reported by the location provider,
 * shown alongside the raw coordinates on the Alert History screen (e.g. "Precisión ±4m").
 * It is `null` when the provider does not report an accuracy value.
 */
data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null
)
