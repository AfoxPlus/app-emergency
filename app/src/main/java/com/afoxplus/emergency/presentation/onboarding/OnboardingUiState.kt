package com.afoxplus.emergency.presentation.onboarding

import com.afoxplus.emergency.domain.model.OnboardingPage

/**
 * Immutable UI state for the onboarding screen.
 */
data class OnboardingUiState(
    val pages: List<OnboardingPage> = onboardingPages,
    val currentPage: Int = 0,
    val isCompleted: Boolean = false
) {
    val isLastPage: Boolean
        get() = currentPage == pages.lastIndex
}
