package com.afoxplus.emergency.presentation.contacts

class FakeContactsRepository(
    private var contacts: List<Contact> = emptyList()
) : ContactsRepository {

    override fun getContacts(): List<Contact> = contacts

    fun setContacts(contacts: List<Contact>) {
        this.contacts = contacts
    }
}
