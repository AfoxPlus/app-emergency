package com.afoxplus.emergency.presentation.onboarding

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.afoxplus.emergency.data.repository.OnboardingPreferencesImpl
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingRoutePreferencesImplTest {

    private fun newPreferences(): OnboardingPreferencesImpl =
        OnboardingPreferencesImpl(ApplicationProvider.getApplicationContext())

    @Test
    fun isOnboardingCompleted_defaultsToFalse() {
        assertFalse(newPreferences().isOnboardingCompleted())
    }

    @Test
    fun setOnboardingCompleted_persistsValueAcrossInstances() {
        newPreferences().setOnboardingCompleted(true)

        assertTrue(newPreferences().isOnboardingCompleted())
    }
}
