package com.afoxplus.emergency.presentation.login

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginRequiresFourDigits() {
        composeTestRule.setContent {
            AppemergencyTheme {
                LoginScreen(LoginUiState(), {}, {}, {})
            }
        }

        composeTestRule.onNodeWithTag("login_primary_action").assertIsNotEnabled()
        listOf(1, 2, 3, 4).forEach {
            composeTestRule.onNodeWithTag("login_digit_$it").performClick()
        }
        composeTestRule.onNodeWithTag("login_primary_action").assertIsEnabled()
    }
}
