package com.afoxplus.emergency.data.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PowerButtonPressDetectorTest {

    @Test
    fun `three presses within window triggers callback`() {
        var triggered = false
        val detector = PowerButtonPressDetector(
            windowMillis = 3500L,
            minIntervalMillis = 150L,
            requiredPressCount = 3
        ) {
            triggered = true
        }

        detector.onPressDetected(currentTime = 1000L)
        assertFalse(triggered)
        assertEquals(1, detector.getRecordedPressCount())

        detector.onPressDetected(currentTime = 1800L)
        assertFalse(triggered)
        assertEquals(2, detector.getRecordedPressCount())

        detector.onPressDetected(currentTime = 2600L)
        assertTrue(triggered)
        assertEquals(0, detector.getRecordedPressCount())
    }

    @Test
    fun `less than three presses does not trigger callback`() {
        var triggered = false
        val detector = PowerButtonPressDetector(
            windowMillis = 3500L,
            minIntervalMillis = 150L,
            requiredPressCount = 3
        ) {
            triggered = true
        }

        detector.onPressDetected(currentTime = 1000L)
        detector.onPressDetected(currentTime = 1800L)

        assertFalse(triggered)
        assertEquals(2, detector.getRecordedPressCount())
    }

    @Test
    fun `presses outside sliding window expire and do not trigger`() {
        var triggered = false
        val detector = PowerButtonPressDetector(
            windowMillis = 3500L,
            minIntervalMillis = 150L,
            requiredPressCount = 3
        ) {
            triggered = true
        }

        // Press 1 and 2
        detector.onPressDetected(currentTime = 1000L)
        detector.onPressDetected(currentTime = 2000L)
        assertEquals(2, detector.getRecordedPressCount())

        // Press 3 occurs at 6000L (outside the 3500ms window from 1000L and 2000L)
        detector.onPressDetected(currentTime = 6000L)

        assertFalse(triggered)
        assertEquals(1, detector.getRecordedPressCount())
    }

    @Test
    fun `rapid duplicate events within min interval are ignored`() {
        var triggered = false
        val detector = PowerButtonPressDetector(
            windowMillis = 3500L,
            minIntervalMillis = 150L,
            requiredPressCount = 3
        ) {
            triggered = true
        }

        detector.onPressDetected(currentTime = 1000L)
        // Duplicate broadcast event 50ms later
        detector.onPressDetected(currentTime = 1050L)

        assertEquals(1, detector.getRecordedPressCount())
        assertFalse(triggered)

        detector.onPressDetected(currentTime = 1500L)
        detector.onPressDetected(currentTime = 2000L)

        assertTrue(triggered)
        assertEquals(0, detector.getRecordedPressCount())
    }

    @Test
    fun `detector resets recorded presses after trigger and requires three new presses`() {
        var triggerCount = 0
        val detector = PowerButtonPressDetector(
            windowMillis = 3500L,
            minIntervalMillis = 150L,
            requiredPressCount = 3
        ) {
            triggerCount++
        }

        detector.onPressDetected(currentTime = 1000L)
        detector.onPressDetected(currentTime = 1500L)
        detector.onPressDetected(currentTime = 2000L)

        assertEquals(1, triggerCount)
        assertEquals(0, detector.getRecordedPressCount())

        // Next sequence
        detector.onPressDetected(currentTime = 3000L)
        detector.onPressDetected(currentTime = 3500L)
        assertEquals(1, triggerCount)

        detector.onPressDetected(currentTime = 4000L)
        assertEquals(2, triggerCount)
    }

    @Test
    fun `reset clears all recorded presses`() {
        var triggered = false
        val detector = PowerButtonPressDetector(
            windowMillis = 3500L,
            minIntervalMillis = 150L,
            requiredPressCount = 3
        ) {
            triggered = true
        }

        detector.onPressDetected(currentTime = 1000L)
        detector.onPressDetected(currentTime = 1500L)
        assertEquals(2, detector.getRecordedPressCount())

        detector.reset()
        assertEquals(0, detector.getRecordedPressCount())

        detector.onPressDetected(currentTime = 2000L)
        assertFalse(triggered)
        assertEquals(1, detector.getRecordedPressCount())
    }
}
