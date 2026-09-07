package com.afoxplus.emergency.presentation.features.onboarding

data class EmergencyContactOnboardingUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val error: EmergencyContactOnboardingError? = null,
    val isCompleted: Boolean = false
) {
    val canContinue: Boolean
        get() = name.isNotBlank() && phoneNumber.filter(Char::isDigit).length == PHONE_DIGITS

    companion object {
        const val PHONE_DIGITS = 9
    }
}

enum class EmergencyContactOnboardingError {
    MissingContact
}
