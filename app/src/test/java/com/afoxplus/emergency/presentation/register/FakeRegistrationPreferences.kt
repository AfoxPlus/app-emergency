package com.afoxplus.emergency.presentation.register

import com.afoxplus.emergency.domain.repository.RegistrationPreferences

class FakeRegistrationPreferences(
    private var completed: Boolean = false
) : RegistrationPreferences {
    private var name: String = ""
    private var phoneNumber: String = ""

    override fun isRegistrationCompleted(): Boolean = completed

    override fun setRegistrationCompleted(completed: Boolean) {
        this.completed = completed
    }

    override fun saveProfile(name: String, phoneNumber: String) {
        this.name = name
        this.phoneNumber = phoneNumber
    }

    override fun getName(): String = name

    override fun getPhoneNumber(): String = phoneNumber
}
