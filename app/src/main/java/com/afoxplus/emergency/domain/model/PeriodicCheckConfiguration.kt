package com.afoxplus.emergency.domain.model

/**
 * Persisted configuration for the Periodic Safety Check feature.
 */
data class PeriodicCheckConfiguration(
    val enabled: Boolean = false,
    val frequencyMinutes: Int = DEFAULT_FREQUENCY_MINUTES,
    val responseTimeoutMinutes: Int = DEFAULT_RESPONSE_TIMEOUT_MINUTES,
    val lastConfigurationUpdate: Long = 0L
) {
    companion object {
        const val DEFAULT_FREQUENCY_MINUTES = 30
        const val DEFAULT_RESPONSE_TIMEOUT_MINUTES = 5

        /** BR05: the frequency must always be greater than zero. */
        const val MIN_FREQUENCY_MINUTES = 5
        const val MAX_FREQUENCY_MINUTES = 180
        const val FREQUENCY_STEP_MINUTES = 1
    }
}

/**
 * Predefined check frequency options (AC03).
 */
enum class FrequencyOption(
    val minutes: Int,
    val label: String,
    val description: String,
    val isRecommended: Boolean = false
) {
    SHORT_TRIPS(15, "15 min", "Rutas cortas / Noche"),
    RECOMMENDED(30, "30 min", "Estándar segura", isRecommended = true),
    LONG_TRIPS(60, "1 hora", "Trayectos largos"),
    WORKDAYS(120, "2 horas", "Jornadas y eventos");

    companion object {
        fun fromMinutes(minutes: Int): FrequencyOption? = entries.firstOrNull { it.minutes == minutes }
    }
}

/**
 * Predefined response-time options (AC07).
 */
enum class ResponseTimeOption(
    val minutes: Int,
    val label: String,
    val description: String
) {
    URGENT(2, "2m", "Urgente"),
    OPTIMAL(5, "5m", "Óptimo"),
    RELAXED(10, "10m", "Holgado"),
    CALM(15, "15m", "Tranquilo");

    companion object {
        fun fromMinutes(minutes: Int): ResponseTimeOption? = entries.firstOrNull { it.minutes == minutes }
    }
}
