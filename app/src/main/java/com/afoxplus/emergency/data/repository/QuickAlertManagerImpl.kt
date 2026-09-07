package com.afoxplus.emergency.data.repository

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.afoxplus.emergency.data.service.QuickAlertService
import com.afoxplus.emergency.domain.repository.QuickAlertManager
import com.afoxplus.emergency.domain.repository.SettingsPreferences

class QuickAlertManagerImpl(
    private val context: Context,
    private val settingsPreferences: SettingsPreferences
) : QuickAlertManager {

    override fun setQuickAlertEnabled(enabled: Boolean) {
        settingsPreferences.setQuickAlertEnabled(enabled)
        if (enabled) {
            enableQuickAlert()
        } else {
            disableQuickAlert()
        }
    }

    override fun isQuickAlertEnabled(): Boolean =
        settingsPreferences.isQuickAlertEnabled()

    override fun enableQuickAlert() {
        val intent = Intent(context, QuickAlertService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        } catch (_: Exception) {}
    }

    override fun disableQuickAlert() {
        val intent = Intent(context, QuickAlertService::class.java)
        try {
            context.stopService(intent)
        } catch (_: Exception) {}
    }
}
