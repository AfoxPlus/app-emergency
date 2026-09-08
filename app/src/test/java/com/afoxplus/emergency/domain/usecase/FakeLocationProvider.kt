package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.domain.repository.LocationProvider

class FakeLocationProvider(
    private var coordinates: Coordinates? = null
) : LocationProvider {

    var getCurrentLocationCalled: Boolean = false
        private set

    override fun getCurrentLocation(): Coordinates? {
        getCurrentLocationCalled = true
        return coordinates
    }
}
