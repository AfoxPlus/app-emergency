package com.afoxplus.emergency.presentation.periodiccheck

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import org.junit.Rule
import org.junit.Test

class PeriodicCheckScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        uiState: PeriodicCheckUiState = PeriodicCheckUiState(),
        onBackClick: () -> Unit = {},
        onFrequencyOptionSelected: (FrequencyOption) -> Unit = {},
        onCustomFrequencyIncrement: () -> Unit = {},
        onCustomFrequencyDecrement: () -> Unit = {},
        onResponseTimeOptionSelected: (ResponseTimeOption) -> Unit = {},
        onEmergencyContactsClick: () -> Unit = {},
        onActivateClicked: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AppemergencyTheme {
                PeriodicCheckScreen(
                    uiState = uiState,
                    onBackClick = onBackClick,
                    onFrequencyOptionSelected = onFrequencyOptionSelected,
                    onCustomFrequencyIncrement = onCustomFrequencyIncrement,
                    onCustomFrequencyDecrement = onCustomFrequencyDecrement,
                    onResponseTimeOptionSelected = onResponseTimeOptionSelected,
                    onEmergencyContactsClick = onEmergencyContactsClick,
                    onActivateClicked = onActivateClicked
                )
            }
        }
    }

    @Test
    fun defaultStateShowsThirtyMinutesAndFiveMinutesSelected() {
        setContent()

        composeTestRule.onNodeWithTag("periodic_check_frequency_option_30").assertExists()
        composeTestRule.onNodeWithTag("periodic_check_response_option_5").assertExists()
        composeTestRule.onNodeWithText("30 min").assertExists()
    }

    @Test
    fun backButtonTriggersCallback() {
        var clicked = false
        setContent(onBackClick = { clicked = true })

        composeTestRule.onNodeWithTag("periodic_check_back").performClick()

        assert(clicked)
    }

    @Test
    fun selectingAPredefinedFrequencyTriggersCallback() {
        var selected: FrequencyOption? = null
        setContent(onFrequencyOptionSelected = { selected = it })

        composeTestRule.onNodeWithTag("periodic_check_frequency_option_15").performClick()

        assert(selected == FrequencyOption.SHORT_TRIPS)
    }

    @Test
    fun customFrequencyIncrementAndDecrementTriggerCallbacks() {
        var incremented = false
        var decremented = false
        setContent(
            onCustomFrequencyIncrement = { incremented = true },
            onCustomFrequencyDecrement = { decremented = true }
        )

        composeTestRule.onNodeWithTag("periodic_check_custom_increment").performClick()
        composeTestRule.onNodeWithTag("periodic_check_custom_decrement").performClick()

        assert(incremented)
        assert(decremented)
    }

    @Test
    fun selectingAResponseTimeTriggersCallback() {
        var selected: ResponseTimeOption? = null
        setContent(onResponseTimeOptionSelected = { selected = it })

        composeTestRule.onNodeWithTag("periodic_check_response_option_10").performClick()

        assert(selected == ResponseTimeOption.RELAXED)
    }

    @Test
    fun activateButtonIsDisabledWithoutEmergencyContacts() {
        setContent(uiState = PeriodicCheckUiState(emergencyContactsCount = 0))

        composeTestRule.onNodeWithTag("periodic_check_activate_button").assertIsNotEnabled()
    }

    @Test
    fun activateButtonIsEnabledWithEmergencyContactsAndTriggersCallback() {
        var activated = false
        setContent(
            uiState = PeriodicCheckUiState(emergencyContactsCount = 3),
            onActivateClicked = { activated = true }
        )

        composeTestRule.onNodeWithTag("periodic_check_activate_button").assertIsEnabled()
        composeTestRule.onNodeWithTag("periodic_check_activate_button").performClick()

        assert(activated)
    }

    @Test
    fun emptyEmergencyContactsStateShowsAddContactAction() {
        setContent(uiState = PeriodicCheckUiState(emergencyContactsCount = 0))

        composeTestRule.onNodeWithTag("periodic_check_add_contact_button").assertExists()
    }

    @Test
    fun emergencyContactsCardTapTriggersCallback() {
        var clicked = false
        setContent(
            uiState = PeriodicCheckUiState(emergencyContactsCount = 3),
            onEmergencyContactsClick = { clicked = true }
        )

        composeTestRule.onNodeWithTag("periodic_check_contacts_card").performClick()

        assert(clicked)
    }
}
