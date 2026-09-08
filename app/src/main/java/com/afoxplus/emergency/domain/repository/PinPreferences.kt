package com.afoxplus.emergency.domain.repository

/**
 * Persistence contract for the user's authentication PIN.
 */
interface PinPreferences {
    fun hasPin(): Boolean
    fun savePin(pin: String)
    fun getPin(): String
}
