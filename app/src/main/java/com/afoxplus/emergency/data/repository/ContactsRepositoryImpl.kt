package com.afoxplus.emergency.data.repository

import android.content.ContentResolver
import android.provider.ContactsContract
import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.repository.ContactsRepository

class ContactsRepositoryImpl(
    private val contentResolver: ContentResolver
) : ContactsRepository {

    override fun getContacts(): List<Contact> {
        val contacts = LinkedHashMap<String, Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (idIndex < 0 || nameIndex < 0 || numberIndex < 0) return@use

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex) ?: continue
                val name = cursor.getString(nameIndex) ?: continue
                val phoneNumber = cursor.getString(numberIndex) ?: continue

                if (!contacts.containsKey(id)) {
                    contacts[id] = Contact(id = id, name = name, phoneNumber = phoneNumber)
                }
            }
        }

        return contacts.values.toList()
    }
}
