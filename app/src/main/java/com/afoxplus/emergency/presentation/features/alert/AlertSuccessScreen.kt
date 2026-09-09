package com.afoxplus.emergency.presentation.features.alert

import android.content.ActivityNotFoundException
import android.widget.Toast
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afoxplus.emergency.domain.model.EmergencyContact
import com.afoxplus.emergency.domain.model.EmergencyContactType
import com.afoxplus.emergency.presentation.ui.components.EmergencyStatusPill
import com.afoxplus.emergency.presentation.ui.theme.AppShapes
import com.afoxplus.emergency.presentation.ui.theme.AppSpacing
import com.afoxplus.emergency.presentation.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.presentation.ui.components.EmergencyButton
import com.afoxplus.emergency.presentation.ui.components.EmergencyButtonVariant
import com.afoxplus.emergency.presentation.ui.theme.EmergencyColors
import com.afoxplus.emergency.presentation.util.WhatsAppIntentFactory

@Composable
fun AlertSuccessScreen(
    uiState: AlertSuccessUiState = AlertSuccessUiState(),
    modifier: Modifier = Modifier,
    latitude: Double? = null,
    longitude: Double? = null,
    onBackToHome: () -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(146.dp), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(EmergencyColors.Brand.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(EmergencyColors.Brand),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PriorityHigh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
        Spacer(Modifier.height(AppSpacing.lg))
        Box(
            modifier = Modifier
                .widthIn(min = 212.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(EmergencyColors.Brand)
                .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.sm),
            contentAlignment = Alignment.Center
        ) {
            Text("● ALERTA SOS ENVIADA", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(AppSpacing.xl))
        if (latitude != null && longitude != null) {
            LocationCard(latitude = latitude, longitude = longitude)
            Spacer(Modifier.height(AppSpacing.md))
        }
        ContactsCard(contacts = uiState.contacts)
        Spacer(Modifier.height(AppSpacing.xl))
        EmergencyButton(
            text = "🗨  Enviar alerta por WhatsApp",
            onClick = {
                for (contact in uiState.contacts) {
                    try {
                        context.startActivity(
                            WhatsAppIntentFactory.create(
                                phoneNumber = contact.phoneNumber,
                                message = uiState.sosMessage
                            )
                        )
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(context, "WhatsApp no está instalado.", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("alert_whatsapp_button"),
            enabled = uiState.hasContacts
        )
        Spacer(Modifier.height(AppSpacing.md))
        EmergencyButton(
            text = "Volver al Inicio",
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().testTag("alert_back_home_button"),
            variant = EmergencyButtonVariant.Secondary
        )
    }
}

@Composable
private fun ContactsCard(contacts: List<EmergencyContact>) {
    AlertInfoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Contactos Notificados (${contacts.size}/${contacts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            EmergencyStatusPill(text = "SMS Enviados")
        }

        if (contacts.isEmpty()) {
            Text(
                "No hay contactos de emergencia configurados.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            contacts.forEachIndexed { index, contact ->
                ContactRow(contact = contact)
                if (index != contacts.lastIndex) {
                    Spacer(Modifier.height(AppSpacing.sm))
                }
            }
        }
    }
}

@Composable
private fun ContactRow(contact: EmergencyContact) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactInitial(
            contact = contact,
            color = if (contact.type == EmergencyContactType.PRIMARY) MaterialTheme.colorScheme.primary else EmergencyColors.Secondary
        )
        Spacer(Modifier.width(AppSpacing.md))
        Column(Modifier.weight(1f)) {
            Text(contact.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(
                formatPhoneNumber(contact.phoneNumber),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = EmergencyColors.Secondary
        )
    }
}

private fun formatPhoneNumber(phoneNumber: String): String {
    val digits = phoneNumber.filter { it.isDigit() }
    val grouped = digits.chunked(3).joinToString(" ")
    return if (grouped.isNotBlank()) "+51 $grouped" else phoneNumber
}

@Composable
private fun ContactInitial(contact: EmergencyContact, color: Color) {
    val initial = contact.name.firstOrNull()?.uppercase() ?: when (contact.type) {
        EmergencyContactType.PRIMARY -> "P"
        EmergencyContactType.BACKUP -> "R"
    }

    Text(
        initial,
        modifier = Modifier.iconCircle(color),
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun LocationCard(latitude: Double, longitude: Double) {
    AlertInfoCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = EmergencyColors.Secondary
            )
            Text(
                "UBICACIÓN COMPARTIDA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            "Coordenadas enviadas en tiempo real",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Lat: %.4f, Long: %.4f".format(latitude, longitude),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("alert_location_coordinates")
        )
    }
}

@Composable
private fun AlertInfoCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, AppShapes.large)
            .clip(AppShapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        content()
    }
}

private fun Modifier.iconCircle(color: Color): Modifier = this
    .size(52.dp)
    .clip(CircleShape)
    .background(color)
    .padding(AppSpacing.md)

@Preview(showBackground = true)
@Composable
private fun AlertSuccessScreenPreview() {
    AppemergencyTheme {
        AlertSuccessScreen(
            uiState = AlertSuccessUiState(
                contacts = listOf(
                    EmergencyContact(
                        contactId = "1",
                        name = "Mamá",
                        phoneNumber = "987654321",
                        type = EmergencyContactType.PRIMARY
                    ),
                    EmergencyContact(
                        contactId = "2",
                        name = "Carlos",
                        phoneNumber = "912345678",
                        type = EmergencyContactType.BACKUP
                    )
                ),
                sosMessage = "Necesito ayuda. Esta es mi ubicación actual."
            )
        )
    }
}
