package org.example.project.domain.usecases

import org.example.project.domain.Trajectory
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class ExportTrajectoryUseCase {

    private val json = Json { prettyPrint = true }

    fun toCSV(trajectory: Trajectory): String {
        return buildString {
            appendLine("Time (s),X (m),Y (m),Z (m),Vx (m/s),Vy (m/s),Vz (m/s),Speed (m/s),Angle (°),Kinetic Energy (J),Potential Energy (J)")
            trajectory.points.forEach { point ->
                appendLine("${point.time},${point.x},${point.y},${point.z},${point.vx},${point.vy},${point.vz},${point.speed},${point.angle},${point.kineticEnergy},${point.potentialEnergy}")
            }
        }
    }

    fun toJSON(trajectory: Trajectory): String {
        return json.encodeToString(
            mapOf(
                "id" to trajectory.id,
                "timestamp" to trajectory.timestamp,
                "maxDistance" to trajectory.maxDistance,
                "maxHeight" to trajectory.maxHeight,
                "timeOfFlight" to trajectory.timeOfFlight,
                "pointCount" to trajectory.points.size
            )
        )
    }

    fun saveToFile(trajectory: Trajectory, format: String, filePath: String): Result<Unit> = runCatching {
        val content = when (format.uppercase()) {
            "CSV" -> toCSV(trajectory)
            "JSON" -> toJSON(trajectory)
            "EXCEL" -> toCSV(trajectory) // Simple fallback
            else -> error("Unsupported format: $format")
        }
        java.io.File(filePath).writeText(content)
    }
}