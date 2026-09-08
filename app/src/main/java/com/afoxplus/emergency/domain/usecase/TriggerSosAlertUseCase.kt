package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.domain.repository.LocationProvider
import javax.inject.Inject

/**
 * Outcome of triggering an SOS alert: the captured [Coordinates] (or `null` if they could
 * not be resolved) and the id of the persisted Alert History entry, if the alert was sent.
 */
data class SosAlertResult(
    val coordinates: Coordinates?,
    val historyEntryId: String? = null
)

/**
 * Triggers an SOS alert from the Home Screen. Reuses [TriggerAlertUseCase] to send
 * the emergency message to all saved contacts, and additionally attempts to capture the
 * device's current [Coordinates] so they can be displayed on the Alert Activated screen.
 * Location capture never blocks or fails the alert: if permissions are missing or the
 * location cannot be resolved, `null` coordinates are returned but the message is still sent.
 */
class TriggerSosAlertUseCase @Inject constructor(
    private val locationProvider: LocationProvider,
    private val triggerAlertUseCase: TriggerAlertUseCase
) {
    operator fun invoke(): SosAlertResult {
        val coordinates = try {
            locationProvider.getCurrentLocation()
        } catch (_: Exception) {
            null
        }

        val result = triggerAlertUseCase(alertType = AlertType.SOS_BUTTON, coordinates = coordinates)

        return SosAlertResult(coordinates = coordinates, historyEntryId = result.historyEntryId)
    }
}

