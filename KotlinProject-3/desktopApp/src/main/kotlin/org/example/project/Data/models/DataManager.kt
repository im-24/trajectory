// data/DataManager.kt
package data

import data.models.TrajectoryResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class DataManager {

    private val json = Json { prettyPrint = true }
    private val trajectoriesDir = File(System.getProperty("user.home"), ".trajectory/data")

    init {
        trajectoriesDir.mkdirs()
    }

    fun saveTrajectory(result: TrajectoryResult) {
        val file = File(trajectoriesDir, "${result.id}.json")
        file.writeText(json.encodeToString(result))
    }

    fun loadAllTrajectories(): List<TrajectoryResult> {
        return trajectoriesDir.listFiles { file -> file.extension == "json" }
            ?.mapNotNull { file ->
                try {
                    json.decodeFromString<TrajectoryResult>(file.readText())
                } catch (e: Exception) {
                    null
                }
            }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()
    }

    fun loadTrajectory(id: String): TrajectoryResult? {
        val file = File(trajectoriesDir, "$id.json")
        return if (file.exists()) {
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun deleteTrajectory(id: String) {
        File(trajectoriesDir, "$id.json").delete()
    }

    fun exportToCSV(result: TrajectoryResult, filePath: String) {
        val csvContent = buildString {
            appendLine("Time (s),X (m),Y (m),Vx (m/s),Vy (m/s),Speed (m/s),Angle (deg),Kinetic Energy (J),Potential Energy (J)")
            result.points.forEach { point ->
                appendLine("${point.time},${point.x},${point.y},${point.vx},${point.vy},${point.speed},${point.angle},${point.kineticEnergy},${point.potentialEnergy}")
            }
        }
        File(filePath).writeText(csvContent)
    }

    fun exportToJSON(result: TrajectoryResult, filePath: String) {
        File(filePath).writeText(json.encodeToString(result))
    }

    fun exportToExcel(result: TrajectoryResult, filePath: String) {
        // For Excel, we'll create a CSV with .xlsx extension
        // Full Excel support would require Apache POI library
        exportToCSV(result, filePath.replace(".xlsx", ".csv"))
    }
}