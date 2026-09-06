package com.afoxplus.emergency.data.repository

import android.content.Context
import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.EmergencyContact
import com.afoxplus.emergency.domain.model.EmergencyContactType
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
internal data class StoredEmergencyContact(
    val contactId: String,
    val name: String,
    val phoneNumber: String
)

class EmergencyContactRepositoryImpl(context: Context) : EmergencyContactRepository {

    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getEmergencyContacts(): List<EmergencyContact> =
        readEntries().mapIndexed { index, entry ->
            EmergencyContact(
                contactId = entry.contactId,
                name = entry.name,
                phoneNumber = entry.phoneNumber,
                type = if (index == 0) EmergencyContactType.PRIMARY else EmergencyContactType.BACKUP
            )
        }

    override fun isEmergencyContact(contactId: String): Boolean =
        readEntries().any { it.contactId == contactId }

    override fun addEmergencyContact(contact: Contact): Boolean {
        val entries = readEntries()
        if (entries.any { it.contactId == contact.id }) return false

        writeEntries(entries + StoredEmergencyContact(contact.id, contact.name, contact.phoneNumber))
        return true
    }

    override fun removeEmergencyContact(contactId: String) {
        writeEntries(readEntries().filterNot { it.contactId == contactId })
    }

    private fun readEntries(): List<StoredEmergencyContact> {
        val json = sharedPreferences.getString(KEY_EMERGENCY_CONTACTS, null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun writeEntries(entries: List<StoredEmergencyContact>) {
        sharedPreferences.edit()
            .putString(KEY_EMERGENCY_CONTACTS, Json.encodeToString(entries))
            .apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "emergency_contacts_preferences"
        private const val KEY_EMERGENCY_CONTACTS = "emergency_contacts"
    }
}
