package com.afoxplus.emergency.data.service

/**
 * Detects rapid consecutive presses of the power button within a sliding time window.
 */
class PowerButtonPressDetector(
    private val windowMillis: Long = DEFAULT_WINDOW_MILLIS,
    private val minIntervalMillis: Long = DEFAULT_MIN_INTERVAL_MILLIS,
    private val requiredPressCount: Int = DEFAULT_REQUIRED_PRESS_COUNT,
    private val onThresholdReached: () -> Unit
) {
    private val pressTimestamps = mutableListOf<Long>()

    fun onPressDetected(currentTime: Long = System.currentTimeMillis()) {
        val lastTimestamp = pressTimestamps.lastOrNull()
        if (lastTimestamp != null && (currentTime - lastTimestamp) < minIntervalMillis) {
            return
        }

        pressTimestamps.removeAll { currentTime - it > windowMillis }
        pressTimestamps.add(currentTime)

        if (pressTimestamps.size >= requiredPressCount) {
            pressTimestamps.clear()
            onThresholdReached()
        }
    }

    fun reset() {
        pressTimestamps.clear()
    }

    fun getRecordedPressCount(): Int = pressTimestamps.size

    companion object {
        const val DEFAULT_WINDOW_MILLIS = 3000L
        const val DEFAULT_MIN_INTERVAL_MILLIS = 100L
        const val DEFAULT_REQUIRED_PRESS_COUNT = 2
    }
}
