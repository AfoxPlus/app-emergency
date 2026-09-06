package com.afoxplus.emergency.presentation.settings

import android.content.Context

/**
 * Persistence contract for the Settings screen configuration: the default SOS emergency
 * message edited from the "Emergency Message" bottom sheet (Settings AC03/AC07/AC09).
 *
 * Kept as an interface so [SettingsViewModel] can be unit tested without any Android
 * framework dependency, following the app's separation between UI state and
 * business/persistence logic (see `OnboardingPreferences`, `RegistrationPreferences`).
 */
interface SettingsPreferences {
    fun getSosMessage(): String
    fun saveSosMessage(message: String)
}

/**
 * [SettingsPreferences] implementation backed by [android.content.SharedPreferences],
 * consistent with the persistence approach used by other features in this app.
 */
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
