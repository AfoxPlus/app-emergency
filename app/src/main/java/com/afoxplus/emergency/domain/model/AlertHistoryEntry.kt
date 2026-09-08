package com.afoxplus.emergency.domain.model

/**
 * Source that triggered an emergency alert, used to label and filter entries on the
 * Alert History screen ("Botón SOS", "Alerta Rápida", "Periódica").
 */
enum class AlertType {
    SOS_BUTTON,
    QUICK_ALERT,
    PERIODIC_CHECK
}

/**
 * Outcome of a triggered alert as recorded in the Alert History.
 */
enum class AlertStatus {
    ISSUED,
    CANCELLED
}

/**
 * A contact that was notified when an alert was triggered, as shown on the Alert History
 * screen (e.g. "Carlos Mendoza (Hermano)").
 */
data class NotifiedContact(
    val name: String,
    val relationship: String
)

/**
 * A persisted record of a triggered emergency alert (SOS Button, Quick Alert or Periodic
 * Check-in), so it can later be reviewed on the Alert History screen.
 */
data class AlertHistoryEntry(
    val id: String,
    val type: AlertType,
    val status: AlertStatus,
    val timestampMillis: Long,
    val description: String,
    val coordinates: Coordinates? = null,
    val notifiedContacts: List<NotifiedContact> = emptyList()
)
