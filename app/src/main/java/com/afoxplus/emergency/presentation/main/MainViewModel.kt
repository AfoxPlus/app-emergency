package com.afoxplus.emergency.presentation.main

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afoxplus.emergency.presentation.navigation.EmergencyNavKey
import com.afoxplus.emergency.presentation.navigation.LoginRoute
import com.afoxplus.emergency.presentation.navigation.OnboardingRoute
import com.afoxplus.emergency.presentation.navigation.RegisterRoute
import com.afoxplus.emergency.presentation.navigation.EmergencyContactOnboardingRoute
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import com.afoxplus.emergency.domain.repository.OnboardingPreferences
import com.afoxplus.emergency.domain.repository.RegistrationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
    private val registrationPreferences: RegistrationPreferences,
    private val emergencyContactRepository: EmergencyContactRepository
) : ViewModel() {
    val backStack = mutableStateListOf<EmergencyNavKey>(OnboardingRoute)

    init {
        viewModelScope.launch {
            if (onboardingPreferences.isOnboardingCompleted()) {
                backStack.clear()
                backStack += if (registrationPreferences.isRegistrationCompleted()) {
                    if (emergencyContactRepository.getEmergencyContacts().isEmpty()) {
                        EmergencyContactOnboardingRoute
                    } else {
                        LoginRoute
                    }
                } else {
                    RegisterRoute
                }
            }
        }
    }
}