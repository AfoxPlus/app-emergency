package com.afoxplus.emergency.data.repository

import android.content.Context
import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.domain.model.NotifiedContact
import com.afoxplus.emergency.domain.repository.AlertHistoryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Persists the Alert History as JSON in SharedPreferences, following the same storage
 * approach used by [EmergencyContactRepositoryImpl].
 */
class AlertHistoryRepositoryImpl(context: Context) : AlertHistoryRepository {

    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getHistory(): List<AlertHistoryEntry> =
        readEntries()
            .sortedByDescending { it.timestampMillis }
            .map { it.toDomain() }

    override fun addEntry(entry: AlertHistoryEntry) {
        writeEntries(readEntries() + entry.toStored())
    }

    override fun updateStatus(id: String, status: AlertStatus) {
        writeEntries(
            readEntries().map { if (it.id == id) it.copy(status = status.name) else it }
        )
    }

    private fun readEntries(): List<StoredAlertHistoryEntry> {
        val json = sharedPreferences.getString(KEY_ALERT_HISTORY, null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun writeEntries(entries: List<StoredAlertHistoryEntry>) {
        sharedPreferences.edit()
            .putString(KEY_ALERT_HISTORY, Json.encodeToString(entries))
            .apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "alert_history_preferences"
        private const val KEY_ALERT_HISTORY = "alert_history"
    }
}

@Serializable
private data class StoredAlertHistoryEntry(
    val id: String,
    val type: String,
    val status: String,
    val timestampMillis: Long,
    val description: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Float? = null,
    val notifiedContacts: List<StoredNotifiedContact> = emptyList()
)

@Serializable
private data class StoredNotifiedContact(
    val name: String,
    val relationship: String
)

private fun AlertHistoryEntry.toStored() = StoredAlertHistoryEntry(
    id = id,
    type = type.name,
    status = status.name,
    timestampMillis = timestampMillis,
    description = description,
    latitude = coordinates?.latitude,
    longitude = coordinates?.longitude,
    accuracyMeters = coordinates?.accuracyMeters,
    notifiedContacts = notifiedContacts.map { StoredNotifiedContact(it.name, it.relationship) }
)

private fun StoredAlertHistoryEntry.toDomain() = AlertHistoryEntry(
    id = id,
    type = AlertType.valueOf(type),
    status = AlertStatus.valueOf(status),
    timestampMillis = timestampMillis,
    description = description,
    coordinates = if (latitude != null && longitude != null) {
        Coordinates(latitude = latitude, longitude = longitude, accuracyMeters = accuracyMeters)
    } else {
        null
    },
    notifiedContacts = notifiedContacts.map { NotifiedContact(it.name, it.relationship) }
)
