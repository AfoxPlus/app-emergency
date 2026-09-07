package com.afoxplus.emergency.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.afoxplus.emergency.R

/**
 * Main sections reachable from the application's bottom [EmergencyBottomNavigationBar].
 */
enum class BottomNavTab {
    HOME,
    CONTACTS,
    SETTINGS
}

/**
 * Bottom navigation bar shared by every top-level screen (Home, Contacts, Settings), so the
 * currently selected section is always highlighted (AC01).
 */
@Composable
fun EmergencyBottomNavigationBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        BottomNavigationItem(
            icon = Icons.Default.Home,
            label = stringResource(R.string.nav_home),
            selected = selectedTab == BottomNavTab.HOME,
            tag = "nav_home",
            onClick = { onTabSelected(BottomNavTab.HOME) }
        )
        BottomNavigationItem(
            icon = Icons.Default.Contacts,
            label = stringResource(R.string.nav_contacts),
            selected = selectedTab == BottomNavTab.CONTACTS,
            tag = "nav_contacts",
            onClick = { onTabSelected(BottomNavTab.CONTACTS) }
        )
        BottomNavigationItem(
            icon = Icons.Default.Settings,
            label = stringResource(R.string.nav_settings),
            selected = selectedTab == BottomNavTab.SETTINGS,
            tag = "nav_settings",
            onClick = { onTabSelected(BottomNavTab.SETTINGS) }
        )
    }
}

@Composable
private fun BottomNavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        modifier = Modifier.testTag(tag),
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
}
