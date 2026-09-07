package com.afoxplus.emergency.presentation.onboarding

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.EmergencyContact
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository

class FakeEmergencyContactRepository : EmergencyContactRepository {
    val addedContacts = mutableListOf<Contact>()

    override fun getEmergencyContacts(): List<EmergencyContact> = emptyList()

    override fun isEmergencyContact(contactId: String): Boolean =
        addedContacts.any { it.id == contactId }

    override fun addEmergencyContact(contact: Contact): Boolean {
        addedContacts += contact
        return true
    }

    override fun removeEmergencyContact(contactId: String) {
        addedContacts.removeAll { it.id == contactId }
    }
}
