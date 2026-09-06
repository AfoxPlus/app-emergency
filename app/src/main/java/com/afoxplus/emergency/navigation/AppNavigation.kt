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
import com.afoxplus.emergency.presentation.home.HomeScreen
import com.afoxplus.emergency.presentation.onboarding.OnboardingScreen
import com.afoxplus.emergency.presentation.register.RegistrationScreen
import com.afoxplus.emergency.presentation.login.LoginScreen

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
                OnboardingScreen(
                    onOnboardingFinished = {
                        backStack.clear()
                        backStack += RegisterRoute
                    }
                )
            }
            entry<RegisterRoute> {
                RegistrationScreen(
                    onRegistrationFinished = {
                        backStack.clear()
                        backStack += HomeRoute
                    }
                )
            }

            entry<HomeRoute> {
                HomeScreen()
            }
            entry<LoginRoute> {
                LoginScreen(
                    onLoginFinished = {
                        backStack.clear()
                        backStack += HomeRoute
                    }
                )
            }
        }
    )
}
