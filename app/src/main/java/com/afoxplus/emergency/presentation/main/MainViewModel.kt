package com.afoxplus.emergency.presentation.main

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afoxplus.emergency.navigation.EmergencyNavKey
import com.afoxplus.emergency.navigation.LoginRoute
import com.afoxplus.emergency.navigation.OnboardingRoute
import com.afoxplus.emergency.navigation.RegisterRoute
import com.afoxplus.emergency.presentation.onboarding.OnboardingPreferences
import com.afoxplus.emergency.presentation.register.RegistrationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
    private val registrationPreferences: RegistrationPreferences
) : ViewModel() {
    val backStack = mutableStateListOf<EmergencyNavKey>(OnboardingRoute)

    init {
        viewModelScope.launch {
            if (onboardingPreferences.isOnboardingCompleted()) {
                backStack.clear()
                backStack += if (registrationPreferences.isRegistrationCompleted()) {
                    LoginRoute
                } else {
                    RegisterRoute
                }
            }
        }
    }
}