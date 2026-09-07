package com.afoxplus.emergency.presentation.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afoxplus.emergency.ui.theme.AppShapes
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyButtonVariant
import com.afoxplus.emergency.ui.theme.EmergencyColors

@Composable
fun AlertSuccessScreen(
    modifier: Modifier = Modifier,
    onCancelAlert: () -> Unit = {},
    onBackToHome: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(124.dp)
                .clip(CircleShape)
                .background(EmergencyColors.Brand)
        )
        Spacer(Modifier.height(AppSpacing.lg))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.large)
                .background(EmergencyColors.Brand)
                .padding(vertical = AppSpacing.sm),
            contentAlignment = Alignment.Center
        ) {
            Text("♙  ALERTA\n     ACTIVADA", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(AppSpacing.xl))
        Text("Alerta de emergencia", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(AppSpacing.sm))
        Text(
            "Tu protocolo de seguridad está activo.\nMantén la calma, la ayuda está en camino.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(AppSpacing.xl))
        LocationCard()
        Spacer(Modifier.height(AppSpacing.md))
        AlertInfoCard(icon = "♣", title = "Contactos notificados") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("M", modifier = Modifier.iconCircle(EmergencyColors.Secondary), color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(AppSpacing.xs))
                Text("C", modifier = Modifier.iconCircle(Color(0xFF00796B)), color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(AppSpacing.md))
                Column {
                    Text("Mamá y Carlos", fontWeight = FontWeight.Bold)
                    Text("2 contactos alertados", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(AppSpacing.md))
        AlertInfoCard(icon = "▤", title = "Mensaje enviado") {
            Text(
                "\"Necesito ayuda. Esta es mi ubicación actual.\"",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppShapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(AppSpacing.lg),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(Modifier.height(AppSpacing.xxl))
        EmergencyButton(
            text = "⊗  Cancelar alerta",
            onClick = onCancelAlert,
            modifier = Modifier.fillMaxWidth().testTag("alert_cancel_button")
        )
        Spacer(Modifier.height(AppSpacing.md))
        EmergencyButton(
            text = "Volver al inicio",
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().testTag("alert_back_home_button"),
            variant = EmergencyButtonVariant.Secondary
        )
    }
}

@Composable
private fun LocationCard() {
    AlertInfoCard(icon = "⌖", title = "Ubicación obtenida") {
        Text("Enviando coordenadas en tiempo real…", style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .clip(AppShapes.medium)
                .background(Color(0xFFE9EEF0))
                .border(1.dp, Color(0xFFD2D7D9), AppShapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Text("●  GPS en vivo ±5m\n\n             📍", color = Color(0xFF00695C), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AlertInfoCard(
    icon: String,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, modifier = Modifier.iconCircle(EmergencyColors.Secondary), color = Color.White)
            Spacer(Modifier.width(AppSpacing.md))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
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
    AppemergencyTheme { AlertSuccessScreen() }
}
