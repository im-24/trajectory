package presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.domain.Trajectory  // Use domain Trajectory
import org.example.project.presentation.ui.them.TrajectoryColors

@Composable
fun TrajectoryResults(
    trajectory: Trajectory,  // Use domain Trajectory
    onExport: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Results",
                    style = MaterialTheme.typography.titleMedium,
                    color = TrajectoryColors.Purple
                )
                IconButton(onClick = onExport) {
                    Icon(Icons.Default.Download, "Export")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem("Distance", "${trajectory.maxDistance.toInt()} m")
                MetricItem("Height", "${trajectory.maxHeight.toInt()} m")
                MetricItem("Time", "${trajectory.timeOfFlight.toInt()} s")
                MetricItem("Impact", "${trajectory.impactVelocity.toInt()} m/s")
            }

            // Show additional stats
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem("Points", "${trajectory.points.size}")
                MetricItem("Max Speed", "${trajectory.getMaxSpeed().toInt()} m/s")
                MetricItem("Avg Speed", "${trajectory.getAverageSpeed().toInt()} m/s")
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = TrajectoryColors.Purple)
        Text(label, style = MaterialTheme.typography.bodySmall, color = TrajectoryColors.TextSecondary)
    }
}