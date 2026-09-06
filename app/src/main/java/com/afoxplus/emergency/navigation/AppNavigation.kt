package com.afoxplus.emergency.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.afoxplus.emergency.presentation.main.EmergencyHome
import com.afoxplus.emergency.presentation.onboarding.OnboardingRoute
import com.afoxplus.emergency.presentation.register.RegistrationRoute

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    backStack: SnapshotStateList<EmergencyNavKey>
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<OnboardingRoute> {
                OnboardingRoute(
                    onOnboardingFinished = {
                        backStack.clear()
                        backStack += RegisterRoute
                    }
                )
            }
            entry<RegisterRoute> {
                RegistrationRoute(
                    onRegistrationFinished = {
                        backStack.clear()
                        backStack += HomeRoute
                    }
                )
            }

            entry<HomeRoute> {
                EmergencyHome()
            }
        }
    )
}
