package com.afoxplus.emergency.presentation.settings

import com.afoxplus.emergency.R

/**
 * The 4 System Permissions surfaced on the Settings screen (Settings AC04/AC05), each backed
 * by a real OS-level runtime permission checked/requested from [SettingsScreen].
 */
enum class SettingsPermissionType(
    val titleRes: Int,
    val descriptionRes: Int,
    val grantedLabelRes: Int
) {
    LOCATION(
        titleRes = R.string.settings_permission_location_title,
        descriptionRes = R.string.settings_permission_location_description,
        grantedLabelRes = R.string.settings_permission_granted
    ),
    CONTACTS(
        titleRes = R.string.settings_permission_contacts_title,
        descriptionRes = R.string.settings_permission_contacts_description,
        grantedLabelRes = R.string.settings_permission_granted
    ),
    NOTIFICATIONS(
        titleRes = R.string.settings_permission_notifications_title,
        descriptionRes = R.string.settings_permission_notifications_description,
        grantedLabelRes = R.string.settings_permission_active
    ),
    CAMERA_MICROPHONE(
        titleRes = R.string.settings_permission_camera_title,
        descriptionRes = R.string.settings_permission_camera_description,
        grantedLabelRes = R.string.settings_permission_granted
    )
}
