package com.afoxplus.emergency.domain.repository

/**
 * Persistence contract for the Settings screen configuration.
 */
interface SettingsPreferences {
    fun getSosMessage(): String
    fun saveSosMessage(message: String)
}
