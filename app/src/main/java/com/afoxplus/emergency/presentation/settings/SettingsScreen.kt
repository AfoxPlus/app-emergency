package com.afoxplus.emergency.presentation.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afoxplus.emergency.R
import com.afoxplus.emergency.data.repository.SettingsPreferencesImpl
import com.afoxplus.emergency.presentation.navigation.BottomNavTab
import com.afoxplus.emergency.presentation.navigation.EmergencyBottomNavigationBar
import com.afoxplus.emergency.ui.theme.AppShapes
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyColors
import com.afoxplus.emergency.ui.theme.EmergencyTextField

/**
 * Returns the runtime permission(s) tied to [this] permission type, so it can be checked and
 * requested with the real Android permission APIs. [SettingsPermissionType.NOTIFICATIONS] only
 * requires a runtime grant from API 33 onwards; below that it is considered always granted.
 */
private fun SettingsPermissionType.runtimePermissions(): List<String> = when (this) {
    SettingsPermissionType.LOCATION -> listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    SettingsPermissionType.CONTACTS -> listOf(Manifest.permission.READ_CONTACTS)
    SettingsPermissionType.NOTIFICATIONS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyList()
    }
    SettingsPermissionType.CAMERA_MICROPHONE -> listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
}

private fun isPermissionTypeGranted(context: android.content.Context, type: SettingsPermissionType): Boolean {
    val permissions = type.runtimePermissions()
    if (permissions.isEmpty()) return true
    return permissions.all {
        context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Stateful entry point: wires the [SettingsViewModel] and the 4 System Permissions runtime
 * requests to the stateless [SettingsScreen].
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {}
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onPermissionsChecked(
            SettingsPermissionType.entries.associateWith { isPermissionTypeGranted(context, it) }
        )
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onPermissionResult(SettingsPermissionType.LOCATION, granted) }

    val contactsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onPermissionResult(SettingsPermissionType.CONTACTS, granted) }

    val notificationsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onPermissionResult(SettingsPermissionType.NOTIFICATIONS, granted) }

    val cameraMicrophoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.onPermissionResult(
            SettingsPermissionType.CAMERA_MICROPHONE,
            results.values.all { it }
        )
    }

    SettingsScreen(
        uiState = uiState,
        onNavigateToHome = onNavigateToHome,
        onNavigateToContacts = onNavigateToContacts,
        onEditMessageClicked = viewModel::onEditMessageClicked,
        onDraftMessageChanged = viewModel::onDraftMessageChanged,
        onDismissMessageSheet = viewModel::onDismissMessageSheet,
        onSaveMessageClicked = viewModel::onSaveMessageClicked,
        onPermissionClicked = { type ->
            when (type) {
                SettingsPermissionType.LOCATION -> locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                SettingsPermissionType.CONTACTS -> contactsLauncher.launch(Manifest.permission.READ_CONTACTS)
                SettingsPermissionType.NOTIFICATIONS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    Unit
                }
                SettingsPermissionType.CAMERA_MICROPHONE -> cameraMicrophoneLauncher.launch(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Stateless Settings UI: User Profile, SOS Emergency Message with SMS preview and the 4
 * System Permissions (Settings AC02-AC05), independent from the ViewModel so it can be
 * exercised directly from previews and UI tests.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    appVersion: String = "",
    onNavigateToHome: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onEditMessageClicked: () -> Unit = {},
    onDraftMessageChanged: (String) -> Unit = {},
    onDismissMessageSheet: () -> Unit = {},
    onSaveMessageClicked: () -> Unit = {},
    onPermissionClicked: (SettingsPermissionType) -> Unit = {},
    onLogoutClicked: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            EmergencyBottomNavigationBar(
                selectedTab = BottomNavTab.SETTINGS,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.HOME -> onNavigateToHome()
                        BottomNavTab.CONTACTS -> onNavigateToContacts()
                        BottomNavTab.SETTINGS -> Unit
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
            SettingsTopBar()
            ProfileSection(uiState)
            SosMessageSection(uiState, onEditMessageClicked)
            PermissionsSection(uiState, onPermissionClicked)
            Text(
                text = stringResource(R.string.settings_permissions_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LogoutButton(onLogoutClicked)
            Text(
                text = appVersion.ifBlank { stringResource(R.string.settings_app_version) },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    if (uiState.isMessageSheetVisible) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = onDismissMessageSheet,
            sheetState = sheetState,
            modifier = Modifier.testTag("settings_message_sheet")
        ) {
            EmergencyMessageSheetContent(
                uiState = uiState,
                onDraftMessageChanged = onDraftMessageChanged,
                onCloseClicked = onDismissMessageSheet,
                onSaveClicked = onSaveMessageClicked
            )
        }
    }
}

@Composable
private fun SettingsTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.settings_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ProfileSection(uiState: SettingsUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_profile_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "🛡 ${stringResource(R.string.settings_profile_protected)}",
                modifier = Modifier
                    .clip(AppShapes.extraSmall)
                    .background(EmergencyColors.SuccessContainer)
                    .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                color = EmergencyColors.Success,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.large)
                .background(MaterialTheme.colorScheme.surface)
                .padding(AppSpacing.lg)
                .testTag("settings_profile_card"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(EmergencyColors.Secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Column(modifier = Modifier.weight(1f).padding(start = AppSpacing.md)) {
                Text(
                    text = uiState.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("settings_profile_name")
                )
                Text(
                    text = uiState.phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag("settings_profile_phone")
                )
            }
        }
    }
}

@Composable
private fun SosMessageSection(uiState: SettingsUiState, onEditMessageClicked: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Text(
            text = stringResource(R.string.settings_sos_section_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.large)
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onEditMessageClicked)
                .padding(AppSpacing.lg)
                .testTag("settings_sos_message_card"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "✱",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(EmergencyColors.SecondaryContainer)
                    .padding(8.dp),
                color = EmergencyColors.Secondary,
                fontSize = 18.sp
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = AppSpacing.md)) {
                Text(stringResource(R.string.settings_sos_card_title), fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.settings_sos_card_description),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text("›", fontSize = 28.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        SmsPreviewCard(uiState)
    }
}

@Composable
private fun SmsPreviewCard(uiState: SettingsUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(AppSpacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_sms_preview_title),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    R.string.settings_sms_preview_counter,
                    uiState.smsCharacterCount,
                    SettingsUiState.SMS_MAX_STANDARD_LENGTH,
                    uiState.smsSegmentCount
                ),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("settings_sms_preview_counter")
            )
        }
        Spacer(Modifier.height(AppSpacing.xs))
        Text(
            text = "\"${uiState.sosMessage}\"",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("settings_sms_preview_message")
        )
        Spacer(Modifier.height(AppSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.settings_sms_gps_status),
                style = MaterialTheme.typography.bodySmall,
                color = EmergencyColors.Success
            )
            Text(
                stringResource(R.string.settings_sms_ssl_status),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PermissionsSection(
    uiState: SettingsUiState,
    onPermissionClicked: (SettingsPermissionType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_permissions_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    R.string.settings_permissions_counter,
                    uiState.grantedPermissionsCount,
                    uiState.totalPermissionsCount
                ),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("settings_permissions_counter")
            )
        }
        SettingsPermissionType.entries.forEach { type ->
            PermissionItem(
                type = type,
                granted = uiState.isPermissionGranted(type),
                onClick = { onPermissionClicked(type) }
            )
        }
    }
}

@Composable
private fun PermissionItem(
    type: SettingsPermissionType,
    granted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(AppSpacing.md)
            .testTag("settings_permission_${type.name}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(type.titleRes), fontWeight = FontWeight.Bold)
            Text(stringResource(type.descriptionRes), style = MaterialTheme.typography.bodySmall)
        }
        val statusLabel = if (granted) {
            stringResource(type.grantedLabelRes)
        } else {
            stringResource(R.string.settings_permission_allow)
        }
        val backgroundColor = if (granted) EmergencyColors.SuccessContainer else EmergencyColors.ErrorContainer
        val contentColor = if (granted) EmergencyColors.Success else EmergencyColors.Error
        Text(
            text = "${if (granted) "✓" else "⚠"} $statusLabel",
            modifier = Modifier
                .clip(AppShapes.extraSmall)
                .background(backgroundColor)
                .clickable(enabled = !granted, onClick = onClick)
                .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs)
                .testTag("settings_permission_action_${type.name}"),
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LogoutButton(onLogoutClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .background(EmergencyColors.ErrorContainer)
            .clickable(onClick = onLogoutClicked)
            .padding(AppSpacing.lg)
            .testTag("settings_logout_button"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("➜ ", color = EmergencyColors.Error, fontWeight = FontWeight.Bold)
        Text(
            text = stringResource(R.string.settings_logout_action),
            color = EmergencyColors.Error,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmergencyMessageSheetContent(
    uiState: SettingsUiState,
    onDraftMessageChanged: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSaveClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_sheet_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.settings_sheet_subtitle),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onCloseClicked, modifier = Modifier.testTag("settings_sheet_close")) {
                Text("✕", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_sheet_content_label),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "✓ ${stringResource(R.string.settings_sheet_secure_badge)}",
                modifier = Modifier
                    .clip(AppShapes.extraSmall)
                    .background(EmergencyColors.SuccessContainer)
                    .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                color = EmergencyColors.Success,
                style = MaterialTheme.typography.labelMedium
            )
        }
        EmergencyTextField(
            value = uiState.draftMessage,
            onValueChange = onDraftMessageChanged,
            label = stringResource(R.string.settings_sheet_content_label),
            singleLine = false,
            modifier = Modifier.testTag("settings_sheet_message_field")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(
                    R.string.settings_sheet_counter,
                    uiState.draftCharacterCount,
                    SettingsUiState.SMS_MAX_STANDARD_LENGTH
                ),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("settings_sheet_counter")
            )
            val segmentLabel = if (uiState.draftSegmentCount <= 1) {
                stringResource(R.string.settings_sheet_segment_standard)
            } else {
                stringResource(R.string.settings_sheet_segment_multiple, uiState.draftSegmentCount)
            }
            Text(
                text = segmentLabel,
                modifier = Modifier
                    .clip(AppShapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs)
                    .testTag("settings_sheet_segment"),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            text = stringResource(R.string.settings_sheet_variables_title),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            VariableChip(stringResource(R.string.settings_sheet_variable_gps))
            VariableChip(stringResource(R.string.settings_sheet_variable_battery))
        }
        EmergencyButton(
            text = "✓  ${stringResource(R.string.settings_sheet_save_action)}",
            onClick = onSaveClicked,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_sheet_save_button")
        )
    }
}

@Composable
private fun VariableChip(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .clip(AppShapes.extraSmall)
            .background(EmergencyColors.SecondaryContainer)
            .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
        color = EmergencyColors.Secondary,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    AppemergencyTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                name = "María González",
                phoneNumber = "+51 987 654 321",
                sosMessage = SettingsPreferencesImpl.DEFAULT_SOS_MESSAGE,
                permissions = mapOf(
                    SettingsPermissionType.LOCATION to true,
                    SettingsPermissionType.CONTACTS to true,
                    SettingsPermissionType.NOTIFICATIONS to true,
                    SettingsPermissionType.CAMERA_MICROPHONE to false
                )
            ),
            appVersion = "SafeGuard v2.4.1 • Sistema de Auxilio Conectado"
        )
    }
}
