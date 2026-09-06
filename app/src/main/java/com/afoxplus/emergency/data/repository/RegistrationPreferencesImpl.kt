package com.afoxplus.emergency.data.repository

import android.content.Context
import com.afoxplus.emergency.domain.repository.RegistrationPreferences

class RegistrationPreferencesImpl(context: Context) : RegistrationPreferences {
    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun isRegistrationCompleted(): Boolean =
        sharedPreferences.getBoolean(KEY_REGISTRATION_COMPLETED, false)

    override fun setRegistrationCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REGISTRATION_COMPLETED, completed).apply()
    }

    override fun saveProfile(name: String, phoneNumber: String) {
        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putString(KEY_PHONE_NUMBER, phoneNumber)
            .apply()
    }

    override fun getName(): String = sharedPreferences.getString(KEY_NAME, "").orEmpty()

    override fun getPhoneNumber(): String =
        sharedPreferences.getString(KEY_PHONE_NUMBER, "").orEmpty()

    private companion object {
        const val PREFERENCES_NAME = "registration_preferences"
        const val KEY_REGISTRATION_COMPLETED = "registration_completed"
        const val KEY_NAME = "name"
        const val KEY_PHONE_NUMBER = "phone_number"
    }
}
