package com.afoxplus.emergency.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.LocalContentColor
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
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.AppSpacing

@Composable
fun LoginRoute(
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
        onLoginClicked = viewModel::onLoginClicked,
        modifier = modifier
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onDigitClicked: (Int) -> Unit,
    onDeleteClicked: () -> Unit,
    onLoginClicked: () -> Unit,
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
                text = "Ingresa tu PIN",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                text = "Introduce tu código de seguridad de 4\ndígitos para acceder a SafeGuard.",
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
            uiState.error?.let {
                Text(
                    text = "Ingresa un PIN de 4 dígitos.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = AppSpacing.sm)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9), listOf(0)).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { digit ->
                        TextButton(
                            onClick = { onDigitClicked(digit) },
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("login_digit_$digit"),
                            shape = CircleShape
                        ) {
                            Text(
                                text = digit.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
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
            TextButton(
                onClick = onLoginClicked,
                enabled = uiState.canLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_primary_action")
            ) {
                Text(
                    text = "Iniciar sesión  →",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    AppemergencyTheme {
        LoginScreen(LoginUiState(), {}, {}, {})
    }
}
