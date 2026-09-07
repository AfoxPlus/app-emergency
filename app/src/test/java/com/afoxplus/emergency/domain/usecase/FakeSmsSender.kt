package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.repository.SmsSender

class FakeSmsSender(
    var shouldSucceed: Boolean = true
) : SmsSender {

    val sentMessages = mutableListOf<Pair<String, String>>()

    override fun sendSms(phoneNumber: String, message: String): Result<Unit> {
        return if (shouldSucceed) {
            sentMessages.add(phoneNumber to message)
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("SMS delivery failed"))
        }
    }
}
