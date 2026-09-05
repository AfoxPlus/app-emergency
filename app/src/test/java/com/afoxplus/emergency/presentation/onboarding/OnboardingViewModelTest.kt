package com.afoxplus.emergency.presentation.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingViewModelTest {

    @Test
    fun `initial state starts on the first page and is not completed when preferences are fresh`() {
        val viewModel = OnboardingViewModel(FakeOnboardingPreferences())

        val state = viewModel.uiState.value

        assertEquals(0, state.currentPage)
        assertFalse(state.isCompleted)
        assertEquals(onboardingPages, state.pages)
    }

    @Test
    fun `initial state is already completed when preferences report onboarding as done`() {
        val viewModel = OnboardingViewModel(FakeOnboardingPreferences(initiallyCompleted = true))

        assertTrue(viewModel.uiState.value.isCompleted)
    }

    @Test
    fun `onNextClicked advances to the next page while not on the last page`() {
        val viewModel = OnboardingViewModel(FakeOnboardingPreferences())

        viewModel.onNextClicked()

        assertEquals(1, viewModel.uiState.value.currentPage)
        assertFalse(viewModel.uiState.value.isCompleted)
    }

    @Test
    fun `onNextClicked on the last page completes and persists onboarding`() {
        val preferences = FakeOnboardingPreferences()
        val viewModel = OnboardingViewModel(preferences)
        val lastPageIndex = onboardingPages.lastIndex

        repeat(lastPageIndex) { viewModel.onNextClicked() }
        assertEquals(lastPageIndex, viewModel.uiState.value.currentPage)
        assertTrue(viewModel.uiState.value.isLastPage)

        viewModel.onNextClicked()

        assertTrue(viewModel.uiState.value.isCompleted)
        assertTrue(preferences.isOnboardingCompleted())
    }

    @Test
    fun `onSkipClicked completes and persists onboarding regardless of current page`() {
        val preferences = FakeOnboardingPreferences()
        val viewModel = OnboardingViewModel(preferences)

        viewModel.onSkipClicked()

        assertTrue(viewModel.uiState.value.isCompleted)
        assertTrue(preferences.isOnboardingCompleted())
    }

    @Test
    fun `onPageChanged updates the current page for valid indices`() {
        val viewModel = OnboardingViewModel(FakeOnboardingPreferences())

        viewModel.onPageChanged(onboardingPages.lastIndex)

        assertEquals(onboardingPages.lastIndex, viewModel.uiState.value.currentPage)
    }

    @Test
    fun `onPageChanged ignores out-of-range indices`() {
        val viewModel = OnboardingViewModel(FakeOnboardingPreferences())

        viewModel.onPageChanged(-1)
        viewModel.onPageChanged(onboardingPages.size)

        assertEquals(0, viewModel.uiState.value.currentPage)
    }
}
