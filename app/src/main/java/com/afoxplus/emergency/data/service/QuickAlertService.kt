package com.afoxplus.emergency.data.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.afoxplus.emergency.data.repository.AndroidAlertNotifier
import com.afoxplus.emergency.domain.repository.AlertNotifier
import com.afoxplus.emergency.domain.usecase.AlertTriggerResult
import com.afoxplus.emergency.domain.usecase.TriggerAlertUseCase
import com.afoxplus.emergency.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuickAlertService : Service() {

    @Inject
    lateinit var triggerAlertUseCase: TriggerAlertUseCase

    @Inject
    lateinit var alertNotifier: AlertNotifier

    private val detector = PowerButtonPressDetector {
        launchAlertSuccessScreen(triggerAlertUseCase())
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

    private fun launchAlertSuccessScreen(result: AlertTriggerResult) {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                putExtra(MainActivity.EXTRA_SHOW_ALERT_SUCCESS, true)
                putExtra(MainActivity.EXTRA_ALERT_HISTORY_ID, result.historyEntryId)
            }
        )
    }

    companion object {
        const val SERVICE_NOTIFICATION_ID = 1001
    }
}
