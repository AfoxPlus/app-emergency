package com.afoxplus.emergency.data.repository

import android.content.Context
import com.afoxplus.emergency.domain.repository.OnboardingPreferences

class OnboardingPreferencesImpl(context: Context) : OnboardingPreferences {
    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun isOnboardingCompleted(): Boolean =
        sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    override fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "onboarding_preferences"
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
