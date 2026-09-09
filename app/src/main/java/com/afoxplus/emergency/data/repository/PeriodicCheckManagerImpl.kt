package com.afoxplus.emergency.data.repository

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.afoxplus.emergency.data.service.PeriodicCheckService
import com.afoxplus.emergency.domain.repository.PeriodicCheckManager
import com.afoxplus.emergency.domain.repository.PeriodicCheckPreferences

class PeriodicCheckManagerImpl(
    private val context: Context,
    private val periodicCheckPreferences: PeriodicCheckPreferences
) : PeriodicCheckManager {

    override fun setPeriodicCheckEnabled(enabled: Boolean) {
        val configuration = periodicCheckPreferences.getConfiguration()
        periodicCheckPreferences.saveConfiguration(
            configuration.copy(
                enabled = enabled,
                lastConfigurationUpdate = System.currentTimeMillis()
            )
        )
        if (enabled) {
            enablePeriodicCheck()
        } else {
            disablePeriodicCheck()
        }
    }

    override fun enablePeriodicCheck() {
        val intent = Intent(context, PeriodicCheckService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        } catch (_: Exception) {}
    }

    override fun disablePeriodicCheck() {
        val intent = Intent(context, PeriodicCheckService::class.java)
        try {
            context.stopService(intent)
        } catch (_: Exception) {}
    }
}
