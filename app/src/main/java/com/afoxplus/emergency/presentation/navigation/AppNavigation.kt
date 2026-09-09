package com.afoxplus.emergency.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.afoxplus.emergency.presentation.features.alert.AlertSuccessScreen
import com.afoxplus.emergency.presentation.features.alert.AlertSuccessViewModel
import com.afoxplus.emergency.presentation.features.contacts.ContactsScreen
import com.afoxplus.emergency.presentation.features.history.HistoryScreen
import com.afoxplus.emergency.presentation.features.home.HomeScreen
import com.afoxplus.emergency.presentation.features.login.LoginScreen
import com.afoxplus.emergency.presentation.features.onboarding.EmergencyContactOnboardingScreen
import com.afoxplus.emergency.presentation.features.onboarding.OnboardingScreen
import com.afoxplus.emergency.presentation.features.periodiccheck.PeriodicCheckScreen
import com.afoxplus.emergency.presentation.features.register.RegistrationScreen
import com.afoxplus.emergency.presentation.features.settings.SettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    backStack: SnapshotStateList<EmergencyNavKey>
) {
    NavDisplay(
        backStack = backStack,
        onBack = {
            when {
                backStack.size > 1 -> backStack.removeLastOrNull()
                backStack.lastOrNull() is AlertSuccessRoute -> navigateToTopLevelTab(backStack, HomeRoute)
            }
        },
        modifier = modifier.systemBarsPadding(),
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator()
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
                        backStack += EmergencyContactOnboardingRoute
                    }
                )
            }
            entry<EmergencyContactOnboardingRoute> {
                EmergencyContactOnboardingScreen(
                    onContactSaved = {
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
                    onAlertClick = { latitude, longitude, historyEntryId ->
                        backStack += AlertSuccessRoute(
                            latitude = latitude,
                            longitude = longitude,
                            historyEntryId = historyEntryId
                        )
                    },
                    onPeriodicCheckClick = { backStack += PeriodicCheckRoute },
                    onNavigateToContacts = { navigateToTopLevelTab(backStack, ContactsRoute) },
                    onNavigateToHistory = { navigateToTopLevelTab(backStack, HistoryRoute) },
                    onNavigateToSettings = { navigateToTopLevelTab(backStack, SettingsRoute) }
                )
            }
            entry<AlertSuccessRoute> { route ->
                val alertSuccessViewModel: AlertSuccessViewModel = hiltViewModel()
                val uiState by alertSuccessViewModel.uiState.collectAsStateWithLifecycle()
                AlertSuccessScreen(
                    uiState = uiState,
                    latitude = route.latitude,
                    longitude = route.longitude,
                    onCancelAlert = {
                        alertSuccessViewModel.onCancelAlert(route.historyEntryId)
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        } else {
                            navigateToTopLevelTab(backStack, HomeRoute)
                        }
                    },
                    onBackToHome = { navigateToTopLevelTab(backStack, HomeRoute) }
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
                    onNavigateToHistory = { navigateToTopLevelTab(backStack, HistoryRoute) },
                    onNavigateToSettings = { navigateToTopLevelTab(backStack, SettingsRoute) }
                )
            }
            entry<HistoryRoute> {
                HistoryScreen(
                    onBackClick = { navigateToTopLevelTab(backStack, HomeRoute) },
                    onNavigateToHome = { navigateToTopLevelTab(backStack, HomeRoute) },
                    onNavigateToContacts = { navigateToTopLevelTab(backStack, ContactsRoute) },
                    onNavigateToSettings = { navigateToTopLevelTab(backStack, SettingsRoute) }
                )
            }
            entry<SettingsRoute> {
                SettingsScreen(
                    onNavigateToHome = { navigateToTopLevelTab(backStack, HomeRoute) },
                    onNavigateToHistory = { navigateToTopLevelTab(backStack, HistoryRoute) },
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
