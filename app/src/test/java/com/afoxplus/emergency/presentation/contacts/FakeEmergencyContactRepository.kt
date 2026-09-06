package com.afoxplus.emergency.presentation.contacts

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.EmergencyContact
import com.afoxplus.emergency.domain.model.EmergencyContactType
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository

/**
 * In-memory [EmergencyContactRepository] used in tests. Mirrors the same ordering rules as
 * [EmergencyContactRepositoryImpl]: the role is derived from insertion order, so the first
 * contact added is [EmergencyContactType.PRIMARY] and every other one is
 * [EmergencyContactType.BACKUP].
 */
class FakeEmergencyContactRepository(
    initialContacts: List<Contact> = emptyList()
) : EmergencyContactRepository {

    private val entries = mutableListOf<Contact>().apply { addAll(initialContacts) }

    override fun getEmergencyContacts(): List<EmergencyContact> =
        entries.mapIndexed { index, contact ->
            EmergencyContact(
                contactId = contact.id,
                name = contact.name,
                phoneNumber = contact.phoneNumber,
                type = if (index == 0) EmergencyContactType.PRIMARY else EmergencyContactType.BACKUP
            )
        }

    override fun isEmergencyContact(contactId: String): Boolean =
        entries.any { it.id == contactId }

    override fun addEmergencyContact(contact: Contact): Boolean {
        if (entries.any { it.id == contact.id }) return false
        entries += contact
        return true
    }

    override fun removeEmergencyContact(contactId: String) {
        entries.removeAll { it.id == contactId }
    }
}
