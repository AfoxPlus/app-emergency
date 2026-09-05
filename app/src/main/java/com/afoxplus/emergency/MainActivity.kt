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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.afoxplus.emergency.presentation.onboarding.OnboardingPreferencesImpl
import com.afoxplus.emergency.presentation.onboarding.OnboardingRoute
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyButtonVariant
import com.afoxplus.emergency.ui.theme.EmergencyCard
import com.afoxplus.emergency.ui.theme.EmergencySecondaryButton
import com.afoxplus.emergency.ui.theme.EmergencyStatusPill
import com.afoxplus.emergency.ui.theme.EmergencyTextField

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppemergencyTheme {
                val context = LocalContext.current
                val onboardingPreferences = remember { OnboardingPreferencesImpl(context) }
                var isOnboardingCompleted by remember {
                    mutableStateOf(onboardingPreferences.isOnboardingCompleted())
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isOnboardingCompleted) {
                        EmergencyHome(
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        OnboardingRoute(
                            preferences = onboardingPreferences,
                            onOnboardingFinished = { isOnboardingCompleted = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
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