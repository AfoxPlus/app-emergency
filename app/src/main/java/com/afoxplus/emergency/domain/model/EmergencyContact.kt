package com.afoxplus.emergency.domain.model

/**
 * The role assigned to a contact selected as an Emergency Contact.
 *
 * Only one [PRIMARY] contact can exist at a time: it is the first Emergency Contact added.
 * Every other Emergency Contact is a [BACKUP].
 */
enum class EmergencyContactType {
    PRIMARY,
    BACKUP
}

/**
 * A [Contact] selected by the user to be notified automatically when the Emergency Button
 * or Panic Mode is activated.
 */
data class EmergencyContact(
    val contactId: String,
    val name: String,
    val phoneNumber: String,
    val type: EmergencyContactType
)
