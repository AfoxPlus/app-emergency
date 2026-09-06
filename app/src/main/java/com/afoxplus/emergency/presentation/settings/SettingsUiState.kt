package com.afoxplus.emergency.presentation.settings

import kotlin.math.ceil

/**
 * Immutable UI state for the Settings screen and its "Emergency Message" bottom sheet.
 */
data class SettingsUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val sosMessage: String = "",
    val permissions: Map<SettingsPermissionType, Boolean> =
        SettingsPermissionType.entries.associateWith { false },
    val isMessageSheetVisible: Boolean = false,
    val draftMessage: String = ""
) {
    /** Avatar initials derived from the real account name (Settings AC02). */
    val initials: String
        get() = name.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString(separator = "") { it.first().uppercase() }

    val grantedPermissionsCount: Int
        get() = permissions.values.count { it }

    val totalPermissionsCount: Int
        get() = permissions.size

    /** SMS character/segment counts always reflect the persisted message (Settings AC03). */
    val smsCharacterCount: Int
        get() = sosMessage.length

    val smsSegmentCount: Int
        get() = segmentsFor(sosMessage)

    /** Character/segment counts for the message being edited in the bottom sheet (Settings AC07). */
    val draftCharacterCount: Int
        get() = draftMessage.length

    val draftSegmentCount: Int
        get() = segmentsFor(draftMessage)

    fun isPermissionGranted(type: SettingsPermissionType): Boolean = permissions[type] == true

    private fun segmentsFor(message: String): Int =
        if (message.isEmpty()) {
            1
        } else {
            ceil(message.length / SMS_SEGMENT_LENGTH.toDouble()).toInt()
        }

    companion object {
        const val SMS_MAX_STANDARD_LENGTH = 160
        const val SMS_SEGMENT_LENGTH = 160
    }
}
