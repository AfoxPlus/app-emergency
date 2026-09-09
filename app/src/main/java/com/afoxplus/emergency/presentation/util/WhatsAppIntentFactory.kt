package com.afoxplus.emergency.presentation.util

import android.content.Intent
import android.net.Uri

object WhatsAppIntentFactory {
    fun create(phoneNumber: String, message: String): Intent {
        val sanitizedPhoneNumber = phoneNumber.filter { it.isDigit() || it == '+' }
        val uri = Uri.parse("https://wa.me/$sanitizedPhoneNumber?text=${Uri.encode(message)}")

        return Intent(Intent.ACTION_VIEW, uri).apply {
            `package` = WHATSAPP_PACKAGE
        }
    }

    private const val WHATSAPP_PACKAGE = "com.whatsapp"
}
