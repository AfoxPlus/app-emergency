package com.afoxplus.emergency.presentation.contacts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactsViewModelTest {

    private val elena = Contact("1", "Elena Martínez", "+34 600 111 222")
    private val javier = Contact("2", "Javier Ruiz", "+34 655 444 333")
    private val sofia = Contact("3", "Sofía García", "+34 677 888 999")
    private val carmen = Contact("4", "Carmen Rodríguez", "+34 612 345 678")

    private fun newViewModel(
        contacts: List<Contact> = listOf(elena, javier, sofia, carmen),
        emergencyContactRepository: EmergencyContactRepository = FakeEmergencyContactRepository()
    ) = ContactsViewModel(
        contactsRepository = FakeContactsRepository(contacts),
        emergencyContactRepository = emergencyContactRepository
    )

    @Test
    fun deniedPermissionDoesNotLoadContacts() {
        val viewModel = newViewModel()

        viewModel.onPermissionResult(granted = false)

        assertEquals(ContactsPermissionState.DENIED, viewModel.uiState.value.permissionState)
        assertTrue(viewModel.uiState.value.phoneContacts.isEmpty())
    }

    @Test
    fun permanentlyDeniedPermissionIsReported() {
        val viewModel = newViewModel()

        viewModel.onPermissionResult(granted = false, permanentlyDenied = true)

        assertEquals(ContactsPermissionState.PERMANENTLY_DENIED, viewModel.uiState.value.permissionState)
    }

    @Test
    fun grantedPermissionLoadsContacts() {
        val viewModel = newViewModel()

        viewModel.onPermissionResult(granted = true)

        assertEquals(ContactsPermissionState.GRANTED, viewModel.uiState.value.permissionState)
        assertEquals(4, viewModel.uiState.value.phoneContacts.size)
    }

    @Test
    fun withoutSearchOnlyFirstThreeContactsAreDisplayed() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)

        val state = viewModel.uiState.value
        assertEquals(4, state.totalContactsCount)
        assertEquals(3, state.displayedContacts.size)
        assertEquals(listOf(elena, javier, sofia), state.displayedContacts)
    }

    @Test
    fun searchByNameMatchesAcrossAllContacts() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)

        viewModel.onSearchQueryChanged("Carmen")

        assertEquals(listOf(carmen), viewModel.uiState.value.displayedContacts)
    }

    @Test
    fun searchByPhoneNumberMatchesAcrossAllContacts() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)

        viewModel.onSearchQueryChanged("612 345")

        assertEquals(listOf(carmen), viewModel.uiState.value.displayedContacts)
    }

    @Test
    fun searchWithNoMatchesReturnsEmptyList() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)

        viewModel.onSearchQueryChanged("Unknown")

        assertTrue(viewModel.uiState.value.displayedContacts.isEmpty())
        assertTrue(viewModel.uiState.value.isSearching)
    }

    @Test
    fun addingEmergencyContactUpdatesStateAndMarksFirstAsPrimary() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)

        viewModel.onAddEmergencyContact(carmen)

        val emergencyContacts = viewModel.uiState.value.emergencyContacts
        assertEquals(1, emergencyContacts.size)
        assertEquals(EmergencyContactType.PRIMARY, emergencyContacts[0].type)
        assertTrue(viewModel.uiState.value.isEmergencyContact(carmen.id))
    }

    @Test
    fun secondAddedEmergencyContactIsBackup() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)

        viewModel.onAddEmergencyContact(carmen)
        viewModel.onAddEmergencyContact(elena)

        val emergencyContacts = viewModel.uiState.value.emergencyContacts
        assertEquals(EmergencyContactType.PRIMARY, emergencyContacts[0].type)
        assertEquals(EmergencyContactType.BACKUP, emergencyContacts[1].type)
    }

    @Test
    fun duplicateEmergencyContactIsIgnored() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)

        viewModel.onAddEmergencyContact(carmen)
        viewModel.onAddEmergencyContact(carmen)

        assertEquals(1, viewModel.uiState.value.emergencyContacts.size)
    }

    @Test
    fun removingPrimaryContactPromotesNextContact() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)
        viewModel.onAddEmergencyContact(carmen)
        viewModel.onAddEmergencyContact(elena)
        viewModel.onAddEmergencyContact(javier)

        viewModel.onRemoveEmergencyContact(carmen.id)

        val emergencyContacts = viewModel.uiState.value.emergencyContacts
        assertEquals(2, emergencyContacts.size)
        assertEquals(elena.id, emergencyContacts[0].contactId)
        assertEquals(EmergencyContactType.PRIMARY, emergencyContacts[0].type)
        assertEquals(javier.id, emergencyContacts[1].contactId)
        assertEquals(EmergencyContactType.BACKUP, emergencyContacts[1].type)
    }

    @Test
    fun removedEmergencyContactBecomesAvailableAgain() {
        val viewModel = newViewModel()
        viewModel.onPermissionResult(granted = true)
        viewModel.onAddEmergencyContact(carmen)

        viewModel.onRemoveEmergencyContact(carmen.id)

        assertTrue(viewModel.uiState.value.emergencyContacts.isEmpty())
        assertFalse(viewModel.uiState.value.isEmergencyContact(carmen.id))
    }

    @Test
    fun emergencyContactsArePersistedAndRestoredAfterRestart() {
        val sharedRepository = FakeEmergencyContactRepository()
        val viewModel = newViewModel(emergencyContactRepository = sharedRepository)
        viewModel.onPermissionResult(granted = true)
        viewModel.onAddEmergencyContact(carmen)

        // Simulate an application restart: a new ViewModel is created against the same
        // persisted repository.
        val restartedViewModel = newViewModel(emergencyContactRepository = sharedRepository)

        val emergencyContacts = restartedViewModel.uiState.value.emergencyContacts
        assertEquals(1, emergencyContacts.size)
        assertEquals(carmen.id, emergencyContacts[0].contactId)
        assertEquals(EmergencyContactType.PRIMARY, emergencyContacts[0].type)
    }
}
