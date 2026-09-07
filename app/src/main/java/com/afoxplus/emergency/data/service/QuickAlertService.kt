package com.afoxplus.emergency.data.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.afoxplus.emergency.data.repository.AndroidAlertNotifier
import com.afoxplus.emergency.domain.repository.AlertNotifier
import com.afoxplus.emergency.domain.usecase.TriggerQuickAlertUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuickAlertService : Service() {

    @Inject
    lateinit var triggerQuickAlertUseCase: TriggerQuickAlertUseCase

    @Inject
    lateinit var alertNotifier: AlertNotifier

    private val detector = PowerButtonPressDetector {
        triggerQuickAlertUseCase()
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_ON || intent.action == Intent.ACTION_SCREEN_OFF) {
                detector.onPressDetected()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = (alertNotifier as? AndroidAlertNotifier)?.createServiceNotification()
        if (notification != null) {
            startForeground(SERVICE_NOTIFICATION_ID, notification)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(screenReceiver)
        } catch (_: Exception) {}
        detector.reset()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val SERVICE_NOTIFICATION_ID = 1001
    }
}
