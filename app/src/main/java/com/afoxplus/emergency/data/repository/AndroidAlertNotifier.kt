package com.afoxplus.emergency.data.repository

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.afoxplus.emergency.R
import com.afoxplus.emergency.domain.repository.AlertNotifier
import com.afoxplus.emergency.presentation.main.MainActivity

class AndroidAlertNotifier(private val context: Context) : AlertNotifier {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_SERVICE_ID,
                context.getString(R.string.notification_channel_service_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_service_description)
            }

            val alertChannel = NotificationChannel(
                CHANNEL_ALERT_ID,
                context.getString(R.string.notification_channel_alert_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_alert_description)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    fun createServiceNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_SERVICE_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.quick_alert_service_title))
            .setContentText(context.getString(R.string.quick_alert_service_description))
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun notifyAlertSent(contactNames: List<String>) {
        val content = if (contactNames.isNotEmpty()) {
            context.getString(R.string.quick_alert_sent_to_contacts, contactNames.joinToString(", "))
        } else {
            context.getString(R.string.quick_alert_sent_success)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERT_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.quick_alert_sent_title))
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ALERT_SENT_ID, notification)
        } catch (_: SecurityException) {}
    }

    override fun notifyAlertFailed(errorMessage: String) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERT_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.quick_alert_error_title))
            .setContentText(errorMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(errorMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ALERT_FAILED_ID, notification)
        } catch (_: SecurityException) {}
    }

    override fun notifyAlertSuccessScreen(historyEntryId: String?) {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            putExtra(MainActivity.EXTRA_SHOW_ALERT_SUCCESS, true)
            putExtra(MainActivity.EXTRA_ALERT_HISTORY_ID, historyEntryId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            4,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERT_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.quick_alert_sent_title))
            .setContentText(context.getString(R.string.quick_alert_sent_success))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ALERT_SUCCESS_SCREEN_ID, notification)
        } catch (_: SecurityException) {}
    }

    override fun notifyNoContactsConfigured() {
        val pendingIntent = PendingIntent.getActivity(
            context,
            3,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERT_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.quick_alert_no_contacts_title))
            .setContentText(context.getString(R.string.quick_alert_no_contacts_description))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.quick_alert_no_contacts_description)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_NO_CONTACTS_ID, notification)
        } catch (_: SecurityException) {}
    }

    companion object {
        const val CHANNEL_SERVICE_ID = "channel_quick_alert_service"
        const val CHANNEL_ALERT_ID = "channel_emergency_alerts"
        const val NOTIFICATION_ALERT_SENT_ID = 2001
        const val NOTIFICATION_ALERT_FAILED_ID = 2002
        const val NOTIFICATION_NO_CONTACTS_ID = 2003
        const val NOTIFICATION_ALERT_SUCCESS_SCREEN_ID = 2004
    }
}
