package com.afoxplus.emergency.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.afoxplus.emergency.ui.theme.AppShapes
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyCard

@Composable
fun RegistrationRoute(
    onRegistrationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RegistrationViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) onRegistrationFinished()
    }

    RegistrationScreen(
        uiState = uiState,
        onPhoneChanged = viewModel::onPhoneChanged,
        onCodeChanged = viewModel::onCodeChanged,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onContinueClicked = viewModel::onContinueClicked,
        onBackClicked = viewModel::onBackClicked,
        modifier = modifier
    )
}

@Composable
fun RegistrationScreen(
    uiState: RegistrationUiState,
    onPhoneChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onContinueClicked: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegistrationProgress(currentStep = uiState.step)
            Spacer(modifier = Modifier.height(AppSpacing.xxl))
            RegistrationContent(
                uiState = uiState,
                onPhoneChanged = onPhoneChanged,
                onCodeChanged = onCodeChanged,
                onFirstNameChanged = onFirstNameChanged
            )
            Spacer(modifier = Modifier.weight(1f))
            uiState.error?.let {
                Text(
                    text = it.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = AppSpacing.sm)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                if (uiState.step > 0) {
                    TextButton(
                        onClick = onBackClicked,
                        modifier = Modifier.testTag("registration_back_action")
                    ) {
                        Text("Atrás")
                    }
                }
                EmergencyButton(
                    text = if (uiState.step == LAST_STEP) "Crear cuenta" else "Continuar →",
                    onClick = onContinueClicked,
                    enabled = uiState.canContinue,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("registration_continue_action")
                )
            }
        }
    }
}

@Composable
private fun RegistrationProgress(currentStep: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        repeat(STEP_COUNT) { step ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(AppSpacing.sm),
                shape = AppShapes.extraSmall,
                color = if (step <= currentStep) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {}
        }
    }
}

@Composable
private fun RegistrationContent(
    uiState: RegistrationUiState,
    onPhoneChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        Text(
            text = when (uiState.step) {
                0 -> "Número de celular"
                1 -> "Verifica tu número"
                else -> "Crea tu cuenta"
            },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = when (uiState.step) {
                0 -> "Ingresa tu número para asociarlo a tu cuenta. Te enviaremos un código por SMS."
                1 -> "Ingresa el código de 6 dígitos que enviamos a tu celular."
                else -> "Completa tus datos para personalizar tu experiencia."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        EmergencyCard(modifier = Modifier.fillMaxWidth()) {
            when (uiState.step) {
                0 -> RegistrationTextField(
                    value = uiState.phoneNumber,
                    onValueChange = onPhoneChanged,
                    label = "+51  Número de celular",
                    keyboardType = KeyboardType.Phone,
                    testTag = "registration_phone_field"
                )
                1 -> RegistrationTextField(
                    value = uiState.verificationCode,
                    onValueChange = onCodeChanged,
                    label = "Código SMS",
                    keyboardType = KeyboardType.Number,
                    testTag = "registration_code_field"
                )
                else -> {
                    RegistrationTextField(
                        value = uiState.firstName,
                        onValueChange = onFirstNameChanged,
                        label = "Nombres",
                        testTag = "registration_first_name_field"
                    )
                }
            }
        }
    }
}

@Composable
private fun RegistrationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    testTag: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = AppShapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

private val RegistrationError.message: String
    get() = when (this) {
        RegistrationError.InvalidPhone -> "Ingresa un número celular válido de 9 dígitos."
        RegistrationError.InvalidCode -> "Ingresa el código de 6 dígitos."
        RegistrationError.MissingName -> "Ingresa tus nombres y apellidos."
    }

@Preview(showBackground = true)
@Composable
private fun RegistrationScreenPreview() {
    AppemergencyTheme {
        RegistrationScreen(
            uiState = RegistrationUiState(),
            onPhoneChanged = {},
            onCodeChanged = {},
            onFirstNameChanged = {},
            onContinueClicked = {},
            onBackClicked = {}
        )
    }
}

private const val STEP_COUNT = 3
private const val LAST_STEP = 2
