package data.models

import kotlinx.serialization.Serializable

// This is the data model used for serialization
// It's separate from the domain model to maintain clean architecture
@Serializable
data class TrajectoryResultData(
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
)