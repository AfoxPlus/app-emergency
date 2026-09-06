package com.afoxplus.emergency.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.rememberViewModelStoreNavEntryDecorator
import com.afoxplus.emergency.presentation.contacts.ContactsScreen
import com.afoxplus.emergency.presentation.home.HomeScreen
import com.afoxplus.emergency.presentation.login.LoginScreen
import com.afoxplus.emergency.presentation.onboarding.OnboardingScreen
import com.afoxplus.emergency.presentation.periodiccheck.PeriodicCheckScreen
import com.afoxplus.emergency.presentation.register.RegistrationScreen
import com.afoxplus.emergency.presentation.settings.SettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    backStack: SnapshotStateList<EmergencyNavKey>
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier.systemBarsPadding(),
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
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
                        backStack += LoginRoute
                    }
                )
            }
            entry<LoginRoute> {
                LoginScreen(
                    onLoginFinished = {
                        backStack.clear()
                        backStack += HomeRoute
                    }
                )
            }

            entry<HomeRoute> {
                HomeScreen(
                    onPeriodicCheckClick = { backStack += PeriodicCheckRoute },
                    onNavigateToContacts = { navigateToTopLevelTab(backStack, ContactsRoute) },
                    onNavigateToSettings = { navigateToTopLevelTab(backStack, SettingsRoute) }
                )
            }
            entry<PeriodicCheckRoute> {
                PeriodicCheckScreen(
                    onBackClick = { backStack.removeLastOrNull() },
                    onActivated = { backStack.removeLastOrNull() },
                    onNavigateToEmergencyContacts = { navigateToTopLevelTab(backStack, ContactsRoute) }
                )
            }
            entry<ContactsRoute> {
                ContactsScreen(
                    onNavigateToHome = { navigateToTopLevelTab(backStack, HomeRoute) },
                    onNavigateToSettings = { navigateToTopLevelTab(backStack, SettingsRoute) }
                )
            }
            entry<SettingsRoute> {
                SettingsScreen(
                    onNavigateToHome = { navigateToTopLevelTab(backStack, HomeRoute) },
                    onNavigateToContacts = { navigateToTopLevelTab(backStack, ContactsRoute) }
                )
            }
        }
    )
}

private fun navigateToTopLevelTab(
    backStack: SnapshotStateList<EmergencyNavKey>,
    target: EmergencyNavKey
) {
    if (backStack.lastOrNull() == target) return
    backStack.clear()
    backStack += target
}
