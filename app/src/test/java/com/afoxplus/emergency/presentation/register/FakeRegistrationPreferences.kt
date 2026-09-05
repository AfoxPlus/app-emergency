package com.afoxplus.emergency.presentation.register

class FakeRegistrationPreferences(
    private var completed: Boolean = false
) : RegistrationPreferences {
    override fun isRegistrationCompleted(): Boolean = completed

    override fun setRegistrationCompleted(completed: Boolean) {
        this.completed = completed
    }
}
