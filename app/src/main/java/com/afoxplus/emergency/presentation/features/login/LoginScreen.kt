package com.afoxplus.emergency.presentation.features.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afoxplus.emergency.presentation.ui.theme.AppShapes
import com.afoxplus.emergency.presentation.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.presentation.ui.theme.AppSpacing

@Composable
fun LoginScreen(
    onLoginFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) onLoginFinished()
    }

    LoginScreen(
        uiState = uiState,
        onDigitClicked = viewModel::onDigitClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
        onPrimaryActionClicked = viewModel::onPrimaryActionClicked,
        onErrorDismissed = viewModel::onErrorDismissed,
        modifier = modifier
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onDigitClicked: (Int) -> Unit,
    onDeleteClicked: () -> Unit,
    onPrimaryActionClicked: () -> Unit,
    onErrorDismissed: () -> Unit = {},
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
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "▣",
                    modifier = Modifier.padding(top = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            Text(
                text = titleFor(uiState),
                modifier = Modifier.testTag("login_title"),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                text = subtitleFor(uiState),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(AppSpacing.xl))
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
                repeat(LoginUiState.PIN_LENGTH) { index ->
                    Surface(
                        modifier = Modifier
                            .size(20.dp)
                            .testTag("login_pin_indicator_$index"),
                        shape = CircleShape,
                        color = if (index < uiState.pin.length) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {}
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9), listOf(0)).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { digit ->
                        Surface(
                            onClick = { onDigitClicked(digit) },
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("login_digit_$digit"),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = digit.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    if (row.size == 1) {
                        IconButton(
                            onClick = onDeleteClicked,
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("login_delete_action")
                        ) {
                            Text("⌫", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(AppSpacing.sm))
            }
            Surface(
                onClick = onPrimaryActionClicked,
                enabled = uiState.canSubmit,
                shape = AppShapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_primary_action")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppSpacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = primaryActionTextFor(uiState),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    uiState.error?.let { error ->
        AlertDialog(
            modifier = Modifier.testTag("login_error_dialog"),
            onDismissRequest = onErrorDismissed,
            title = { Text(errorTitleFor(error), fontWeight = FontWeight.Bold) },
            text = { Text(errorMessageFor(error)) },
            confirmButton = {
                TextButton(onClick = onErrorDismissed) {
                    Text("Entendido")
                }
            }
        )
    }
}

private fun titleFor(uiState: LoginUiState): String = when (uiState.mode) {
    LoginMode.SignIn -> "Ingresa tu PIN"
    LoginMode.PinRegistration -> when (uiState.stage) {
        PinRegistrationStage.EnterPin -> "Crea tu PIN"
        PinRegistrationStage.ConfirmPin -> "Confirma tu PIN"
    }
}

private fun subtitleFor(uiState: LoginUiState): String = when (uiState.mode) {
    LoginMode.SignIn -> "Introduce tu código de seguridad de 4\ndígitos para acceder a CAYU."
    LoginMode.PinRegistration -> when (uiState.stage) {
        PinRegistrationStage.EnterPin ->
            "Crea un PIN de 4 dígitos para proteger\ntu cuenta de CAYU."
        PinRegistrationStage.ConfirmPin ->
            "Vuelve a introducir el mismo PIN de 4\ndígitos para confirmarlo."
    }
}

private fun primaryActionTextFor(uiState: LoginUiState): String = when (uiState.mode) {
    LoginMode.SignIn -> "Iniciar sesión  →"
    LoginMode.PinRegistration -> when (uiState.stage) {
        PinRegistrationStage.EnterPin -> "Continuar  →"
        PinRegistrationStage.ConfirmPin -> "Confirmar PIN  →"
    }
}

private fun errorTitleFor(error: LoginError): String = when (error) {
    LoginError.InvalidPin -> "PIN incompleto"
    LoginError.PinMismatch -> "Los PIN no coinciden"
    LoginError.IncorrectPin -> "PIN incorrecto"
}

private fun errorMessageFor(error: LoginError): String = when (error) {
    LoginError.InvalidPin -> "Ingresa un PIN de 4 dígitos."
    LoginError.PinMismatch -> "El PIN de confirmación no coincide. Intenta nuevamente."
    LoginError.IncorrectPin -> "El PIN ingresado es incorrecto. Intenta nuevamente."
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    AppemergencyTheme {
        LoginScreen(LoginUiState(), {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenRegistrationPreview() {
    AppemergencyTheme {
        LoginScreen(LoginUiState(mode = LoginMode.PinRegistration), {}, {}, {})
    }
}
