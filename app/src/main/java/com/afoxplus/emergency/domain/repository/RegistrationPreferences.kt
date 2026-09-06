package com.afoxplus.emergency.domain.repository

/**
 * Persistence contract for registration state and user profile.
 */
interface RegistrationPreferences {
    fun isRegistrationCompleted(): Boolean
    fun setRegistrationCompleted(completed: Boolean)
    fun saveProfile(name: String, phoneNumber: String)
    fun getName(): String
    fun getPhoneNumber(): String
}
