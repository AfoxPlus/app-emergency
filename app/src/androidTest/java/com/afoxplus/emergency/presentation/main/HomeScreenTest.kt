package com.afoxplus.emergency.presentation.main

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenShowsEmergencyContentAndSettings() {
        composeTestRule.setContent {
            AppemergencyTheme {
                HomeScreen()
            }
        }

        composeTestRule.onNodeWithText("Hola, Valentin").assertExists()
        composeTestRule.onNodeWithText("SOS").assertExists()
        composeTestRule.onNodeWithText("Llamar a Emergencias").assertExists()
        composeTestRule.onNodeWithTag("home_quick_alert").assertExists()
        composeTestRule.onNodeWithTag("home_periodic_check").assertExists()
    }
}
