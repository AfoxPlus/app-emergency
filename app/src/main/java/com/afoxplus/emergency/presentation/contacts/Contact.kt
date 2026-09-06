package com.afoxplus.emergency.presentation.contacts

/**
 * A contact retrieved from the device's Contacts Provider.
 */
data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String
)
