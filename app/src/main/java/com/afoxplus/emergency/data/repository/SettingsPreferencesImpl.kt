package com.afoxplus.emergency.data.repository

import android.content.Context
import com.afoxplus.emergency.domain.repository.SettingsPreferences

class SettingsPreferencesImpl(context: Context) : SettingsPreferences {

    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getSosMessage(): String =
        sharedPreferences.getString(KEY_SOS_MESSAGE, null) ?: DEFAULT_SOS_MESSAGE

    override fun saveSosMessage(message: String) {
        sharedPreferences.edit().putString(KEY_SOS_MESSAGE, message).apply()
    }

    companion object {
        const val DEFAULT_SOS_MESSAGE = "¡Emergencia! Necesito ayuda inmediata. Mi última " +
            "ubicación conocida se adjunta automáticamente."
        private const val PREFERENCES_NAME = "settings_preferences"
        private const val KEY_SOS_MESSAGE = "sos_message"
    }
}
