// data/models/ReportData.kt
package data.models

import java.util.Date

data class ReportData(
    val title: String = "Trajectory Simulation Report",
    val date: Date = Date(),
    val programInfo: ProgramInfo,
    val projectileParameters: ProjectileReportData,
    val environmentParameters: EnvironmentReportData,
    val simulationResults: SimulationResultsData,
    val trajectoryPoints: List<TrajectoryPoint>,
    val chartImagePath: String? = null
)

data class ProgramInfo(
    val name: String = "Trajectory Pro",
    val version: String = "1.0.0",
    val description: String = "Advanced projectile motion simulation software with real-time visualization and analysis capabilities.",
    val features: List<String> = listOf(
        "Realistic physics simulation with air drag",
        "2D and 3D trajectory visualization",
        "Environmental condition modeling",
        "Data export and reporting"
    )
)

data class ProjectileReportData(
    val name: String,
    val mass: Double,
    val radius: Double,
    val diameter: Double,
    val volume: Double,
    val surfaceArea: Double,
    val material: String
)

data class EnvironmentReportData(
    val gravity: Double,
    val airDensity: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val temperature: Double,
    val pressure: Double,
    val humidity: Double
)

// In data/models/ReportData.kt
data class SimulationResultsData(
    val initialVelocity: Double,
    val launchAngle: Double,        // Keep this name for backward compatibility in reports
    val launchAzimuth: Double = 0.0, // Add this
    val initialHeight: Double,
    val maxDistance: Double,
    val maxHeight: Double,
    val timeOfFlight: Double,
    val impactVelocity: Double,
    val impactAngle: Double,
    val maxSpeed: Double,
    val avgSpeed: Double,
    val totalEnergy: Double
)