package com.afoxplus.emergency.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.afoxplus.emergency.EmergencyHome
import com.afoxplus.emergency.presentation.onboarding.OnboardingRoute
import com.afoxplus.emergency.presentation.register.RegistrationRoute

@Composable
fun AppNavigation(
    backStack: SnapshotStateList<Any>
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                Onboarding -> NavEntry(key) {
                    OnboardingRoute(
                        onOnboardingFinished = {
                            backStack.clear()
                            backStack += Register
                        }
                    )
                }

                Register -> NavEntry(key) {
                    RegistrationRoute(
                        onRegistrationFinished = {
                            backStack.clear()
                            backStack += Home
                        }
                    )
                }

                Home -> NavEntry(key) {
                    EmergencyHome()
                }

                else -> NavEntry(key) {
                    error("Unknown navigation key: $key")
                }
            }
        }
    )
}
