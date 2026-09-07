package com.afoxplus.emergency.presentation.alert

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
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
        composeTestRule.onNodeWithText("Ubicación obtenida").assertExists()
        composeTestRule.onNodeWithText("Contactos notificados").assertExists()
        composeTestRule.onNodeWithTag("alert_cancel_button").assertExists()
        composeTestRule.onNodeWithTag("alert_back_home_button").assertExists()
    }
}
