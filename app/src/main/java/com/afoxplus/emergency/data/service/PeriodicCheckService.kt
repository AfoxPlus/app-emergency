package com.afoxplus.emergency.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.afoxplus.emergency.R
import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.repository.PeriodicCheckPreferences
import com.afoxplus.emergency.domain.usecase.TriggerAlertUseCase
import com.afoxplus.emergency.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PeriodicCheckService : Service() {

    @Inject
    lateinit var periodicCheckPreferences: PeriodicCheckPreferences

    @Inject
    lateinit var triggerAlertUseCase: TriggerAlertUseCase

    private val handler = Handler(Looper.getMainLooper())
    private var checkRunnable: Runnable? = null
    private var timeoutRunnable: Runnable? = null
    private var currentAttemptId: Long? = null
    private var missedAttempts: Int = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val configuration = periodicCheckPreferences.getConfiguration()
        if (!configuration.enabled) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Always promote to foreground first, even when handling a confirmation, so the
        // cycle keeps running whether this call revives an already running service or
        // cold-starts a fresh process (e.g. the system killed it while waiting for a reply).
        startForeground(SERVICE_NOTIFICATION_ID, createServiceNotification())

        when (intent?.action) {
            ACTION_CONFIRM_CHECK -> {
                handleConfirmation(intent.getLongExtra(EXTRA_ATTEMPT_ID, INVALID_ATTEMPT_ID))
                return START_STICKY
            }
        }

        if (checkRunnable == null && timeoutRunnable == null && currentAttemptId == null) {
            scheduleNextCheck()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        clearPendingCallbacks()
        notificationManager.cancel(NOTIFICATION_PERIODIC_CHECK_ID)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun scheduleNextCheck() {
        clearCheckRunnable()
        val configuration = periodicCheckPreferences.getConfiguration()
        if (!configuration.enabled) {
            stopSelf()
            return
        }

        checkRunnable = Runnable {
            sendPeriodicCheckNotification()
        }.also {
            handler.postDelayed(it, configuration.frequencyMinutes.toLong() * ONE_MINUTE_MS)
        }
    }

    private fun sendPeriodicCheckNotification() {
        val configuration = periodicCheckPreferences.getConfiguration()
        if (!configuration.enabled) {
            stopSelf()
            return
        }

        val attemptId = System.currentTimeMillis()
        currentAttemptId = attemptId
        clearTimeoutRunnable()

        val confirmIntent = Intent(this, PeriodicCheckService::class.java).apply {
            action = ACTION_CONFIRM_CHECK
            putExtra(EXTRA_ATTEMPT_ID, attemptId)
        }
        val confirmPendingIntent = PendingIntent.getService(
            this,
            attemptId.hashCode(),
            confirmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_PERIODIC_ALERT_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.periodic_check_notification_title))
            .setContentText(getString(R.string.periodic_check_notification_description))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.periodic_check_notification_description))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(confirmPendingIntent)
            .addAction(
                0,
                getString(R.string.periodic_check_notification_confirm_action),
                confirmPendingIntent
            )
            .build()

        notificationManager.notify(NOTIFICATION_PERIODIC_CHECK_ID, notification)

        timeoutRunnable = Runnable {
            handleTimeout(attemptId)
        }.also {
            handler.postDelayed(it, configuration.responseTimeoutMinutes.toLong() * ONE_MINUTE_MS)
        }
    }

    private fun handleConfirmation(attemptId: Long) {
        if (attemptId == INVALID_ATTEMPT_ID) return
        // If in-memory state was lost (process restarted while the notification was
        // pending), currentAttemptId will be null; still honor the confirmation instead of
        // dropping it, otherwise the periodic cycle would silently stop forever.
        if (currentAttemptId != null && currentAttemptId != attemptId) return
        currentAttemptId = null
        missedAttempts = 0
        clearTimeoutRunnable()
        notificationManager.cancel(NOTIFICATION_PERIODIC_CHECK_ID)
        scheduleNextCheck()
    }

    private fun handleTimeout(attemptId: Long) {
        if (currentAttemptId != attemptId) return

        currentAttemptId = null
        clearTimeoutRunnable()
        notificationManager.cancel(NOTIFICATION_PERIODIC_CHECK_ID)
        missedAttempts += 1

        if (missedAttempts >= MAX_MISSED_ATTEMPTS) {
            missedAttempts = 0
            val configuration = periodicCheckPreferences.getConfiguration()
            triggerAlertUseCase(
                alertType = AlertType.PERIODIC_CHECK,
                description = getString(
                    R.string.periodic_check_timeout_alert_description,
                    configuration.responseTimeoutMinutes
                )
            )
        }

        scheduleNextCheck()
    }

    private fun clearPendingCallbacks() {
        clearCheckRunnable()
        clearTimeoutRunnable()
        currentAttemptId = null
    }

    private fun clearCheckRunnable() {
        checkRunnable?.let(handler::removeCallbacks)
        checkRunnable = null
    }

    private fun clearTimeoutRunnable() {
        timeoutRunnable?.let(handler::removeCallbacks)
        timeoutRunnable = null
    }

    private fun createServiceNotification(): Notification {
        ensureNotificationChannels()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_PERIODIC_SERVICE_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.periodic_check_service_title))
            .setContentText(getString(R.string.periodic_check_service_description))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun ensureNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val serviceChannel = NotificationChannel(
            CHANNEL_PERIODIC_SERVICE_ID,
            getString(R.string.periodic_check_service_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.periodic_check_service_channel_description)
        }
        val alertChannel = NotificationChannel(
            CHANNEL_PERIODIC_ALERT_ID,
            getString(R.string.notification_channel_alert_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.notification_channel_alert_description)
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(serviceChannel)
        notificationManager.createNotificationChannel(alertChannel)
    }

    private val notificationManager: NotificationManager
        get() = getSystemService(NotificationManager::class.java)

    companion object {
        private const val ACTION_CONFIRM_CHECK = "com.afoxplus.emergency.action.CONFIRM_PERIODIC_CHECK"
        private const val EXTRA_ATTEMPT_ID = "extra_attempt_id"
        private const val INVALID_ATTEMPT_ID = -1L
        private const val ONE_MINUTE_MS = 60_000L
        private const val MAX_MISSED_ATTEMPTS = 3
        private const val CHANNEL_PERIODIC_SERVICE_ID = "channel_periodic_check_service"
        private const val CHANNEL_PERIODIC_ALERT_ID = "channel_emergency_alerts"
        private const val NOTIFICATION_PERIODIC_CHECK_ID = 2101
        private const val SERVICE_NOTIFICATION_ID = 1101
    }
}
