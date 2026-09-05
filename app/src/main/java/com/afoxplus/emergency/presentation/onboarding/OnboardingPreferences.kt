package com.afoxplus.emergency.presentation.onboarding

import android.content.Context

/**
 * Persistence contract for the onboarding completion flag.
 *
 * Kept as an interface so the [OnboardingViewModel] can be unit tested without any
 * Android framework dependency, following the app's separation between UI state and
 * business/persistence logic.
 */
interface OnboardingPreferences {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
}

/**
 * [OnboardingPreferences] implementation backed by [android.content.SharedPreferences],
 * the persistence mechanism already available in the Android SDK.
 */
class OnboardingPreferencesImpl(context: Context) : OnboardingPreferences {

    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun isOnboardingCompleted(): Boolean =
        sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    override fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "onboarding_preferences"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
