package com.afoxplus.emergency.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afoxplus.emergency.ui.theme.AppShapes
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyColors
import com.afoxplus.emergency.presentation.navigation.BottomNavTab
import com.afoxplus.emergency.presentation.navigation.EmergencyBottomNavigationBar


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToContacts: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HomeTopBar() },
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
            ImmediateActionCard()
            ProtectionSummary()
            Text(
                text = "PROTECCIONES CONFIGURADAS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            ProtectionSetting(
                icon = "♧",
                title = "Alerta rápida",
                description = "3 pulsaciones del botón de encendido",
                tag = "home_quick_alert"
            )
            ProtectionSetting(
                icon = "♧",
                title = "Comprobación periódica",
                description = "Confirmación de bienestar por notificación",
                tag = "home_periodic_check"
            )
        }
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Hola, Valentin", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp))
            Text("Tu seguridad está monitorizada", style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF9DEDE1))
                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("✓", color = Color(0xFF00695C), fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(AppSpacing.xs))
            Text("ACTIVO", color = Color(0xFF00695C), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ImmediateActionCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .25f), AppShapes.large)
            .padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ACCIÓN INMEDIATA", color = EmergencyColors.BrandDark, fontWeight = FontWeight.Bold)
        Text("Mantén presionado para enviar SOS con ubicación", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(AppSpacing.lg))
        Column(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(EmergencyColors.Brand)
                .border(5.dp, Color(0xFFF2B7B7), CircleShape),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("SOS", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("PULSAR", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(AppSpacing.lg))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.medium)
                .background(Color(0xFFE5E6E9))
                .padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("☎", modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD8D8))
                .padding(10.dp), color = EmergencyColors.BrandDark, fontSize = 20.sp)
            Column(modifier = Modifier.weight(1f).padding(start = AppSpacing.md)) {
                Text("Llamar a Emergencias", fontWeight = FontWeight.Bold)
                Text("Marcación rápida (112 / 911)", style = MaterialTheme.typography.bodySmall)
            }
            Text("›", fontSize = 38.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ProtectionSummary() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(Color(0xFFB0EEE6))
            .padding(AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("⬟", modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color(0xFF00796B))
            .padding(10.dp), color = Color.White, fontSize = 20.sp)
        Column(modifier = Modifier.padding(start = AppSpacing.md)) {
            Text("Protección en tiempo real", color = Color(0xFF00695C), fontWeight = FontWeight.Bold)
            Text("2 mecanismos automáticos habilitados", color = Color(0xFF23756D), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ProtectionSetting(icon: String, title: String, description: String, tag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(Color(0xFFE9EAED))
            .padding(AppSpacing.md)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF283593))
            .padding(8.dp), color = Color.White, fontSize = 20.sp)
        Column(modifier = Modifier.weight(1f).padding(horizontal = AppSpacing.md)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = true,
            onCheckedChange = {},
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF001A72))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppemergencyTheme {
        HomeScreen()
    }
}