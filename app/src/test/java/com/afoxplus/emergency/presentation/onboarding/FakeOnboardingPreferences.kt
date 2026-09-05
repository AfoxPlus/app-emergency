package com.afoxplus.emergency.presentation.onboarding

/**
 * In-memory fake used to unit test [OnboardingViewModel] without any Android dependency.
 */
class FakeOnboardingPreferences(initiallyCompleted: Boolean = false) : OnboardingPreferences {
    var completed: Boolean = initiallyCompleted
        private set

    override fun isOnboardingCompleted(): Boolean = completed

    override fun setOnboardingCompleted(completed: Boolean) {
        this.completed = completed
    }
}
