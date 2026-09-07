package com.afoxplus.emergency.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.afoxplus.emergency.domain.repository.QuickAlertManager
import com.afoxplus.emergency.domain.repository.SettingsPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    @Inject
    lateinit var quickAlertManager: QuickAlertManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            if (settingsPreferences.isQuickAlertEnabled()) {
                quickAlertManager.enableQuickAlert()
            }
        }
    }
}
