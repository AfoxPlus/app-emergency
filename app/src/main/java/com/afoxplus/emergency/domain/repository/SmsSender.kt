package com.afoxplus.emergency.domain.repository

/**
 * Abstraction for sending SMS text messages.
 */
interface SmsSender {
    fun sendSms(phoneNumber: String, message: String): Result<Unit>
}
