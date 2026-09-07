package com.afoxplus.emergency.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyCard
import com.afoxplus.emergency.ui.theme.EmergencyPhoneNumberField
import com.afoxplus.emergency.ui.theme.EmergencyProgressIndicator
import com.afoxplus.emergency.ui.theme.EmergencyTextField

@Composable
fun EmergencyContactOnboardingScreen(
    onContactSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: EmergencyContactOnboardingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) onContactSaved()
    }
    EmergencyContactOnboardingScreen(
        uiState = uiState,
        onNameChanged = viewModel::onNameChanged,
        onPhoneChanged = viewModel::onPhoneChanged,
        onContinueClicked = viewModel::onContinueClicked,
        modifier = modifier
    )
}

@Composable
fun EmergencyContactOnboardingScreen(
    uiState: EmergencyContactOnboardingUiState,
    onNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmergencyProgressIndicator(currentStep = 3, stepCount = 4)
            Spacer(Modifier.height(AppSpacing.xxl))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "👤+",
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(AppSpacing.lg),
                    fontSize = MaterialTheme.typography.displaySmall.fontSize
                )
                Spacer(Modifier.height(AppSpacing.xl))
                Text(
                    text = "Contacto de emergencia",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(AppSpacing.sm))
                Text(
                    text = "Selecciona o ingresa a una persona de confianza.\nRecibirá una alerta inmediata con tu ubicación en caso de peligro.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(AppSpacing.xl))
                EmergencyCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Nombre completo", fontWeight = FontWeight.Bold)
                    EmergencyTextField(
                        value = uiState.name,
                        onValueChange = onNameChanged,
                        label = "Ej. Carlos Mendoza",
                        modifier = Modifier.testTag("emergency_contact_name_field")
                    )
                    Spacer(Modifier.height(AppSpacing.md))
                    Text("Número de celular", fontWeight = FontWeight.Bold)
                    EmergencyPhoneNumberField(
                        value = uiState.phoneNumber,
                        onValueChange = onPhoneChanged,
                        testTag = "emergency_contact_phone_field"
                    )
                }
                uiState.error?.let {
                    Text(
                        "Ingresa un nombre y un número celular válido.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = AppSpacing.sm)
                    )
                }
            }
            EmergencyButton(
                text = "Continuar",
                onClick = onContinueClicked,
                enabled = uiState.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emergency_contact_continue_action")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmergencyContactOnboardingScreenPreview() {
    AppemergencyTheme {
        EmergencyContactOnboardingScreen(
            uiState = EmergencyContactOnboardingUiState(),
            onNameChanged = {},
            onPhoneChanged = {},
            onContinueClicked = {}
        )
    }
}
