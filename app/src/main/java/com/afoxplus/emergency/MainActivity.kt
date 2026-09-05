package com.afoxplus.emergency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import com.afoxplus.emergency.navigation.AppNavigation
import com.afoxplus.emergency.navigation.Home
import com.afoxplus.emergency.navigation.Onboarding
import com.afoxplus.emergency.presentation.onboarding.OnboardingPreferences
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyButtonVariant
import com.afoxplus.emergency.ui.theme.EmergencyCard
import com.afoxplus.emergency.ui.theme.EmergencySecondaryButton
import com.afoxplus.emergency.ui.theme.EmergencyStatusPill
import com.afoxplus.emergency.ui.theme.EmergencyTextField
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var onboardingPreferences: OnboardingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppemergencyTheme {
                val backStack = remember {
                    listOf<Any>(
                        if (onboardingPreferences.isOnboardingCompleted()) Home else Onboarding
                    ).toMutableStateList()
                }
                AppNavigation(backStack = backStack)
            }
        }
    }
}

@Composable
fun EmergencyHome(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xl)
        ) {
            Text(
                text = "Emergency Assist",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Fast help when every second matters.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            EmergencyStatusPill(text = "System online")

            EmergencyCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Urgent action",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                EmergencyTextField(
                    value = "",
                    onValueChange = {},
                    label = "Location or address"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    EmergencyButton(
                        text = "Call 911",
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        variant = EmergencyButtonVariant.Primary
                    )
                    EmergencySecondaryButton(
                        text = "Share",
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            EmergencyCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quick actions",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    EmergencyButton(
                        text = "Medical",
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                    EmergencyButton(
                        text = "Fire",
                        onClick = {},
                        modifier = Modifier
                            .weight(1f),
                        variant = EmergencyButtonVariant.Secondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyHomePreview() {
    AppemergencyTheme {
        EmergencyHome()
    }
}