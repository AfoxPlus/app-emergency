package com.afoxplus.emergency.presentation.main

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.afoxplus.emergency.presentation.features.home.HomeScreenContent
import com.afoxplus.emergency.presentation.features.home.HomeUiState
import com.afoxplus.emergency.presentation.ui.theme.AppemergencyTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenShowsEmergencyContentAndSettings() {
        composeTestRule.setContent {
            AppemergencyTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        userName = "Valentin",
                        isQuickAlertEnabled = true,
                        isPeriodicCheckEnabled = false
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Hola, Valentin").assertExists()
        composeTestRule.onNodeWithText("SOS").assertExists()
        composeTestRule.onNodeWithText("Llamar a Emergencias").assertExists()
        composeTestRule.onNodeWithTag("home_quick_alert").assertExists()
        composeTestRule.onNodeWithTag("home_periodic_check").assertExists()
        composeTestRule.onNodeWithTag("home_status_chip").assertExists()
    }

    @Test
    fun alertButtonInvokesCallback() {
        var clicked = false
        composeTestRule.setContent {
            AppemergencyTheme {
                HomeScreenContent(
                    uiState = HomeUiState(userName = "Valentin"),
                    onAlertClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("home_alert_button").performClick()
        assert(clicked)
    }

    @Test
    fun statusChipOpensStatusDialogWhenClicked() {
        composeTestRule.setContent {
            AppemergencyTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        userName = "Valentin",
                        hasContactsPermission = false
                    )
                )
            }
        }

        composeTestRule.onNodeWithTag("home_status_chip").performClick()
        composeTestRule.onNodeWithText("Estado de Seguridad").assertExists()
        composeTestRule.onNodeWithText("• Permiso de lectura de contactos no concedido").assertExists()
    }

    @Test
    fun bottomNavigationInvokesSelectedDestination() {
        var contactsSelected = false
        var settingsSelected = false
        composeTestRule.setContent {
            AppemergencyTheme {
                HomeScreenContent(
                    uiState = HomeUiState(userName = "Valentin"),
                    onNavigateToContacts = { contactsSelected = true },
                    onNavigateToSettings = { settingsSelected = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("nav_contacts").performClick()
        composeTestRule.onNodeWithTag("nav_settings").performClick()

        assert(contactsSelected)
        assert(settingsSelected)
    }
}
