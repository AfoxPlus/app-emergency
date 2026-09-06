package com.afoxplus.emergency.domain.repository

/**
 * Exposes the number of persisted Emergency Contacts.
 */
interface EmergencyContactsCountProvider {
    fun getContactsCount(): Int
}
