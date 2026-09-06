package com.afoxplus.emergency.data.repository

import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import com.afoxplus.emergency.domain.repository.EmergencyContactsCountProvider

class EmergencyContactsCountProviderImpl(
    private val emergencyContactRepository: EmergencyContactRepository
) : EmergencyContactsCountProvider {

    override fun getContactsCount(): Int = emergencyContactRepository.getEmergencyContacts().size
}
