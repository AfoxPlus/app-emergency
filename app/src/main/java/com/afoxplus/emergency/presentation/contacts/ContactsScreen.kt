package com.afoxplus.emergency.presentation.contacts

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.EmergencyContact
import com.afoxplus.emergency.domain.model.EmergencyContactType
import com.afoxplus.emergency.presentation.navigation.BottomNavTab
import com.afoxplus.emergency.presentation.navigation.EmergencyBottomNavigationBar
import com.afoxplus.emergency.ui.theme.AppShapes
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyColors
import com.afoxplus.emergency.ui.theme.EmergencyTextField

/**
 * Stateful entry point: wires the [ContactsViewModel], the `READ_CONTACTS` runtime permission
 * request and the stateless [ContactsScreen] together.
 */
@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val viewModel: ContactsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Tracks whether a runtime permission request was already made in this app installation,
    // so the very first denial is never mistaken for a permanent one: `shouldShowRequestPermissionRationale`
    // returns `false` both before any request has ever been made and once the permission is
    // permanently denied.
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val activity = context as? Activity
        val permanentlyDenied = !granted && hasRequestedPermission && activity != null &&
            !activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)
        hasRequestedPermission = true
        viewModel.onPermissionResult(granted, permanentlyDenied)
    }

    LaunchedEffect(Unit) {
        val granted = context.checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
        viewModel.onPermissionResult(granted)
    }

    ContactsScreen(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onAddEmergencyContact = viewModel::onAddEmergencyContact,
        onRemoveEmergencyContact = viewModel::onRemoveEmergencyContact,
        onRequestPermission = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
        onOpenAppSettings = {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", context.packageName, null))
            )
        },
        onNavigateToHome = onNavigateToHome,
        onNavigateToSettings = onNavigateToSettings,
        modifier = modifier
    )
}

/**
 * Stateless Contacts UI: Trust Network card, Emergency Contacts section and Phone Contacts
 * section with search, plus the loading/permission/empty states described in the design.
 */
@Composable
fun ContactsScreen(
    uiState: ContactsUiState,
    onSearchQueryChanged: (String) -> Unit,
    onAddEmergencyContact: (Contact) -> Unit,
    onRemoveEmergencyContact: (String) -> Unit,
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            EmergencyBottomNavigationBar(
                selectedTab = BottomNavTab.CONTACTS,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.HOME -> onNavigateToHome()
                        BottomNavTab.CONTACTS -> Unit
                        BottomNavTab.SETTINGS -> onNavigateToSettings()
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.permissionState == ContactsPermissionState.DENIED ||
                uiState.permissionState == ContactsPermissionState.PERMANENTLY_DENIED -> {
                PermissionRequiredContent(
                    modifier = Modifier.padding(padding),
                    permanentlyDenied = uiState.permissionState == ContactsPermissionState.PERMANENTLY_DENIED,
                    onRequestPermission = onRequestPermission,
                    onOpenAppSettings = onOpenAppSettings
                )
            }

            uiState.isLoading -> LoadingContent(modifier = Modifier.padding(padding))

            else -> ContactsContent(
                modifier = Modifier.padding(padding),
                uiState = uiState,
                onSearchQueryChanged = onSearchQueryChanged,
                onAddEmergencyContact = onAddEmergencyContact,
                onRemoveEmergencyContact = onRemoveEmergencyContact
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("contacts_loading"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = EmergencyColors.Brand)
        Spacer(Modifier.height(AppSpacing.md))
        Text(stringResource(R.string.contacts_loading), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PermissionRequiredContent(
    permanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.xl)
            .testTag("contacts_permission_required"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.contacts_permission_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(AppSpacing.xl))
        EmergencyButton(
            text = stringResource(
                if (permanentlyDenied) {
                    R.string.contacts_permission_open_settings
                } else {
                    R.string.contacts_permission_action
                }
            ),
            onClick = if (permanentlyDenied) onOpenAppSettings else onRequestPermission,
            modifier = Modifier.testTag("contacts_permission_action")
        )
    }
}

@Composable
private fun ContactsContent(
    uiState: ContactsUiState,
    onSearchQueryChanged: (String) -> Unit,
    onAddEmergencyContact: (Contact) -> Unit,
    onRemoveEmergencyContact: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        TrustNetworkCard()

        if (uiState.emergencyContacts.isNotEmpty()) {
            EmergencyContactsSection(
                emergencyContacts = uiState.emergencyContacts,
                onRemoveEmergencyContact = onRemoveEmergencyContact
            )
        }

        PhoneContactsSection(
            uiState = uiState,
            onSearchQueryChanged = onSearchQueryChanged,
            onAddEmergencyContact = onAddEmergencyContact
        )
    }
}

@Composable
private fun TrustNetworkCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .background(EmergencyColors.SuccessContainer)
            .padding(AppSpacing.lg)
    ) {
        Text(
            text = stringResource(R.string.contacts_trust_network_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(AppSpacing.xs))
        Text(
            text = stringResource(R.string.contacts_trust_network_description),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmergencyContactsSection(
    emergencyContacts: List<EmergencyContact>,
    onRemoveEmergencyContact: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        Text(
            text = stringResource(R.string.contacts_emergency_section_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        emergencyContacts.forEach { emergencyContact ->
            EmergencyContactItem(
                emergencyContact = emergencyContact,
                onRemove = { onRemoveEmergencyContact(emergencyContact.contactId) }
            )
        }
    }
}

@Composable
private fun EmergencyContactItem(
    emergencyContact: EmergencyContact,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(AppSpacing.md)
            .testTag("emergency_contact_${emergencyContact.contactId}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ContactAvatar(name = emergencyContact.name)
            Column(modifier = Modifier.weight(1f).padding(start = AppSpacing.md)) {
                Text(emergencyContact.name, fontWeight = FontWeight.Bold)
                Text(emergencyContact.phoneNumber, style = MaterialTheme.typography.bodySmall)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val roleLabel = if (emergencyContact.type == EmergencyContactType.PRIMARY) {
                stringResource(R.string.contacts_primary_label)
            } else {
                stringResource(R.string.contacts_backup_label)
            }
            Text(
                text = roleLabel,
                modifier = Modifier
                    .weight(1f)
                    .clip(AppShapes.extraSmall)
                    .background(
                        if (emergencyContact.type == EmergencyContactType.PRIMARY) {
                            EmergencyColors.BrandContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                    .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.testTag("delete_emergency_contact_${emergencyContact.contactId}")
            ) {
                Text(
                    text = "🗑",
                    fontSize = 20.sp,
                    color = EmergencyColors.Brand
                )
            }
        }
    }
}

@Composable
private fun PhoneContactsSection(
    uiState: ContactsUiState,
    onSearchQueryChanged: (String) -> Unit,
    onAddEmergencyContact: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.contacts_phone_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.contacts_count, uiState.totalContactsCount),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("contacts_total_count")
            )
        }

        EmergencyTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChanged,
            label = stringResource(R.string.contacts_search_placeholder),
            modifier = Modifier.testTag("contacts_search_field")
        )

        val displayedContacts = uiState.displayedContacts
        when {
            uiState.phoneContacts.isEmpty() -> Text(
                text = stringResource(R.string.contacts_empty),
                modifier = Modifier.testTag("contacts_empty_state"),
                style = MaterialTheme.typography.bodyMedium
            )

            uiState.isSearching && displayedContacts.isEmpty() -> Text(
                text = stringResource(R.string.contacts_no_search_results),
                modifier = Modifier.testTag("contacts_no_search_results"),
                style = MaterialTheme.typography.bodyMedium
            )

            else -> Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                displayedContacts.forEach { contact ->
                    PhoneContactItem(
                        contact = contact,
                        isEmergencyContact = uiState.isEmergencyContact(contact.id),
                        onAddEmergencyContact = { onAddEmergencyContact(contact) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneContactItem(
    contact: Contact,
    isEmergencyContact: Boolean,
    onAddEmergencyContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(AppSpacing.md)
            .testTag("phone_contact_${contact.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(name = contact.name)
        Column(modifier = Modifier.weight(1f).padding(start = AppSpacing.md)) {
            Text(contact.name, fontWeight = FontWeight.Bold)
            Text(contact.phoneNumber, style = MaterialTheme.typography.bodySmall)
        }
        if (isEmergencyContact) {
            Text(
                text = stringResource(R.string.contacts_added_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            EmergencyButton(
                text = stringResource(R.string.contacts_add_action),
                onClick = onAddEmergencyContact,
                modifier = Modifier.testTag("add_emergency_contact_${contact.id}")
            )
        }
    }
}

@Composable
private fun ContactAvatar(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactsScreenPreview() {
    AppemergencyTheme {
        ContactsScreen(
            uiState = ContactsUiState(
                permissionState = ContactsPermissionState.GRANTED,
                phoneContacts = listOf(
                    Contact("1", "Elena Martínez", "+34 600 111 222"),
                    Contact("2", "Javier Ruiz", "+34 655 444 333"),
                    Contact("3", "Sofía García", "+34 677 888 999")
                ),
                emergencyContacts = listOf(
                    EmergencyContact("4", "Carmen Rodríguez", "+34 612 345 678", EmergencyContactType.PRIMARY)
                )
            ),
            onSearchQueryChanged = {},
            onAddEmergencyContact = {},
            onRemoveEmergencyContact = {},
            onRequestPermission = {},
            onOpenAppSettings = {}
        )
    }
}
