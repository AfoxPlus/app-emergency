package com.afoxplus.emergency.ui.theme

import androidx.compose.ui.graphics.Color
import com.afoxplus.emergency.presentation.ui.theme.AppSpacing
import com.afoxplus.emergency.presentation.ui.theme.EmergencyColors
import org.junit.Assert.assertEquals
import org.junit.Test

class DesignSystemTest {
    @Test
    fun safeGuardPaletteUsesSemanticPrimaryAndStatusColors() {
        assertEquals(Color(0xFF000666), EmergencyColors.Primary)
        assertEquals(Color(0xFF006B5E), EmergencyColors.Secondary)
        assertEquals(Color(0xFFBA1A1A), EmergencyColors.Error)
        assertEquals(Color(0xFFF8F9FB), EmergencyColors.Surface)
    }
}
