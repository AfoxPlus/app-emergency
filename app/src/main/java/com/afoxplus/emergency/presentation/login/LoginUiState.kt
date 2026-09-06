package com.afoxplus.emergency.presentation.login

data class LoginUiState(
    val pin: String = "",
    val error: LoginError? = null,
    val isCompleted: Boolean = false
) {
    val canLogin: Boolean
        get() = pin.length == PIN_LENGTH

    companion object {
        const val PIN_LENGTH = 4
    }
}

enum class LoginError {
    InvalidPin
}
