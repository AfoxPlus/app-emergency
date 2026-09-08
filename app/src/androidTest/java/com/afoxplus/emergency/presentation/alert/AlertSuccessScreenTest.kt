package com.afoxplus.emergency.presentation.alert

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertDoesNotExist
import com.afoxplus.emergency.presentation.features.alert.AlertSuccessScreen
import com.afoxplus.emergency.presentation.ui.theme.AppemergencyTheme
import org.junit.Rule
import org.junit.Test

class AlertSuccessScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun alertSuccessScreenShowsStatusAndActions() {
        composeTestRule.setContent {
            AppemergencyTheme { AlertSuccessScreen() }
        }

        composeTestRule.onNodeWithText("Alerta de emergencia").assertExists()
        composeTestRule.onNodeWithText("Contactos notificados").assertExists()
        composeTestRule.onNodeWithTag("alert_cancel_button").assertExists()
        composeTestRule.onNodeWithTag("alert_back_home_button").assertExists()
    }

    @Test
    fun mapSectionIsHiddenWhenNoCoordinatesWereCaptured() {
        composeTestRule.setContent {
            AppemergencyTheme { AlertSuccessScreen(latitude = null, longitude = null) }
        }

        composeTestRule.onNodeWithText("Ubicación obtenida").assertDoesNotExist()
        composeTestRule.onNodeWithTag("alert_location_coordinates").assertDoesNotExist()
        composeTestRule.onNodeWithText("Contactos notificados").assertExists()
    }

    @Test
    fun mapSectionIsShownWhenCoordinatesWereCaptured() {
        composeTestRule.setContent {
            AppemergencyTheme {
                AlertSuccessScreen(latitude = -12.0464, longitude = -77.0428)
            }
        }

        composeTestRule.onNodeWithText("Ubicación obtenida").assertExists()
        composeTestRule.onNodeWithTag("alert_location_coordinates").assertExists()
    }
}
