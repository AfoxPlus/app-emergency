package com.afoxplus.emergency.presentation.periodiccheck

import android.content.Context

/**
 * Exposes the number of persisted Emergency Contacts (BR13/BR14).
 *
 * This app does not yet implement a dedicated Emergency Contacts management feature,
 * so this provider currently reads a persisted count that will be kept in sync once
 * that feature exists. Kept as an interface so [PeriodicCheckViewModel] remains
 * testable without any Android framework dependency.
 */
interface EmergencyContactsCountProvider {
    fun getContactsCount(): Int
}

/**
 * [EmergencyContactsCountProvider] implementation backed by [android.content.SharedPreferences].
 */
class EmergencyContactsCountProviderImpl(context: Context) : EmergencyContactsCountProvider {

    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getContactsCount(): Int = sharedPreferences.getInt(KEY_CONTACTS_COUNT, 0)

    companion object {
        private const val PREFERENCES_NAME = "emergency_contacts_preferences"
        private const val KEY_CONTACTS_COUNT = "contacts_count"
    }
}
