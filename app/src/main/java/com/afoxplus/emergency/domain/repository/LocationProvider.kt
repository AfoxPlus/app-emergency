package com.afoxplus.emergency.domain.repository

import com.afoxplus.emergency.domain.model.Coordinates

/**
 * Abstraction for capturing the device's current location. Implementations must return
 * `null` when location permissions are not granted or the coordinates cannot be resolved
 * (e.g. GPS disabled), so callers can proceed without blocking on location availability.
 */
interface LocationProvider {
    fun getCurrentLocation(): Coordinates?
}
