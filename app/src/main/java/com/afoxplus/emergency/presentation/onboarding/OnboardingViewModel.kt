package com.afoxplus.emergency.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Owns the onboarding UI state: current page, navigation between pages and
 * skip/finish completion, persisting the result through [OnboardingPreferences].
 */
class OnboardingViewModel(
    private val preferences: OnboardingPreferences,
    pages: List<OnboardingPage> = onboardingPages
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OnboardingUiState(pages = pages, isCompleted = preferences.isOnboardingCompleted())
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /**
     * Called when the user swipes/navigates to a different page directly.
     */
    fun onPageChanged(page: Int) {
        _uiState.update { state ->
            if (page in state.pages.indices) state.copy(currentPage = page) else state
        }
    }

    /**
     * Moves to the next page, or completes onboarding when already on the last page.
     */
    fun onNextClicked() {
        val state = _uiState.value
        if (state.isLastPage) {
            completeOnboarding()
        } else {
            _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        }
    }

    /**
     * Skipping onboarding is also considered as completing it.
     */
    fun onSkipClicked() {
        completeOnboarding()
    }

    private fun completeOnboarding() {
        preferences.setOnboardingCompleted(true)
        _uiState.update { it.copy(isCompleted = true) }
    }
}

/**
 * Simple [ViewModelProvider.Factory] since the project does not use a DI framework yet.
 */
class OnboardingViewModelFactory(
    private val preferences: OnboardingPreferences,
    private val pages: List<OnboardingPage> = onboardingPages
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        OnboardingViewModel(preferences, pages) as T
}
