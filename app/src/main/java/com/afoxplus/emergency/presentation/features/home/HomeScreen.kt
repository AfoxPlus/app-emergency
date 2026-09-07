package com.afoxplus.emergency.presentation.features.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afoxplus.emergency.presentation.navigation.BottomNavTab
import com.afoxplus.emergency.presentation.navigation.EmergencyBottomNavigationBar
import com.afoxplus.emergency.presentation.ui.theme.AppShapes
import com.afoxplus.emergency.presentation.ui.theme.AppSpacing
import com.afoxplus.emergency.presentation.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.presentation.ui.theme.EmergencyColors

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAlertClick: () -> Unit = {},
    onPeriodicCheckClick: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val hasContacts = context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                val hasLocation = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                viewModel.onResume(hasContactsPermission = hasContacts, hasLocationPermission = hasLocation)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HomeScreenContent(
        uiState = uiState,
        modifier = modifier,
        onAlertClick = onAlertClick,
        onPeriodicCheckClick = onPeriodicCheckClick,
        onNavigateToContacts = onNavigateToContacts,
        onNavigateToSettings = onNavigateToSettings,
        onQuickAlertToggle = { enabled -> viewModel.onQuickAlertToggled(enabled) },
        onSnackbarShown = { viewModel.onSnackbarShown() }
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onAlertClick: () -> Unit = {},
    onPeriodicCheckClick: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onQuickAlertToggle: (Boolean) -> Unit = {},
    onSnackbarShown: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    val callPhoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            placeEmergencyCall(context)
        } else {
            showPermissionDeniedDialog = true
        }
    }

    val onCallEmergencyClick = {
        if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            placeEmergencyCall(context)
        } else {
            callPhoneLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onSnackbarShown()
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Estado de Seguridad", fontWeight = FontWeight.Bold) },
            text = {
                if (uiState.isActive) {
                    Text("Tu sistema de seguridad está activo. Todos los permisos y configuraciones están concedidos correctamente.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                        Text("El sistema está inactivo. Requisitos faltantes para estar activo:", fontWeight = FontWeight.SemiBold)
                        uiState.missingRequirements.forEach { req ->
                            Text("• $req", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text("Permiso Requerido", fontWeight = FontWeight.Bold) },
            text = {
                Text("Para realizar llamadas directas de emergencia se requiere el permiso de llamadas. Puedes activarlo en la configuración de la aplicación.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDeniedDialog = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }) {
                    Text("Abrir Ajustes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDeniedDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            HomeTopBar(
                uiState = uiState,
                onStatusChipClick = { showStatusDialog = true }
            )
        },
        bottomBar = {
            EmergencyBottomNavigationBar(
                selectedTab = BottomNavTab.HOME,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.HOME -> Unit
                        BottomNavTab.CONTACTS -> onNavigateToContacts()
                        BottomNavTab.SETTINGS -> onNavigateToSettings()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
        ) {
            ImmediateActionCard(
                onAlertClick = onAlertClick,
                onCallEmergencyClick = onCallEmergencyClick
            )
            ProtectionSummary(uiState = uiState)
            Text(
                text = "PROTECCIONES CONFIGURADAS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            ProtectionSetting(
                icon = Icons.Default.FlashOn,
                title = "Alerta rápida",
                description = "3 pulsaciones del botón de encendido",
                tag = "home_quick_alert",
                checked = uiState.isQuickAlertEnabled,
                onCheckedChange = onQuickAlertToggle,
                onClick = { onQuickAlertToggle(!uiState.isQuickAlertEnabled) }
            )
            ProtectionSetting(
                icon = Icons.Default.EventRepeat,
                title = "Comprobación periódica",
                description = "Confirmación de bienestar por notificación",
                tag = "home_periodic_check",
                checked = uiState.isPeriodicCheckEnabled,
                onCheckedChange = { onPeriodicCheckClick() },
                onClick = onPeriodicCheckClick
            )
        }
        }
    )
}

private fun placeEmergencyCall(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:911"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
            dialIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(dialIntent)
        } catch (_: Exception) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    uiState: HomeUiState,
    onStatusChipClick: () -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .shadow(4.dp),
        title = {
            Column {
            Text(
                "Hola, ${uiState.displayName}",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp)
            )
            Text("Tu seguridad está monitorizada", style = MaterialTheme.typography.bodyMedium)
            }
        },
        actions = {
        val chipContainerColor = if (uiState.isActive) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.errorContainer
        }
        val chipContentColor = if (uiState.isActive) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onErrorContainer
        }
        val chipText = if (uiState.isActive) "ACTIVO" else "INACTIVO"
        Row(
            modifier = Modifier
                .padding(end = AppSpacing.md)
                .clip(RoundedCornerShape(20.dp))
                .background(chipContainerColor)
                .clickable(onClick = onStatusChipClick)
                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
                .testTag("home_status_chip"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (uiState.isActive) Icons.Default.Check else Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = chipContentColor
            )
            Spacer(Modifier.width(AppSpacing.xs))
            Text(
                chipText,
                color = chipContentColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ImmediateActionCard(
    onAlertClick: () -> Unit,
    onCallEmergencyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .25f), AppShapes.large)
            .padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ACCIÓN INMEDIATA", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        Text(
            "Mantén presionado para enviar SOS con ubicación",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(AppSpacing.lg))
        AlertButton(onAlertClick)
        Spacer(Modifier.height(AppSpacing.lg))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable(onClick = onCallEmergencyClick)
                .padding(AppSpacing.md)
                .testTag("home_call_emergency_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Call,
                contentDescription = "Llamar a Emergencias",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(10.dp), tint = MaterialTheme.colorScheme.error
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = AppSpacing.md)) {
                Text("Llamar a Emergencias", fontWeight = FontWeight.Bold)
                Text("Marcación rápida (112 / 911)", style = MaterialTheme.typography.bodySmall)
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AlertButton(onAlertClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(EmergencyColors.Brand)
            .border(5.dp, MaterialTheme.colorScheme.errorContainer, CircleShape)
            .clickable(onClick = onAlertClick)
            .testTag("home_alert_button"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SOS",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text("PULSAR", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProtectionSummary(uiState: HomeUiState) {
    val activeCount = listOf(uiState.isQuickAlertEnabled, uiState.isPeriodicCheckEnabled).count { it }
    val descriptionText = if (uiState.isActive) {
        "$activeCount mecanismos automáticos habilitados"
    } else {
        "Configura tus permisos y alertas"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Shield, contentDescription = null, modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .padding(10.dp), tint = Color.White
        )
        Column(modifier = Modifier.padding(start = AppSpacing.md)) {
            Text(
                "Protección en tiempo real",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                descriptionText,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProtectionSetting(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    tag: String,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(AppSpacing.md)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon, contentDescription = title, modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp), tint = Color.White
        )
        Column(modifier = Modifier
            .weight(1f)
            .padding(horizontal = AppSpacing.md)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppemergencyTheme {
        HomeScreenContent(uiState = HomeUiState(userName = "Valentin", isQuickAlertEnabled = true))
    }
}