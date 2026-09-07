package com.afoxplus.emergency.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyCard
import com.afoxplus.emergency.ui.theme.EmergencyColors
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
            modifier = Modifier.fillMaxSize().padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProgressIndicator()
            Spacer(Modifier.height(AppSpacing.xxl))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("👤+", modifier = Modifier.clip(CircleShape).padding(AppSpacing.lg), fontSize = MaterialTheme.typography.displaySmall.fontSize)
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
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        EmergencyTextField(value = "+51", onValueChange = {}, label = "", modifier = Modifier.width(96.dp))
                        EmergencyTextField(
                            value = uiState.phoneNumber,
                            onValueChange = onPhoneChanged,
                            label = "987 654 321",
                            keyboardType = KeyboardType.Phone,
                            modifier = Modifier.weight(1f).testTag("emergency_contact_phone_field")
                        )
                    }
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
                text = "Continuar →",
                onClick = onContinueClicked,
                enabled = uiState.canContinue,
                modifier = Modifier.fillMaxWidth().testTag("emergency_contact_continue_action")
            )
        }
    }
}

@Composable
private fun ProgressIndicator(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        repeat(4) { index ->
            Surface(
                modifier = Modifier.weight(1f).height(AppSpacing.sm),
                shape = MaterialTheme.shapes.extraSmall,
                color = if (index < 3) EmergencyColors.Secondary else MaterialTheme.colorScheme.surfaceVariant
            ) {}
        }
        Text("3/4", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
