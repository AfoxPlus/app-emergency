package com.afoxplus.emergency.presentation.contacts

import android.content.ContentResolver
import android.provider.ContactsContract

/**
 * Read-only access to the device's phone contacts.
 *
 * Kept as an interface so [ContactsViewModel] can be unit tested without any Android
 * framework dependency, following the app's separation between UI state and data access.
 */
interface ContactsRepository {
    /**
     * Returns every contact that has at least one phone number, ordered by display name.
     */
    fun getContacts(): List<Contact>
}

/**
 * [ContactsRepository] implementation backed by the Android Contacts Provider.
 */
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

                // A contact can have multiple phone numbers; keep only the first one found.
                if (!contacts.containsKey(id)) {
                    contacts[id] = Contact(id = id, name = name, phoneNumber = phoneNumber)
                }
            }
        }

        return contacts.values.toList()
    }
}
