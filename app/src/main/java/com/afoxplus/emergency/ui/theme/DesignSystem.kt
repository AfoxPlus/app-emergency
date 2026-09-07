package com.afoxplus.emergency.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object AppSpacing {
    val unit = 4.dp
    val xs = unit
    val sm = 2 * unit
    val md = 3 * unit
    val lg = 4 * unit
    val xl = 6 * unit
    val xxl = 8 * unit
    val gutter = lg
    val marginMobile = lg
    val marginTablet = xxl
    val marginDesktop = 8 * unit
    val touchTargetMin = 12 * unit
}

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
