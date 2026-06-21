// Data/models/TrajectoryData.kt
package data.models

import kotlinx.serialization.Serializable
import java.util.Date
import kotlin.math.sqrt

@Serializable
data class TrajectoryPoint(
    val time: Double,
    val x: Double,
    val y: Double,
    val z: Double,
    val vx: Double,
    val vy: Double,
    val vz: Double,
    val speed: Double,
    val angle: Double,
    val azimuth: Double,
    val kineticEnergy: Double,
    val potentialEnergy: Double,
    val mach: Double = 0.0,
    val dragCoefficient: Double = 0.0,
    val airDensity: Double = 0.0,
    val temperature: Double = 0.0
)

@Serializable
data class TrajectoryResult(
    val id: String,
    val timestamp: Long,
    val projectileData: ProjectileData,
    val environmentData: EnvironmentData,
    val initialVelocity: Double,
    val launchElevation: Double,
    val launchAzimuth: Double,
    val initialHeight: Double,
    val points: List<TrajectoryPoint>,
    val maxHeight: Double,
    val maxDistance: Double,
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

enum class DragModel { G1, G7, SPHERE, CUSTOM_CD }

@Serializable
data class ProjectileData(
    val name: String = "Projectile",
    val mass: Double = 1.0,            // kg
    val radius: Double = 0.1,          // meters
    val diameter: Double = 0.2,        // meters
    val volume: Double = 0.00418879,   // m³
    val material: String = "Steel",
    val colorRed: Int = 123,
    val colorGreen: Int = 94,
    val colorBlue: Int = 167,
    val dragModel: DragModel = DragModel.SPHERE,
    val ballisticCoefficient: Double = 0.5,
    val spinRate: Double = 0.0,        // rad/s, 0 = no spin
    val spinAxisYaw: Double = 0.0,     // deg
    val spinAxisPitch: Double = 0.0    // deg
) {
    val surfaceArea: Double get() = 4 * Math.PI * radius * radius
    val crossSectionalArea: Double get() = Math.PI * radius * radius
}

@Serializable
data class EnvironmentData(
    val gravity: Double = 9.80665,            // m/s²
    val airDensity: Double = 1.225,           // kg/m³ at sea level (used as seaLevelAirDensity)
    val windSpeed: Double = 0.0,              // m/s
    val windDirection: Double = 0.0,          // degrees
    val temperature: Double = 20.0,           // Celsius (sea-level / launch-site temp)
    val pressure: Double = 101325.0,          // Pascals (sea-level / launch-site pressure)
    val humidity: Double = 0.5,               // 0-1
    val altitude: Double = 0.0,               // launch site altitude above sea level, m
    val temperatureLapseRate: Double = 0.0065,// K/m (standard atmosphere)
    val windGustSpeed: Double = 0.0,          // m/s
    val windGustFrequency: Double = 0.0,      // Hz
    val turbulenceIntensity: Double = 0.0     // 0..1
) {
    val seaLevelTemperatureK: Double get() = temperature + 273.15
    val speedOfSoundSeaLevel: Double get() = sqrt(1.4 * 287.05 * seaLevelTemperatureK)
}