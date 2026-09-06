package com.afoxplus.emergency.presentation.periodiccheck

import com.afoxplus.emergency.domain.repository.EmergencyContactsCountProvider

/**
 * In-memory fake used to unit test [PeriodicCheckViewModel] without any Android dependency.
 */
class FakeEmergencyContactsCountProvider(
    private var count: Int = 0
) : EmergencyContactsCountProvider {

    override fun getContactsCount(): Int = count

    fun setContactsCount(value: Int) {
        count = value
    }
}
