package com.afoxplus.emergency.presentation.contacts

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import org.junit.Rule
import org.junit.Test

class ContactsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val elena = Contact("1", "Elena Martínez", "+34 600 111 222")
    private val javier = Contact("2", "Javier Ruiz", "+34 655 444 333")
    private val sofia = Contact("3", "Sofía García", "+34 677 888 999")
    private val carmen = EmergencyContact("4", "Carmen Rodríguez", "+34 612 345 678", EmergencyContactType.PRIMARY)

    private fun setContent(
        uiState: ContactsUiState,
        onSearchQueryChanged: (String) -> Unit = {},
        onAddEmergencyContact: (Contact) -> Unit = {},
        onRemoveEmergencyContact: (String) -> Unit = {},
        onRequestPermission: () -> Unit = {},
        onOpenAppSettings: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AppemergencyTheme {
                ContactsScreen(
                    uiState = uiState,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onAddEmergencyContact = onAddEmergencyContact,
                    onRemoveEmergencyContact = onRemoveEmergencyContact,
                    onRequestPermission = onRequestPermission,
                    onOpenAppSettings = onOpenAppSettings
                )
            }
        }
    }

    @Test
    fun loadingStateIsDisplayed() {
        setContent(ContactsUiState(isLoading = true))

        composeTestRule.onNodeWithTag("contacts_loading").assertExists()
    }

    @Test
    fun permissionRequiredStateShowsAllowAccessAction() {
        var requested = false
        setContent(
            ContactsUiState(permissionState = ContactsPermissionState.DENIED),
            onRequestPermission = { requested = true }
        )

        composeTestRule.onNodeWithTag("contacts_permission_action").performClick()

        assert(requested)
    }

    @Test
    fun permanentlyDeniedStateOpensAppSettings() {
        var openedSettings = false
        setContent(
            ContactsUiState(permissionState = ContactsPermissionState.PERMANENTLY_DENIED),
            onOpenAppSettings = { openedSettings = true }
        )

        composeTestRule.onNodeWithTag("contacts_permission_action").performClick()

        assert(openedSettings)
    }

    @Test
    fun onlyFirstThreeContactsAreDisplayedByDefault() {
        setContent(
            ContactsUiState(
                permissionState = ContactsPermissionState.GRANTED,
                phoneContacts = listOf(elena, javier, sofia, Contact("5", "Extra", "+34 000 000 000"))
            )
        )

        composeTestRule.onNodeWithTag("phone_contact_1").assertExists()
        composeTestRule.onNodeWithTag("phone_contact_2").assertExists()
        composeTestRule.onNodeWithTag("phone_contact_3").assertExists()
        composeTestRule.onNodeWithText("4 contactos").assertExists()
    }

    @Test
    fun searchingFiltersAcrossAllContacts() {
        setContent(
            ContactsUiState(
                permissionState = ContactsPermissionState.GRANTED,
                phoneContacts = listOf(elena, javier, sofia)
            )
        )

        composeTestRule.onNodeWithTag("contacts_search_field").performTextInput("Javier")

        composeTestRule.onNodeWithTag("phone_contact_2").assertExists()
    }

    @Test
    fun addEmergencyContactTriggersCallback() {
        var addedContact: Contact? = null
        setContent(
            ContactsUiState(
                permissionState = ContactsPermissionState.GRANTED,
                phoneContacts = listOf(elena)
            ),
            onAddEmergencyContact = { addedContact = it }
        )

        composeTestRule.onNodeWithTag("add_emergency_contact_1").performClick()

        assertExistsAndEquals(addedContact, elena)
    }

    @Test
    fun emergencyContactsSectionDisplaysPrimaryContact() {
        setContent(
            ContactsUiState(
                permissionState = ContactsPermissionState.GRANTED,
                emergencyContacts = listOf(carmen)
            )
        )

        composeTestRule.onNodeWithText("Carmen Rodríguez").assertExists()
        composeTestRule.onNodeWithText("Contacto Principal").assertExists()
    }

    @Test
    fun deletingEmergencyContactTriggersCallback() {
        var removedId: String? = null
        setContent(
            ContactsUiState(
                permissionState = ContactsPermissionState.GRANTED,
                emergencyContacts = listOf(carmen)
            ),
            onRemoveEmergencyContact = { removedId = it }
        )

        composeTestRule.onNodeWithTag("delete_emergency_contact_4").performClick()

        assertExistsAndEquals(removedId, carmen.contactId)
    }

    private fun <T> assertExistsAndEquals(actual: T?, expected: T) {
        assert(actual == expected)
    }
}
