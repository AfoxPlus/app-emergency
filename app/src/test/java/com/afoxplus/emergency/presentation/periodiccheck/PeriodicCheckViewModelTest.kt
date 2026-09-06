package com.afoxplus.emergency.presentation.periodiccheck

import com.afoxplus.emergency.domain.model.PeriodicCheckConfiguration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PeriodicCheckViewModelTest {

    private fun createViewModel(
        configuration: PeriodicCheckConfiguration = PeriodicCheckConfiguration(),
        contactsCount: Int = 0
    ): Triple<PeriodicCheckViewModel, FakePeriodicCheckPreferences, FakeEmergencyContactsCountProvider> {
        val preferences = FakePeriodicCheckPreferences(configuration)
        val contactsProvider = FakeEmergencyContactsCountProvider(contactsCount)
        return Triple(PeriodicCheckViewModel(preferences, contactsProvider), preferences, contactsProvider)
    }

    @Test
    fun `default configuration matches the product defaults`() {
        val (viewModel, _, _) = createViewModel()

        val state = viewModel.uiState.value

        assertFalse(state.enabled)
        assertEquals(30, state.frequencyMinutes)
        assertEquals(5, state.responseTimeoutMinutes)
        assertEquals(FrequencyOption.RECOMMENDED, state.selectedFrequencyOption)
        assertEquals(ResponseTimeOption.OPTIMAL, state.selectedResponseTimeOption)
    }

    @Test
    fun `selecting a predefined frequency updates the active configuration`() {
        val (viewModel, preferences, _) = createViewModel()

        viewModel.onFrequencyOptionSelected(FrequencyOption.SHORT_TRIPS)

        assertEquals(15, viewModel.uiState.value.frequencyMinutes)
        assertEquals(FrequencyOption.SHORT_TRIPS, viewModel.uiState.value.selectedFrequencyOption)
        assertEquals(15, preferences.configuration.frequencyMinutes)
    }

    @Test
    fun `custom frequency increment increases the value and clears predefined selection`() {
        val (viewModel, _, _) = createViewModel()

        viewModel.onCustomFrequencyIncrement()

        assertEquals(31, viewModel.uiState.value.frequencyMinutes)
        assertEquals(null, viewModel.uiState.value.selectedFrequencyOption)
    }

    @Test
    fun `custom frequency decrement decreases the value`() {
        val (viewModel, _, _) = createViewModel()

        viewModel.onCustomFrequencyDecrement()

        assertEquals(29, viewModel.uiState.value.frequencyMinutes)
    }

    @Test
    fun `custom frequency never goes below the minimum supported value`() {
        val (viewModel, _, _) = createViewModel(
            configuration = PeriodicCheckConfiguration(
                frequencyMinutes = PeriodicCheckConfiguration.MIN_FREQUENCY_MINUTES
            )
        )

        viewModel.onCustomFrequencyDecrement()

        assertEquals(PeriodicCheckConfiguration.MIN_FREQUENCY_MINUTES, viewModel.uiState.value.frequencyMinutes)
        assertTrue(viewModel.uiState.value.frequencyMinutes > 0)
    }

    @Test
    fun `custom frequency never goes above the maximum supported value`() {
        val (viewModel, _, _) = createViewModel(
            configuration = PeriodicCheckConfiguration(
                frequencyMinutes = PeriodicCheckConfiguration.MAX_FREQUENCY_MINUTES
            )
        )

        viewModel.onCustomFrequencyIncrement()

        assertEquals(PeriodicCheckConfiguration.MAX_FREQUENCY_MINUTES, viewModel.uiState.value.frequencyMinutes)
    }

    @Test
    fun `selecting a response time updates the active configuration`() {
        val (viewModel, preferences, _) = createViewModel()

        viewModel.onResponseTimeOptionSelected(ResponseTimeOption.CALM)

        assertEquals(15, viewModel.uiState.value.responseTimeoutMinutes)
        assertEquals(ResponseTimeOption.CALM, viewModel.uiState.value.selectedResponseTimeOption)
        assertEquals(15, preferences.configuration.responseTimeoutMinutes)
    }

    @Test
    fun `configuration changes are persisted through the preferences`() {
        val (viewModel, preferences, _) = createViewModel()

        viewModel.onFrequencyOptionSelected(FrequencyOption.LONG_TRIPS)
        viewModel.onResponseTimeOptionSelected(ResponseTimeOption.URGENT)

        assertEquals(60, preferences.configuration.frequencyMinutes)
        assertEquals(2, preferences.configuration.responseTimeoutMinutes)
        assertTrue(preferences.saveCallCount >= 2)
    }

    @Test
    fun `previously saved configuration is restored on load`() {
        val savedConfiguration = PeriodicCheckConfiguration(
            enabled = true,
            frequencyMinutes = 60,
            responseTimeoutMinutes = 10
        )
        val (viewModel, _, _) = createViewModel(configuration = savedConfiguration, contactsCount = 2)

        val state = viewModel.uiState.value

        assertTrue(state.enabled)
        assertEquals(60, state.frequencyMinutes)
        assertEquals(10, state.responseTimeoutMinutes)
    }

    @Test
    fun `activation is blocked when there are no emergency contacts`() {
        val (viewModel, preferences, _) = createViewModel(contactsCount = 0)

        viewModel.onActivateClicked()

        assertFalse(viewModel.uiState.value.enabled)
        assertFalse(preferences.configuration.enabled)
    }

    @Test
    fun `activation persists the configuration when emergency contacts are configured`() {
        val (viewModel, preferences, _) = createViewModel(contactsCount = 3)

        viewModel.onActivateClicked()

        assertTrue(viewModel.uiState.value.enabled)
        assertTrue(preferences.configuration.enabled)
    }

    @Test
    fun `deactivation clears the enabled flag and persists it`() {
        val (viewModel, preferences, _) = createViewModel(
            configuration = PeriodicCheckConfiguration(enabled = true),
            contactsCount = 3
        )

        viewModel.onDeactivateClicked()

        assertFalse(viewModel.uiState.value.enabled)
        assertFalse(preferences.configuration.enabled)
    }

    @Test
    fun `emergency contacts count reflects the persisted count and can be refreshed`() {
        val (viewModel, _, contactsProvider) = createViewModel(contactsCount = 0)

        assertFalse(viewModel.uiState.value.hasEmergencyContacts)

        contactsProvider.setContactsCount(3)
        viewModel.onEmergencyContactsRefreshRequested()

        assertEquals(3, viewModel.uiState.value.emergencyContactsCount)
        assertTrue(viewModel.uiState.value.hasEmergencyContacts)
    }
}
