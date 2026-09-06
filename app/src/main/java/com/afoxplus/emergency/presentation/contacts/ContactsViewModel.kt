package com.afoxplus.emergency.presentation.contacts

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.EmergencyContact
import com.afoxplus.emergency.domain.repository.ContactsRepository
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Owns the Contacts screen UI state: contact permission handling, loading phone contacts,
 * searching and managing Emergency Contacts, persisting the result through
 * [EmergencyContactRepository].
 */
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val emergencyContactRepository: EmergencyContactRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ContactsUiState(emergencyContacts = emergencyContactRepository.getEmergencyContacts())
    )
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    /**
     * Called once the app knows whether the `READ_CONTACTS` permission is granted, and again
     * whenever the user answers the runtime permission request.
     */
    fun onPermissionResult(granted: Boolean, permanentlyDenied: Boolean = false) {
        if (granted) {
            loadContacts()
        } else {
            _uiState.update {
                it.copy(
                    permissionState = if (permanentlyDenied) {
                        ContactsPermissionState.PERMANENTLY_DENIED
                    } else {
                        ContactsPermissionState.DENIED
                    },
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Adds [contact] as an Emergency Contact (AC08), ignoring the request when it is already
     * registered (BR05).
     */
    fun onAddEmergencyContact(contact: Contact) {
        if (emergencyContactRepository.addEmergencyContact(contact)) {
            refreshEmergencyContacts()
        }
    }

    /**
     * Removes [contactId] from the Emergency Contacts (AC13). When the removed contact was the
     * Primary Contact, the next Emergency Contact is automatically promoted (AC14/BR10) since
     * the role is derived from list order.
     */
    fun onRemoveEmergencyContact(contactId: String) {
        emergencyContactRepository.removeEmergencyContact(contactId)
        refreshEmergencyContacts()
    }

    private fun loadContacts() {
        _uiState.update { it.copy(isLoading = true) }
        // Reads happen directly on the caller's thread, matching the synchronous style already
        // used by OnboardingPreferences/RegistrationPreferences in this codebase. For very large
        // contact lists this could be moved to a background dispatcher.
        val contacts = contactsRepository.getContacts()
        _uiState.update {
            it.copy(
                permissionState = ContactsPermissionState.GRANTED,
                isLoading = false,
                phoneContacts = contacts
            )
        }
    }

    private fun refreshEmergencyContacts() {
        _uiState.update { it.copy(emergencyContacts = emergencyContactRepository.getEmergencyContacts()) }
    }
}
