package com.afoxplus.emergency.presentation.contacts

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Persistence contract for the list of Emergency Contacts.
 *
 * The role ([EmergencyContactType]) is derived from the order contacts were added: the
 * first contact added is the [EmergencyContactType.PRIMARY] contact, every other one is a
 * [EmergencyContactType.BACKUP]. This keeps role reassignment automatic when the Primary
 * contact is removed (BR10).
 *
 * Kept as an interface so [ContactsViewModel] can be unit tested without any Android
 * framework dependency, following the app's separation between UI state and persistence.
 */
interface EmergencyContactRepository {
    fun getEmergencyContacts(): List<EmergencyContact>
    fun isEmergencyContact(contactId: String): Boolean

    /**
     * Adds [contact] as an Emergency Contact. Returns `false` without changing anything when
     * the contact is already registered (BR05).
     */
    fun addEmergencyContact(contact: Contact): Boolean
    fun removeEmergencyContact(contactId: String)
}

@Serializable
internal data class StoredEmergencyContact(
    val contactId: String,
    val name: String,
    val phoneNumber: String
)

/**
 * [EmergencyContactRepository] implementation backed by [android.content.SharedPreferences],
 * the persistence mechanism already used by the rest of the app (see
 * `OnboardingPreferencesImpl` and `RegistrationPreferencesImpl`).
 */
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
