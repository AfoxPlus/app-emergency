package com.afoxplus.emergency.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.afoxplus.emergency.R
import com.afoxplus.emergency.ui.theme.AppSpacing

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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomNavigationItem(
            icon = "⌂",
            label = stringResource(R.string.nav_home),
            selected = selectedTab == BottomNavTab.HOME,
            tag = "nav_home",
            onClick = { onTabSelected(BottomNavTab.HOME) }
        )
        BottomNavigationItem(
            icon = "▣",
            label = stringResource(R.string.nav_contacts),
            selected = selectedTab == BottomNavTab.CONTACTS,
            tag = "nav_contacts",
            onClick = { onTabSelected(BottomNavTab.CONTACTS) }
        )
        BottomNavigationItem(
            icon = "⚙",
            label = stringResource(R.string.nav_settings),
            selected = selectedTab == BottomNavTab.SETTINGS,
            tag = "nav_settings",
            onClick = { onTabSelected(BottomNavTab.SETTINGS) }
        )
    }
}

@Composable
private fun BottomNavigationItem(
    icon: String,
    label: String,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    val tint = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = AppSpacing.xl)
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Text(icon, fontSize = 24.sp, color = tint)
        Text(label, style = MaterialTheme.typography.labelMedium, color = tint)
    }
}
