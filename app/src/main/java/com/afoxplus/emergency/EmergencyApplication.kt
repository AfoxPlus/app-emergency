package com.afoxplus.emergency

import android.app.Application
import com.afoxplus.emergency.domain.repository.QuickAlertManager
import com.afoxplus.emergency.domain.repository.SettingsPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EmergencyApplication : Application() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    @Inject
    lateinit var quickAlertManager: QuickAlertManager

    override fun onCreate() {
        super.onCreate()
        if (settingsPreferences.isQuickAlertEnabled()) {
            quickAlertManager.enableQuickAlert()
        }
    }
}
