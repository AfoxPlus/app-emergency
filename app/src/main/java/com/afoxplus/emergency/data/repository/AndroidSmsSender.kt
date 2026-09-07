package com.afoxplus.emergency.data.repository

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import com.afoxplus.emergency.domain.repository.SmsSender

class AndroidSmsSender(private val context: Context) : SmsSender {
    override fun sendSms(phoneNumber: String, message: String): Result<Unit> {
        return try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
