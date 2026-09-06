package com.afoxplus.emergency.presentation.contacts

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.EmergencyContact

/**
 * Contact permission lifecycle as observed by the [ContactsScreen].
 */
enum class ContactsPermissionState {
    UNKNOWN,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

/**
 * Immutable UI state for the Contacts screen.
 */
data class ContactsUiState(
    val permissionState: ContactsPermissionState = ContactsPermissionState.UNKNOWN,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val phoneContacts: List<Contact> = emptyList(),
    val emergencyContacts: List<EmergencyContact> = emptyList()
) {
    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    val totalContactsCount: Int
        get() = phoneContacts.size

    private val emergencyContactIds: Set<String>
        get() = emergencyContacts.map { it.contactId }.toSet()

    /**
     * Contacts matching [searchQuery] by first name, last name, full name or phone number
     * (BR04). When there is no active search only the first 3 contacts are shown (BR03).
     */
    val displayedContacts: List<Contact>
        get() {
            val normalizedQuery = searchQuery.trim()
            val matches = if (normalizedQuery.isBlank()) {
                phoneContacts
            } else {
                phoneContacts.filter { contact ->
                    contact.name.contains(normalizedQuery, ignoreCase = true) ||
                        contact.phoneNumber.contains(normalizedQuery, ignoreCase = true)
                }
            }
            return if (normalizedQuery.isBlank()) matches.take(3) else matches
        }

    fun isEmergencyContact(contactId: String): Boolean = contactId in emergencyContactIds
}
