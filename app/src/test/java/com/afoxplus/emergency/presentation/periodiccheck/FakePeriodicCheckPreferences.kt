package com.afoxplus.emergency.presentation.periodiccheck

import com.afoxplus.emergency.domain.model.PeriodicCheckConfiguration
import com.afoxplus.emergency.domain.repository.PeriodicCheckPreferences

/**
 * In-memory fake used to unit test [PeriodicCheckViewModel] without any Android dependency.
 */
class FakePeriodicCheckPreferences(
    initialConfiguration: PeriodicCheckConfiguration = PeriodicCheckConfiguration()
) : PeriodicCheckPreferences {

    var storedConfiguration: PeriodicCheckConfiguration = initialConfiguration
        private set

    var saveCallCount: Int = 0
        private set

    override fun getConfiguration(): PeriodicCheckConfiguration = storedConfiguration

    override fun saveConfiguration(configuration: PeriodicCheckConfiguration) {
        this.storedConfiguration = configuration
        saveCallCount++
    }
}
