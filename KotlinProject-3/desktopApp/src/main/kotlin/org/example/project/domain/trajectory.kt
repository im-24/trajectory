package org.example.project.domain

import kotlin.math.sqrt

// Business entities - pure Kotlin data classes
data class Trajectory(
    val id: String,
    val timestamp: Long,
    val projectile: Projectile,
    val environment: Environment,
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
    fun getMaxSpeed(): Double = points.maxOfOrNull { it.speed } ?: 0.0
    fun getAverageSpeed(): Double = points.map { it.speed }.average()
    fun getTotalEnergy(): Double = points.lastOrNull()?.kineticEnergy ?: 0.0
}

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
    val mach: Double,
    val dragCoefficient: Double,
    val airDensity: Double,
    val temperature: Double
)

data class Projectile(
    val name: String = "Projectile",
    val mass: Double = 1.0,
    val radius: Double = 0.1,
    val material: String = "Steel",
    val dragModel: DragModel = DragModel.SPHERE,
    val ballisticCoefficient: Double = 0.5,
    val spinRate: Double = 0.0,
    val spinAxisYaw: Double = 0.0,
    val spinAxisPitch: Double = 0.0
) {
    val diameter: Double get() = radius * 2
    val volume: Double get() = (4.0/3.0) * Math.PI * radius * radius * radius
    val crossSectionalArea: Double get() = Math.PI * radius * radius
    val surfaceArea: Double get() = 4 * Math.PI * radius * radius
}

enum class DragModel { G1, G7, SPHERE, CUSTOM_CD }

data class Environment(
    val gravity: Double = 9.80665,
    val airDensity: Double = 1.225,
    val windSpeed: Double = 0.0,
    val windDirection: Double = 0.0,
    val temperature: Double = 20.0,
    val pressure: Double = 101325.0,
    val humidity: Double = 0.5,
    val altitude: Double = 0.0,
    val temperatureLapseRate: Double = 0.0065,
    val windGustSpeed: Double = 0.0,
    val windGustFrequency: Double = 0.0,
    val turbulenceIntensity: Double = 0.0
) {
    val temperatureKelvin: Double get() = temperature + 273.15
    val speedOfSound: Double get() = kotlin.math.sqrt(1.4 * 287.05 * temperatureKelvin)
}