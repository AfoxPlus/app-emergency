package com.afoxplus.emergency.domain.repository

import com.afoxplus.emergency.domain.model.Contact

/**
 * Read-only access to the device's phone contacts.
 */
interface ContactsRepository {
    fun getContacts(): List<Contact>
}
