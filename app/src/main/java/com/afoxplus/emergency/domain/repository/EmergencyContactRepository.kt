package com.afoxplus.emergency.domain.repository

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.EmergencyContact

/**
 * Persistence contract for the list of Emergency Contacts.
 */
interface EmergencyContactRepository {
    fun getEmergencyContacts(): List<EmergencyContact>
    fun isEmergencyContact(contactId: String): Boolean
    fun addEmergencyContact(contact: Contact): Boolean
    fun removeEmergencyContact(contactId: String)
}
