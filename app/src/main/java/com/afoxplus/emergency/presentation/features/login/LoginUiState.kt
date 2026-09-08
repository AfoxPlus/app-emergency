package com.afoxplus.emergency.presentation.features.login

data class LoginUiState(
    val mode: LoginMode = LoginMode.SignIn,
    val stage: PinRegistrationStage = PinRegistrationStage.EnterPin,
    val pin: String = "",
    val firstPin: String = "",
    val error: LoginError? = null,
    val isCompleted: Boolean = false
) {
    val canSubmit: Boolean
        get() = pin.length == PIN_LENGTH

    companion object {
        const val PIN_LENGTH = 4
    }
}

enum class LoginMode {
    SignIn,
    PinRegistration
}

enum class PinRegistrationStage {
    EnterPin,
    ConfirmPin
}

enum class LoginError {
    InvalidPin,
    PinMismatch,
    IncorrectPin
}
