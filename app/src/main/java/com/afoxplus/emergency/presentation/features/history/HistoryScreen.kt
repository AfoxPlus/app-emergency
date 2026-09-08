package com.afoxplus.emergency.presentation.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afoxplus.emergency.R
import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.domain.model.NotifiedContact
import com.afoxplus.emergency.presentation.navigation.BottomNavTab
import com.afoxplus.emergency.presentation.navigation.EmergencyBottomNavigationBar
import com.afoxplus.emergency.presentation.ui.theme.AppShapes
import com.afoxplus.emergency.presentation.ui.theme.AppSpacing
import com.afoxplus.emergency.presentation.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.presentation.ui.theme.EmergencyColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Stateful entry point: wires the [HistoryViewModel] to the stateless [HistoryScreen].
 */
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onRefreshRequested()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HistoryScreen(
        uiState = uiState,
        onFilterSelected = viewModel::onFilterSelected,
        modifier = modifier,
        onBackClick = onBackClick,
        onNavigateToHome = onNavigateToHome,
        onNavigateToContacts = onNavigateToContacts,
        onNavigateToSettings = onNavigateToSettings
    )
}

/**
 * Stateless Alert History UI: filter tabs plus a chronological list of alert cards, matching
 * the reference design (type badge, status badge, date/time, description, location and
 * notified contacts).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onFilterSelected: (HistoryFilter) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.history_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("history_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            EmergencyBottomNavigationBar(
                selectedTab = BottomNavTab.HISTORY,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.HOME -> onNavigateToHome()
                        BottomNavTab.HISTORY -> Unit
                        BottomNavTab.CONTACTS -> onNavigateToContacts()
                        BottomNavTab.SETTINGS -> onNavigateToSettings()
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            FilterTabs(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = onFilterSelected
            )
            if (uiState.isEmpty) {
                EmptyHistoryContent()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("history_list"),
                    contentPadding = PaddingValues(AppSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    items(uiState.filteredEntries, key = { it.id }) { entry ->
                        AlertHistoryCard(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterTabs(
    selectedFilter: HistoryFilter,
    onFilterSelected: (HistoryFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        FilterTab(
            label = stringResource(R.string.history_filter_all),
            selected = selectedFilter == HistoryFilter.ALL,
            tag = "history_filter_all",
            onClick = { onFilterSelected(HistoryFilter.ALL) }
        )
        FilterTab(
            label = stringResource(R.string.history_filter_sos),
            selected = selectedFilter == HistoryFilter.SOS_BUTTON,
            tag = "history_filter_sos",
            onClick = { onFilterSelected(HistoryFilter.SOS_BUTTON) }
        )
        FilterTab(
            label = stringResource(R.string.history_filter_quick),
            selected = selectedFilter == HistoryFilter.QUICK_ALERT,
            tag = "history_filter_quick",
            onClick = { onFilterSelected(HistoryFilter.QUICK_ALERT) }
        )
        FilterTab(
            label = stringResource(R.string.history_filter_periodic),
            selected = selectedFilter == HistoryFilter.PERIODIC_CHECK,
            tag = "history_filter_periodic",
            onClick = { onFilterSelected(HistoryFilter.PERIODIC_CHECK) }
        )
    }
}

@Composable
private fun FilterTab(
    label: String,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(AppShapes.extraLarge)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .testTag(tag)
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun EmptyHistoryContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.xxl)
            .testTag("history_empty_state"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(AppSpacing.md))
        Text(
            text = stringResource(R.string.history_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(AppSpacing.xs))
        Text(
            text = stringResource(R.string.history_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AlertHistoryCard(entry: AlertHistoryEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .border(1.dp, MaterialTheme.colorScheme.outline, AppShapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .padding(AppSpacing.lg)
            .testTag("history_card_${entry.id}"),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TypeBadge(entry.type)
            StatusBadge(entry.status)
        }
        Text(
            text = formatEntryDateTime(entry.timestampMillis),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = entry.description,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        entry.coordinates?.let { coordinates ->
            LocationBlock(coordinates)
        }
        if (entry.notifiedContacts.isNotEmpty()) {
            NotifiedContactsSection(entry.notifiedContacts)
        }
    }
}

@Composable
private fun TypeBadge(type: AlertType) {
    val (label, icon) = when (type) {
        AlertType.SOS_BUTTON -> stringResource(R.string.history_filter_sos) to Icons.Default.PanTool
        AlertType.QUICK_ALERT -> stringResource(R.string.history_filter_quick) to Icons.Default.Bolt
        AlertType.PERIODIC_CHECK -> stringResource(R.string.history_filter_periodic) to Icons.Default.Schedule
    }
    val containerColor = when (type) {
        AlertType.PERIODIC_CHECK -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when (type) {
        AlertType.PERIODIC_CHECK -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }
    Badge(label = label, icon = icon, containerColor = containerColor, contentColor = contentColor)
}

@Composable
private fun StatusBadge(status: AlertStatus) {
    val label = when (status) {
        AlertStatus.ISSUED -> stringResource(R.string.history_status_issued)
        AlertStatus.CANCELLED -> stringResource(R.string.history_status_cancelled)
    }
    val icon = when (status) {
        AlertStatus.ISSUED -> Icons.Default.CheckCircle
        AlertStatus.CANCELLED -> Icons.Default.Cancel
    }
    val containerColor = when (status) {
        AlertStatus.ISSUED -> MaterialTheme.colorScheme.secondaryContainer
        AlertStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (status) {
        AlertStatus.ISSUED -> MaterialTheme.colorScheme.onSecondaryContainer
        AlertStatus.CANCELLED -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Badge(label = label, icon = icon, containerColor = containerColor, contentColor = contentColor)
}

@Composable
private fun Badge(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
) {
    Row(
        modifier = Modifier
            .clip(AppShapes.extraLarge)
            .background(containerColor)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun LocationBlock(coordinates: Coordinates) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(AppSpacing.md)
            .testTag("history_location_block"),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column {
            val accuracy = coordinates.accuracyMeters?.toInt()
            val coordinatesText = "%.4f, %.4f".format(coordinates.latitude, coordinates.longitude)
            Text(
                text = if (accuracy != null) {
                    "$coordinatesText (Precisión ±${accuracy}m)"
                } else {
                    coordinatesText
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NotifiedContactsSection(contacts: List<NotifiedContact>) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Text(
            text = stringResource(
                R.string.history_contacts_notified_sms,
                contacts.size,
                contacts.size
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            contacts.forEach { contact ->
                ContactAvatar(contact.name)
            }
            Spacer(Modifier.width(AppSpacing.xs))
            Text(
                text = contacts.joinToString { contact -> "${contact.name} (${contact.relationship})" },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ContactAvatar(name: String) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(EmergencyColors.SecondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = EmergencyColors.OnSecondaryContainer,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

private fun formatEntryDateTime(timestampMillis: Long): String {
    val entryCalendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale("es", "ES"))
    val datePart = dateFormat.format(Date(timestampMillis))
    val timePart = timeFormat.format(Date(timestampMillis))

    val prefix = when {
        isSameDay(entryCalendar, today) -> "Hoy, "
        isSameDay(entryCalendar, yesterday) -> "Ayer, "
        else -> ""
    }

    return "$prefix$datePart • $timePart hrs"
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    AppemergencyTheme {
        HistoryScreen(
            uiState = HistoryUiState(
                entries = listOf(
                    AlertHistoryEntry(
                        id = "1",
                        type = AlertType.SOS_BUTTON,
                        status = AlertStatus.ISSUED,
                        timestampMillis = System.currentTimeMillis(),
                        description = "Pulsación sostenida de botón de auxilio en pantalla principal",
                        coordinates = Coordinates(
                            latitude = -12.0964,
                            longitude = -77.0345,
                            accuracyMeters = 4f
                        ),
                        notifiedContacts = listOf(NotifiedContact("Carlos Mendoza", "Hermano"))
                    ),
                    AlertHistoryEntry(
                        id = "2",
                        type = AlertType.QUICK_ALERT,
                        status = AlertStatus.CANCELLED,
                        timestampMillis = System.currentTimeMillis() - 86_400_000,
                        description = "3 pulsaciones consecutivas del botón de encendido",
                        coordinates = Coordinates(
                            latitude = -12.1215,
                            longitude = -77.0298,
                            accuracyMeters = 6f
                        ),
                        notifiedContacts = listOf(NotifiedContact("Carlos Mendoza", "Hermano"))
                    )
                )
            ),
            onFilterSelected = {}
        )
    }
}
