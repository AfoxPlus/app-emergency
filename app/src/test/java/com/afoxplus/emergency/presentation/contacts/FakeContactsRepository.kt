package com.afoxplus.emergency.presentation.contacts

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.repository.ContactsRepository

class FakeContactsRepository(
    private var contacts: List<Contact> = emptyList()
) : ContactsRepository {

    override fun getContacts(): List<Contact> = contacts

    fun setContacts(contacts: List<Contact>) {
        this.contacts = contacts
    }
}
