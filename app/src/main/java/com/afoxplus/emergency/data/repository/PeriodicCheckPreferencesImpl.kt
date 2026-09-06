package com.afoxplus.emergency.data.repository

import android.content.Context
import com.afoxplus.emergency.domain.model.PeriodicCheckConfiguration
import com.afoxplus.emergency.domain.repository.PeriodicCheckPreferences

class PeriodicCheckPreferencesImpl(context: Context) : PeriodicCheckPreferences {

    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getConfiguration(): PeriodicCheckConfiguration = PeriodicCheckConfiguration(
        enabled = sharedPreferences.getBoolean(KEY_ENABLED, false),
        frequencyMinutes = sharedPreferences.getInt(
            KEY_FREQUENCY_MINUTES,
            PeriodicCheckConfiguration.DEFAULT_FREQUENCY_MINUTES
        ),
        responseTimeoutMinutes = sharedPreferences.getInt(
            KEY_RESPONSE_TIMEOUT_MINUTES,
            PeriodicCheckConfiguration.DEFAULT_RESPONSE_TIMEOUT_MINUTES
        ),
        lastConfigurationUpdate = sharedPreferences.getLong(KEY_LAST_UPDATE, 0L)
    )

    override fun saveConfiguration(configuration: PeriodicCheckConfiguration) {
        sharedPreferences.edit()
            .putBoolean(KEY_ENABLED, configuration.enabled)
            .putInt(KEY_FREQUENCY_MINUTES, configuration.frequencyMinutes)
            .putInt(KEY_RESPONSE_TIMEOUT_MINUTES, configuration.responseTimeoutMinutes)
            .putLong(KEY_LAST_UPDATE, configuration.lastConfigurationUpdate)
            .apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "periodic_check_preferences"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_FREQUENCY_MINUTES = "frequency_minutes"
        private const val KEY_RESPONSE_TIMEOUT_MINUTES = "response_timeout_minutes"
        private const val KEY_LAST_UPDATE = "last_configuration_update"
    }
}
