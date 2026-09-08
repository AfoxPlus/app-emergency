package com.afoxplus.emergency.data.repository

import android.content.Context
import com.afoxplus.emergency.domain.repository.PinPreferences

class PinPreferencesImpl(context: Context) : PinPreferences {
    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun hasPin(): Boolean = sharedPreferences.contains(KEY_PIN)

    override fun savePin(pin: String) {
        sharedPreferences.edit().putString(KEY_PIN, pin).apply()
    }

    override fun getPin(): String = sharedPreferences.getString(KEY_PIN, "").orEmpty()

    private companion object {
        const val PREFERENCES_NAME = "pin_preferences"
        const val KEY_PIN = "pin"
    }
}
