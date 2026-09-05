package com.afoxplus.emergency.presentation.register

import android.content.Context

interface RegistrationPreferences {
    fun isRegistrationCompleted(): Boolean
    fun setRegistrationCompleted(completed: Boolean)
}

class RegistrationPreferencesImpl(context: Context) : RegistrationPreferences {
    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun isRegistrationCompleted(): Boolean =
        sharedPreferences.getBoolean(KEY_REGISTRATION_COMPLETED, false)

    override fun setRegistrationCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REGISTRATION_COMPLETED, completed).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "registration_preferences"
        const val KEY_REGISTRATION_COMPLETED = "registration_completed"
    }
}
