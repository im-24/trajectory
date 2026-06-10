// Data/models/TrajectoryData.kt
package data.models

import kotlinx.serialization.Serializable
import java.util.Date



@Serializable
data class TrajectoryPoint(
    val time: Double,      // seconds
    val x: Double,         // meters (forward)
    val y: Double,         // meters (height)
    val z: Double,         // meters (lateral)
    val vx: Double,        // m/s
    val vy: Double,        // m/s
    val vz: Double,        // m/s
    val speed: Double,     // m/s (total velocity)
    val angle: Double,     // degrees (flight path elevation)
    val azimuth: Double,   // degrees (horizontal direction)
    val kineticEnergy: Double,
    val potentialEnergy: Double
)

@Serializable
data class TrajectoryResult(
    val id: String,
    val timestamp: Long,
    val projectileData: ProjectileData,
    val environmentData: EnvironmentData,
    val initialVelocity: Double,
    val launchElevation: Double,   // renamed from launchAngle
    val launchAzimuth: Double,     // new: 0° = +X, 90° = +Z
    val initialHeight: Double,
    val points: List<TrajectoryPoint>,
    val maxHeight: Double,
    val maxDistance: Double,       // horizontal distance (sqrt(x²+z²))
    val timeOfFlight: Double,
    val impactVelocity: Double,
    val impactAngle: Double
) {
    fun getDate(): Date = Date(timestamp)

    companion object {
        fun empty() = TrajectoryResult(
            id = "", timestamp = System.currentTimeMillis(),
            projectileData = ProjectileData(),
            environmentData = EnvironmentData(),
            initialVelocity = 0.0,
            launchElevation = 0.0,
            launchAzimuth = 0.0,
            initialHeight = 0.0,
            points = emptyList(),
            maxHeight = 0.0, maxDistance = 0.0,
            timeOfFlight = 0.0, impactVelocity = 0.0, impactAngle = 0.0
        )
    }
}


@Serializable
data class ProjectileData(
    val name: String = "Projectile",
    val mass: Double = 1.0,          // kg
    val radius: Double = 0.1,        // meters
    val diameter: Double = 0.2,      // meters
    val volume: Double = 0.00418879, // m³
    val material: String = "Steel",
    val colorRed: Int = 123,
    val colorGreen: Int = 94,
    val colorBlue: Int = 167
) {
    val surfaceArea: Double get() = 4 * Math.PI * radius * radius
    val crossSectionalArea: Double get() = Math.PI * radius * radius
}

@Serializable
data class EnvironmentData(
    val gravity: Double = 9.81,      // m/s²
    val airDensity: Double = 1.225,  // kg/m³
    val windSpeed: Double = 0.0,     // m/s
    val windDirection: Double = 0.0, // degrees
    val temperature: Double = 20.0,  // Celsius
    val pressure: Double = 101325.0, // Pascals
    val humidity: Double = 0.5       // 0-1
)