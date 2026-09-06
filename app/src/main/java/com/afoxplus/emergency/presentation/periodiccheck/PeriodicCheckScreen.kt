package com.afoxplus.emergency.presentation.periodiccheck

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afoxplus.emergency.domain.model.FrequencyOption
import com.afoxplus.emergency.domain.model.ResponseTimeOption
import com.afoxplus.emergency.ui.theme.AppShapes
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyColors
import com.afoxplus.emergency.ui.theme.EmergencySecondaryButton

/**
 * Stateful entry point: wires the [PeriodicCheckViewModel] to the stateless [PeriodicCheckScreen].
 */
@Composable
fun PeriodicCheckScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToEmergencyContacts: () -> Unit = {},
    onActivated: () -> Unit = {},
    viewModel: PeriodicCheckViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PeriodicCheckScreen(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onFrequencyOptionSelected = viewModel::onFrequencyOptionSelected,
        onCustomFrequencyIncrement = viewModel::onCustomFrequencyIncrement,
        onCustomFrequencyDecrement = viewModel::onCustomFrequencyDecrement,
        onResponseTimeOptionSelected = viewModel::onResponseTimeOptionSelected,
        onEmergencyContactsClick = onNavigateToEmergencyContacts,
        onActivateClicked = {
            viewModel.onActivateClicked()
            onActivated()
        }
    )
}

/**
 * Stateless Periodic Safety Check UI (AC01-AC17), independent from the ViewModel so it
 * can be exercised directly from previews and UI tests.
 */
@Composable
fun PeriodicCheckScreen(
    modifier: Modifier = Modifier,
    uiState: PeriodicCheckUiState = PeriodicCheckUiState(),
    onBackClick: () -> Unit = {},
    onFrequencyOptionSelected: (FrequencyOption) -> Unit = {},
    onCustomFrequencyIncrement: () -> Unit = {},
    onCustomFrequencyDecrement: () -> Unit = {},
    onResponseTimeOptionSelected: (ResponseTimeOption) -> Unit = {},
    onEmergencyContactsClick: () -> Unit = {},
    onActivateClicked: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { PeriodicCheckTopBar(onBackClick) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(AppSpacing.lg)
            ) {
                EmergencyButton(
                    text = "✓  Activar comprobación",
                    onClick = onActivateClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("periodic_check_activate_button"),
                    enabled = uiState.canActivate
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
        ) {
            item { InformationCard() }
            item {
                FrequencySection(
                    uiState = uiState,
                    onFrequencyOptionSelected = onFrequencyOptionSelected,
                    onCustomFrequencyIncrement = onCustomFrequencyIncrement,
                    onCustomFrequencyDecrement = onCustomFrequencyDecrement
                )
            }
            item { ResponseTimeSection(uiState, onResponseTimeOptionSelected) }
            item { EmergencyContactsCard(uiState, onEmergencyContactsClick) }
            item { Spacer(Modifier.height(AppSpacing.md)) }
        }
    }
}

@Composable
private fun PeriodicCheckTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .testTag("periodic_check_back")
                .semantics { contentDescription = "Volver" }
        ) {
            Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(AppSpacing.sm))
        Text(
            text = "Comprobación",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InformationCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(EmergencyColors.SecondaryContainer)
            .padding(AppSpacing.lg)
    ) {
        Text(
            "🛡",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(EmergencyColors.Secondary)
                .padding(8.dp),
            fontSize = 16.sp
        )
        Column(modifier = Modifier.padding(start = AppSpacing.md)) {
            Text(
                "Supervisión periódica activa",
                color = EmergencyColors.Secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(AppSpacing.xs))
            Text(
                "SafeGuard te enviará una notificación periódica. Si no confirmas que " +
                    "estás bien dentro del tiempo límite, tus contactos de emergencia " +
                    "recibirán tu ubicación y una alerta automática.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun FrequencySection(
    uiState: PeriodicCheckUiState,
    onFrequencyOptionSelected: (FrequencyOption) -> Unit,
    onCustomFrequencyIncrement: () -> Unit,
    onCustomFrequencyDecrement: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Text(
            "¿Con qué frecuencia te consultamos?",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Elige el intervalo de verificación recurrente",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(AppSpacing.xs))

        val options = FrequencyOption.entries.toList()
        options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                rowOptions.forEach { option ->
                    FrequencyOptionCard(
                        option = option,
                        isSelected = uiState.selectedFrequencyOption == option,
                        onClick = { onFrequencyOptionSelected(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ajuste personalizado:", modifier = Modifier.weight(1f))
            CustomFrequencyStepButton(
                text = "-",
                tag = "periodic_check_custom_decrement",
                onClick = onCustomFrequencyDecrement
            )
            Text(
                text = "${uiState.frequencyMinutes} min",
                modifier = Modifier
                    .padding(horizontal = AppSpacing.md)
                    .testTag("periodic_check_custom_value"),
                fontWeight = FontWeight.Bold
            )
            CustomFrequencyStepButton(
                text = "+",
                tag = "periodic_check_custom_increment",
                onClick = onCustomFrequencyIncrement
            )
        }
    }
}

@Composable
private fun CustomFrequencyStepButton(text: String, tag: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FrequencyOptionCard(
    option: FrequencyOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) EmergencyColors.Secondary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) EmergencyColors.OnSecondary else MaterialTheme.colorScheme.onSurface
    Column(
        modifier = modifier
            .clip(AppShapes.medium)
            .background(backgroundColor)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isSelected) 0f else .3f), AppShapes.medium)
            .clickable(onClick = onClick)
            .padding(AppSpacing.md)
            .testTag("periodic_check_frequency_option_${option.minutes}"),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        if (option.isRecommended) {
            Text(
                "RECOMENDADA",
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Text(option.label, color = contentColor, fontWeight = FontWeight.Bold)
        Text(option.description, color = contentColor, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ResponseTimeSection(
    uiState: PeriodicCheckUiState,
    onResponseTimeOptionSelected: (ResponseTimeOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Text(
            "¿Cuánto tiempo tienes para responder?",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Margen antes de disparar la alerta automática",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(AppSpacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            ResponseTimeOption.entries.forEach { option ->
                ResponseTimeOptionCard(
                    option = option,
                    isSelected = uiState.selectedResponseTimeOption == option,
                    onClick = { onResponseTimeOptionSelected(option) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(AppSpacing.xs))
        Text(
            "Se activará una cuenta regresiva con alarma sonora progresiva y vibración " +
                "antes de notificar a tus contactos.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ResponseTimeOptionCard(
    option: ResponseTimeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) EmergencyColors.Secondary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) EmergencyColors.OnSecondary else MaterialTheme.colorScheme.onSurface
    Column(
        modifier = modifier
            .clip(AppShapes.medium)
            .background(backgroundColor)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isSelected) 0f else .3f), AppShapes.medium)
            .clickable(onClick = onClick)
            .padding(AppSpacing.sm)
            .testTag("periodic_check_response_option_${option.minutes}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Text(option.label, color = contentColor, fontWeight = FontWeight.Bold)
        Text(option.description, color = contentColor, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun EmergencyContactsCard(
    uiState: PeriodicCheckUiState,
    onEmergencyContactsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onEmergencyContactsClick)
            .padding(AppSpacing.lg)
            .testTag("periodic_check_contacts_card"),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Contactos de emergencia", fontWeight = FontWeight.Bold)
                if (uiState.hasEmergencyContacts) {
                    Text(
                        "${uiState.emergencyContactsCount} contactos asignados recibirán tu alerta",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        "No hay contactos de emergencia configurados.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text("›", fontSize = 28.sp)
        }
        if (!uiState.hasEmergencyContacts) {
            EmergencySecondaryButton(
                text = "Agregar contacto de emergencia",
                onClick = onEmergencyContactsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("periodic_check_add_contact_button")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PeriodicCheckScreenPreview() {
    AppemergencyTheme {
        PeriodicCheckScreen(uiState = PeriodicCheckUiState(emergencyContactsCount = 3))
    }
}
