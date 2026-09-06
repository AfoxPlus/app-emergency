package com.afoxplus.emergency.domain.repository

/**
 * Persistence contract for onboarding state.
 */
interface OnboardingPreferences {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
}
