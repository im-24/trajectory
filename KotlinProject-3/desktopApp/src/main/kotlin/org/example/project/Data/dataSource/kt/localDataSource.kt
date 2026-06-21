package data.datasources

import data.models.TrajectoryResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

class LocalDataSource {
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
                } catch (e: Exception) { null }
            }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()
    }

    fun loadTrajectory(id: String): TrajectoryResult? {
        val file = File(trajectoriesDir, "$id.json")
        return if (file.exists()) {
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) { null }
        } else null
    }

    fun deleteTrajectory(id: String) {
        File(trajectoriesDir, "$id.json").delete()
    }
}