package com.afoxplus.emergency.presentation.register

data class RegistrationUiState(
    val step: Int = 0,
    val phoneNumber: String = "",
    val firstName: String = "",
    val error: RegistrationError? = null,
    val isCompleted: Boolean = false
) {
    val canContinue: Boolean
        get() = when (step) {
            0 -> phoneNumber.filter(Char::isDigit).length == PHONE_DIGITS
            1 -> firstName.isNotBlank()
            else -> false
        }

    companion object {
        const val PHONE_DIGITS = 9
    }
}

enum class RegistrationError {
    InvalidPhone,
    MissingName
}
