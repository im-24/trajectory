// ui/screens/DataScreen.kt
package ui.screens

import TrajectoryColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.DataManager
import data.models.TrajectoryResult
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DataScreen() {
    val dataManager = remember { DataManager() }
    var trajectories by remember { mutableStateOf<List<TrajectoryResult>>(emptyList()) }
    var selectedTrajectory by remember { mutableStateOf<TrajectoryResult?>(null) }

    LaunchedEffect(Unit) {
        trajectories = dataManager.loadAllTrajectories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Trajectory Data",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.Purple,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Saved trajectory calculations",
                        fontSize = 14.sp,
                        color = TrajectoryColors.TextSecondary
                    )
                }
                Icon(
                    Icons.Default.TableChart,
                    contentDescription = null,
                    tint = TrajectoryColors.Purple,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Data Table Card
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TrajectoryColors.Purple.copy(alpha = 0.1f))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TableHeaderCell("Date")
                    TableHeaderCell("Max Distance")
                    TableHeaderCell("Max Height")
                    TableHeaderCell("Time of Flight")
                    TableHeaderCell("Impact Velocity")
                }

                Divider(color = TrajectoryColors.Divider)

                // Table Content
                if (trajectories.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inbox,
                                contentDescription = null,
                                tint = TrajectoryColors.TextMuted,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No saved trajectories",
                                fontSize = 16.sp,
                                color = TrajectoryColors.TextSecondary
                            )
                            Text(
                                text = "Run a simulation to see data here",
                                fontSize = 12.sp,
                                color = TrajectoryColors.TextMuted
                            )
                        }
                    }
                } else {
                    LazyColumn {
                        items(trajectories) { trajectory ->
                            TrajectoryRow(
                                trajectory = trajectory,
                                onClick = { selectedTrajectory = trajectory }
                            )
                            Divider(color = TrajectoryColors.Divider.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }

    // Details Dialog
    if (selectedTrajectory != null) {
        AlertDialog(
            onDismissRequest = { selectedTrajectory = null },
            title = { Text("Trajectory Details") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    Text("Date: ${dateFormat.format(Date(selectedTrajectory!!.timestamp))}")
                    Text("ID: ${selectedTrajectory!!.id}")
                    Text("Points: ${selectedTrajectory!!.points.size}")
                    Text("Max Distance: ${selectedTrajectory!!.maxDistance.toInt()} m")
                    Text("Max Height: ${selectedTrajectory!!.maxHeight.toInt()} m")
                    Text("Time of Flight: ${selectedTrajectory!!.timeOfFlight.toInt()} s")
                    Text("Impact Velocity: ${selectedTrajectory!!.impactVelocity.toInt()} m/s")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTrajectory = null }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dataManager.deleteTrajectory(selectedTrajectory!!.id)
                        trajectories = dataManager.loadAllTrajectories()
                        selectedTrajectory = null
                    }
                ) {
                    Text("Delete", color = TrajectoryColors.Purple)
                }
            }
        )
    }
}

@Composable
private fun TableHeaderCell(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TrajectoryColors.Purple,
    )
}

@Composable
private fun TrajectoryRow(
    trajectory: TrajectoryResult,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = dateFormat.format(Date(trajectory.timestamp)),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            color = TrajectoryColors.TextPrimary
        )
        Text(
            text = "${trajectory.maxDistance.toInt()} m",
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            color = TrajectoryColors.TextPrimary,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "${trajectory.maxHeight.toInt()} m",
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            color = TrajectoryColors.TextPrimary,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "${trajectory.timeOfFlight.toInt()} s",
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            color = TrajectoryColors.TextPrimary,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "${trajectory.impactVelocity.toInt()} m/s",
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            color = TrajectoryColors.TextPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
}