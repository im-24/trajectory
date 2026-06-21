// utils/ExportUtils.kt
package utils

import data.models.TrajectoryResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object ExportUtils {

    private val json = Json { prettyPrint = true }

    fun exportToCSV(result: TrajectoryResult, filePath: String) {
        val content = buildString {
            appendLine("Time (s),X (m),Y (m),Vx (m/s),Vy (m/s),Speed (m/s),Angle (deg),Kinetic Energy (J),Potential Energy (J)")
            result.points.forEach { point ->
                appendLine("${point.time},${point.x},${point.y},${point.vx},${point.vy},${point.speed},${point.angle},${point.kineticEnergy},${point.potentialEnergy}")
            }
        }
        File(filePath).writeText(content)
    }

    fun exportToJSON(result: TrajectoryResult, filePath: String) {
        File(filePath).writeText(json.encodeToString(result))
    }

    fun exportToXML(result: TrajectoryResult, filePath: String) {
        val content = buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            appendLine("<trajectory>")
            appendLine("  <metadata>")
            appendLine("    <id>${result.id}</id>")
            appendLine("    <timestamp>${result.timestamp}</timestamp>")
            appendLine("    <maxDistance>${result.maxDistance}</maxDistance>")
            appendLine("    <maxHeight>${result.maxHeight}</maxHeight>")
            appendLine("    <timeOfFlight>${result.timeOfFlight}</timeOfFlight>")
            appendLine("  </metadata>")
            appendLine("  <points>")
            result.points.forEach { point ->
                appendLine("    <point>")
                appendLine("      <time>${point.time}</time>")
                appendLine("      <x>${point.x}</x>")
                appendLine("      <y>${point.y}</y>")
                appendLine("      <vx>${point.vx}</vx>")
                appendLine("      <vy>${point.vy}</vy>")
                appendLine("      <speed>${point.speed}</speed>")
                appendLine("    </point>")
            }
            appendLine("  </points>")
            appendLine("</trajectory>")
        }
        File(filePath).writeText(content)
    }
}