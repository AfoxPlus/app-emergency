package com.afoxplus.emergency.presentation.periodiccheck

import com.afoxplus.emergency.domain.model.PeriodicCheckConfiguration
import com.afoxplus.emergency.domain.model.FrequencyOption
import com.afoxplus.emergency.domain.model.ResponseTimeOption
import com.afoxplus.emergency.presentation.features.periodiccheck.PeriodicCheckViewModel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PeriodicCheckViewModelTest {

    private fun createViewModel(
        configuration: PeriodicCheckConfiguration = PeriodicCheckConfiguration(),
        contactsCount: Int = 0
    ): Quadruple<PeriodicCheckViewModel, FakePeriodicCheckPreferences, FakeEmergencyContactsCountProvider, FakePeriodicCheckManager> {
        val preferences = FakePeriodicCheckPreferences(configuration)
        val contactsProvider = FakeEmergencyContactsCountProvider(contactsCount)
        val periodicCheckManager = FakePeriodicCheckManager()
        return Quadruple(
            PeriodicCheckViewModel(preferences, periodicCheckManager, contactsProvider),
            preferences,
            contactsProvider,
            periodicCheckManager
        )
    }

    private data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )

    @Test
    fun `default configuration matches the product defaults`() {
        val (viewModel, _, _, _) = createViewModel()

        val state = viewModel.uiState.value

        assertFalse(state.enabled)
        assertEquals(30, state.frequencyMinutes)
        assertEquals(5, state.responseTimeoutMinutes)
        assertEquals(FrequencyOption.RECOMMENDED, state.selectedFrequencyOption)
        assertEquals(ResponseTimeOption.OPTIMAL, state.selectedResponseTimeOption)
    }

    @Test
    fun `selecting a predefined frequency updates the active configuration`() {
        val (viewModel, preferences, _, _) = createViewModel()

        viewModel.onFrequencyOptionSelected(FrequencyOption.SHORT_TRIPS)

        assertEquals(15, viewModel.uiState.value.frequencyMinutes)
        assertEquals(FrequencyOption.SHORT_TRIPS, viewModel.uiState.value.selectedFrequencyOption)
        assertEquals(15, preferences.storedConfiguration.frequencyMinutes)
    }

    @Test
    fun `custom frequency increment increases the value and clears predefined selection`() {
        val (viewModel, _, _, _) = createViewModel()

        viewModel.onCustomFrequencyIncrement()

        assertEquals(31, viewModel.uiState.value.frequencyMinutes)
        assertEquals(null, viewModel.uiState.value.selectedFrequencyOption)
    }

    @Test
    fun `custom frequency decrement decreases the value`() {
        val (viewModel, _, _, _) = createViewModel()

        viewModel.onCustomFrequencyDecrement()

        assertEquals(29, viewModel.uiState.value.frequencyMinutes)
    }

    @Test
    fun `custom frequency never goes below the minimum supported value`() {
        val (viewModel, _, _, _) = createViewModel(
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
        val (viewModel, _, _, _) = createViewModel(
            configuration = PeriodicCheckConfiguration(
                frequencyMinutes = PeriodicCheckConfiguration.MAX_FREQUENCY_MINUTES
            )
        )

        viewModel.onCustomFrequencyIncrement()

        assertEquals(PeriodicCheckConfiguration.MAX_FREQUENCY_MINUTES, viewModel.uiState.value.frequencyMinutes)
    }

    @Test
    fun `selecting a response time updates the active configuration`() {
        val (viewModel, preferences, _, _) = createViewModel()

        viewModel.onResponseTimeOptionSelected(ResponseTimeOption.CALM)

        assertEquals(15, viewModel.uiState.value.responseTimeoutMinutes)
        assertEquals(ResponseTimeOption.CALM, viewModel.uiState.value.selectedResponseTimeOption)
        assertEquals(15, preferences.storedConfiguration.responseTimeoutMinutes)
    }

    @Test
    fun `configuration changes are persisted through the preferences`() {
        val (viewModel, preferences, _, _) = createViewModel()

        viewModel.onFrequencyOptionSelected(FrequencyOption.LONG_TRIPS)
        viewModel.onResponseTimeOptionSelected(ResponseTimeOption.URGENT)

        assertEquals(60, preferences.storedConfiguration.frequencyMinutes)
        assertEquals(2, preferences.storedConfiguration.responseTimeoutMinutes)
        assertTrue(preferences.saveCallCount >= 2)
    }

    @Test
    fun `previously saved configuration is restored on load`() {
        val savedConfiguration = PeriodicCheckConfiguration(
            enabled = true,
            frequencyMinutes = 60,
            responseTimeoutMinutes = 10
        )
        val (viewModel, _, _, _) = createViewModel(configuration = savedConfiguration, contactsCount = 2)

        val state = viewModel.uiState.value

        assertTrue(state.enabled)
        assertEquals(60, state.frequencyMinutes)
        assertEquals(10, state.responseTimeoutMinutes)
    }

    @Test
    fun `activation is blocked when there are no emergency contacts`() {
        val (viewModel, preferences, _, periodicCheckManager) = createViewModel(contactsCount = 0)

        viewModel.onActivateClicked()

        assertFalse(viewModel.uiState.value.enabled)
        assertFalse(preferences.storedConfiguration.enabled)
        assertFalse(periodicCheckManager.enablePeriodicCheckCalled)
    }

    @Test
    fun `activation persists the configuration when emergency contacts are configured`() {
        val (viewModel, preferences, _, periodicCheckManager) = createViewModel(contactsCount = 3)

        viewModel.onActivateClicked()

        assertTrue(viewModel.uiState.value.enabled)
        assertTrue(preferences.storedConfiguration.enabled)
        assertTrue(periodicCheckManager.enablePeriodicCheckCalled)
    }

    @Test
    fun `deactivation clears the enabled flag and persists it`() {
        val (viewModel, preferences, _, periodicCheckManager) = createViewModel(
            configuration = PeriodicCheckConfiguration(enabled = true),
            contactsCount = 3
        )

        viewModel.onDeactivateClicked()

        assertFalse(viewModel.uiState.value.enabled)
        assertFalse(preferences.storedConfiguration.enabled)
        assertTrue(periodicCheckManager.disablePeriodicCheckCalled)
    }

    @Test
    fun `emergency contacts count reflects the persisted count and can be refreshed`() {
        val (viewModel, _, contactsProvider, _) = createViewModel(contactsCount = 0)

        assertFalse(viewModel.uiState.value.hasEmergencyContacts)

        contactsProvider.setContactsCount(3)
        viewModel.onEmergencyContactsRefreshRequested()

        assertEquals(3, viewModel.uiState.value.emergencyContactsCount)
        assertTrue(viewModel.uiState.value.hasEmergencyContacts)
    }
}
