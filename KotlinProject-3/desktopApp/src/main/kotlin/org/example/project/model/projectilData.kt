package ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.PI

// ── Projectile Data Model ───────────────────────────────────────────────
data class ProjectileData(
    val name: String = "Projectile",
    val mass: Double = 1.0,          // kg
    val radius: Double = 0.1,        // meters
    val diameter: Double = 0.2,      // meters (calculated)
    val volume: Double = 0.00418879, // m³ (calculated for sphere)
    val material: String = "Steel",
    val color: Color = Color(0xFF7B5EA7)
) {
    // Calculated properties
    val surfaceArea: Double get() = 4 * PI * radius * radius
    val crossSectionalArea: Double get() = PI * radius * radius
}