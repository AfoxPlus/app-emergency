package com.afoxplus.emergency.presentation.register

data class RegistrationUiState(
    val step: Int = 0,
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val firstName: String = "",
    val error: RegistrationError? = null,
    val isCompleted: Boolean = false
) {
    val canContinue: Boolean
        get() = when (step) {
            0 -> phoneNumber.filter(Char::isDigit).length == PHONE_DIGITS
            1 -> verificationCode.length == VERIFICATION_CODE_LENGTH
            2 -> firstName.isNotBlank()
            else -> false
        }

    companion object {
        const val PHONE_DIGITS = 9
        const val VERIFICATION_CODE_LENGTH = 6
    }
}

enum class RegistrationError {
    InvalidPhone,
    InvalidCode,
    MissingName
}
