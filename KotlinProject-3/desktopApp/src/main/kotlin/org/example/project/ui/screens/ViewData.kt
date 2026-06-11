// ui/screens/DataScreen.kt
package ui.screens

import org.example.project.ui.them.TrajectoryColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.*
// ui/screens/DataScreen.kt
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer


@Composable
fun DataScreen() {
    val dataManager = remember { DataManager() }
    var trajectories by remember { mutableStateOf<List<TrajectoryResult>>(emptyList()) }
    var selectedTrajectory by remember { mutableStateOf<TrajectoryResult?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Velocity, 1: Position X, 2: Position Y, 3: Position Z

    LaunchedEffect(Unit) {
        trajectories = dataManager.loadAllTrajectories()
    }

    // Get the last simulation (most recent)
    val lastTrajectory = trajectories.lastOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .verticalScroll(rememberScrollState())
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

        // Most Recent Simulation with Graphs Section
        if (lastTrajectory != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Most Recent Simulation",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.Purple
                    )

                    Text(
                        text = "Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(lastTrajectory.timestamp))}",
                        fontSize = 12.sp,
                        color = TrajectoryColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Summary stats for most recent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge("Max Distance", "${lastTrajectory.maxDistance.toInt()} m")
                        StatBadge("Max Height", "${lastTrajectory.maxHeight.toInt()} m")
                        StatBadge("Time of Flight", "${lastTrajectory.timeOfFlight.toInt()} s")
                        StatBadge("Impact Velocity", "${lastTrajectory.impactVelocity.toInt()} m/s")
                    }

                    // Tab Row for different graphs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = TrajectoryColors.Background,
                        contentColor = TrajectoryColors.Purple
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Velocity vs Time") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Position X vs Time") }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Position Y vs Time") }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("Position Z vs Time") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Graph display based on selected tab
                    when (selectedTab) {
                        0 -> VelocityGraph(trajectory = lastTrajectory)
                        1 -> PositionGraph(trajectory = lastTrajectory, component = "X")
                        2 -> PositionGraph(trajectory = lastTrajectory, component = "Y")
                        3 -> PositionGraph(trajectory = lastTrajectory, component = "Z")
                    }

                    // 3D Position Trajectory Graph
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "3D Trajectory Path",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.Purple,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ThreeDPositionGraph(trajectory = lastTrajectory)
                }
            }
        }

        // All Trajectories List
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                    TableHeaderCell("Actions")
                }

                HorizontalDivider(color = TrajectoryColors.Divider)

                // Table Content
                if (trajectories.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
                    // LazyColumn cannot be used inside a verticalScroll parent because it needs
                    // a bounded height constraint. We render items directly in a plain Column
                    // instead — the outer verticalScroll Column already handles scrolling.
                    Column {
                        trajectories.reversed().forEach { trajectory ->  // Show newest first
                            TrajectoryRow(
                                trajectory = trajectory,
                                onClick = { selectedTrajectory = trajectory },
                                onDelete = {
                                    dataManager.deleteTrajectory(trajectory.id)
                                    trajectories = dataManager.loadAllTrajectories()
                                }
                            )
                            HorizontalDivider(color = TrajectoryColors.Divider.copy(alpha = 0.5f))
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preview of points
                    Text("First 5 points:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    selectedTrajectory!!.points.take(5).forEach { point ->
                        Text(
                            text = "t=%.2fs, x=%.1fm, y=%.1fm, z=%.1fm".format(point.time, point.x, point.y, point.z),
                            fontSize = 10.sp,
                            color = TrajectoryColors.TextSecondary
                        )
                    }
                    if (selectedTrajectory!!.points.size > 5) {
                        Text("... and ${selectedTrajectory!!.points.size - 5} more points", fontSize = 10.sp)
                    }
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
private fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TrajectoryColors.Purple,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TrajectoryColors.TextSecondary
        )
    }
}

@Composable
private fun VelocityGraph(trajectory: TrajectoryResult) {
    val points = trajectory.points
    val textMeasurer = rememberTextMeasurer()

    // Calculate velocities at each point
    val velocities = mutableListOf<Double>()
    val timesWithVelocity = mutableListOf<Float>()

    for (i in 0 until points.size - 1) {
        val p1 = points[i]
        val p2 = points[i + 1]
        val dt = (p2.time - p1.time)
        if (dt > 0) {
            val dx = p2.x - p1.x
            val dy = p2.y - p1.y
            val dz = p2.z - p1.z
            val v = sqrt(dx * dx + dy * dy + dz * dz) / dt
            velocities.add(v)
            timesWithVelocity.add(p1.time.toFloat())
        }
    }

    // Add last point
    if (velocities.isNotEmpty() && points.isNotEmpty()) {
        velocities.add(velocities.last())
        timesWithVelocity.add(points.last().time.toFloat())
    }

    val maxVelocity = if (velocities.isNotEmpty()) velocities.maxOrNull()?.toFloat() ?: 1f else 1f
    val maxTime = if (timesWithVelocity.isNotEmpty()) timesWithVelocity.maxOrNull() ?: 1f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = TrajectoryColors.Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val paddingLeft = 60f
            val paddingRight = 40f
            val paddingTop = 40f
            val paddingBottom = 40f
            val graphWidth = size.width - paddingLeft - paddingRight
            val graphHeight = size.height - paddingTop - paddingBottom

            // Draw axes
            drawLine(
                color = Color.Gray,
                start = Offset(paddingLeft, paddingTop),
                end = Offset(paddingLeft, size.height - paddingBottom),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.Gray,
                start = Offset(paddingLeft, size.height - paddingBottom),
                end = Offset(size.width - paddingRight, size.height - paddingBottom),
                strokeWidth = 2f
            )

            // Draw axis labels
            drawText(
                textMeasurer = textMeasurer,
                text = "Time (s)",
                topLeft = Offset(size.width / 2 - 30f, size.height - 15f),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )

            drawText(
                textMeasurer = textMeasurer,
                text = "Velocity (m/s)",
                topLeft = Offset(10f, size.height / 2),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )

            // Draw grid lines and labels
            for (i in 0..4) {
                val y = paddingTop + (graphHeight / 4) * i
                drawLine(
                    color = Color.LightGray,
                    start = Offset(paddingLeft, y),
                    end = Offset(size.width - paddingRight, y),
                    strokeWidth = 0.5f
                )

                val velocityValue = (maxVelocity * (1 - i / 4f)).toInt()
                drawText(
                    textMeasurer = textMeasurer,
                    text = velocityValue.toString(),
                    topLeft = Offset(paddingLeft - 35f, y - 8f),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                )
            }

            for (i in 0..4) {
                val x = paddingLeft + (graphWidth / 4) * i
                drawLine(
                    color = Color.LightGray,
                    start = Offset(x, paddingTop),
                    end = Offset(x, size.height - paddingBottom),
                    strokeWidth = 0.5f
                )

                val timeValue = (maxTime * i / 4).toInt()
                drawText(
                    textMeasurer = textMeasurer,
                    text = timeValue.toString(),
                    topLeft = Offset(x - 10f, size.height - paddingBottom + 10f),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                )
            }

            // Draw velocity line
            if (velocities.isNotEmpty() && timesWithVelocity.isNotEmpty()) {
                val path = Path()
                var isFirst = true

                for (index in velocities.indices) {
                    if (index < timesWithVelocity.size) {
                        val x = paddingLeft + (timesWithVelocity[index] / maxTime) * graphWidth
                        val y = paddingTop + graphHeight - (velocities[index].toFloat() / maxVelocity) * graphHeight

                        if (isFirst) {
                            path.moveTo(x, y)
                            isFirst = false
                        } else {
                            path.lineTo(x, y)
                        }
                    }
                }

                drawPath(
                    path = path,
                    color = TrajectoryColors.Purple,
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}

@Composable
private fun PositionGraph(trajectory: TrajectoryResult, component: String) {
    val points = trajectory.points
    val textMeasurer = rememberTextMeasurer()

    val values = when (component) {
        "X" -> points.map { it.x.toFloat() }
        "Y" -> points.map { it.y.toFloat() }
        else -> points.map { it.z.toFloat() }
    }

    val maxValue = if (values.isNotEmpty()) max(values.maxOrNull()?.absoluteValue ?: 1f, 1f) else 1f
    val minValue = if (values.isNotEmpty()) min(values.minOrNull()?.absoluteValue ?: 0f, 0f) else 0f
    val range = max(maxValue, abs(minValue))
    val times = points.map { it.time.toFloat() }
    val maxTime = if (times.isNotEmpty()) times.maxOrNull() ?: 1f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = TrajectoryColors.Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val paddingLeft = 60f
            val paddingRight = 40f
            val paddingTop = 40f
            val paddingBottom = 40f
            val graphWidth = size.width - paddingLeft - paddingRight
            val graphHeight = size.height - paddingTop - paddingBottom

            // Draw axes
            drawLine(
                color = Color.Gray,
                start = Offset(paddingLeft, paddingTop),
                end = Offset(paddingLeft, size.height - paddingBottom),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.Gray,
                start = Offset(paddingLeft, size.height / 2),
                end = Offset(size.width - paddingRight, size.height / 2),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.Gray,
                start = Offset(paddingLeft, size.height - paddingBottom),
                end = Offset(size.width - paddingRight, size.height - paddingBottom),
                strokeWidth = 2f
            )

            // Draw axis labels
            drawText(
                textMeasurer = textMeasurer,
                text = "Time (s)",
                topLeft = Offset(size.width / 2 - 30f, size.height - 15f),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )

            drawText(
                textMeasurer = textMeasurer,
                text = "Position $component (m)",
                topLeft = Offset(10f, size.height / 2),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )

            // Draw grid lines
            for (i in 0..4) {
                val y = paddingTop + (graphHeight / 4) * i
                drawLine(
                    color = Color.LightGray,
                    start = Offset(paddingLeft, y),
                    end = Offset(size.width - paddingRight, y),
                    strokeWidth = 0.5f
                )

                val value = (range * 2 * (1 - i / 4f) - range).toInt()
                drawText(
                    textMeasurer = textMeasurer,
                    text = value.toString(),
                    topLeft = Offset(paddingLeft - 35f, y - 8f),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                )
            }

            for (i in 0..4) {
                val x = paddingLeft + (graphWidth / 4) * i
                drawLine(
                    color = Color.LightGray,
                    start = Offset(x, paddingTop),
                    end = Offset(x, size.height - paddingBottom),
                    strokeWidth = 0.5f
                )

                val timeValue = (maxTime * i / 4).toInt()
                drawText(
                    textMeasurer = textMeasurer,
                    text = timeValue.toString(),
                    topLeft = Offset(x - 10f, size.height - paddingBottom + 10f),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                )
            }

            // Draw position line
            if (values.isNotEmpty()) {
                val path = Path()
                var isFirst = true

                values.forEachIndexed { index, value ->
                    val x = paddingLeft + (times[index] / maxTime) * graphWidth
                    val y = paddingTop + graphHeight / 2 - (value / (range * 2)) * graphHeight

                    if (isFirst) {
                        path.moveTo(x, y)
                        isFirst = false
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = when (component) {
                        "X" -> Color.Red
                        "Y" -> Color.Green
                        else -> Color.Blue
                    },
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}

@Composable
private fun ThreeDPositionGraph(trajectory: TrajectoryResult) {
    val points = trajectory.points
    val textMeasurer = rememberTextMeasurer()

    if (points.isEmpty()) return

    // Find bounds
    val maxX = points.maxOfOrNull { abs(it.x) } ?: 10.0
    val maxY = points.maxOfOrNull { it.y } ?: 8.0
    val maxZ = points.maxOfOrNull { abs(it.z) } ?: 10.0
    val maxCoord = maxOf(maxX, maxY, maxZ)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = TrajectoryColors.Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val scale = min(size.width, size.height) * 0.3f / maxCoord.toFloat()

            // Draw axes
            val xEnd = project3D(10.0, 0.0, 0.0, centerX, centerY, scale)
            val yEnd = project3D(0.0, 10.0, 0.0, centerX, centerY, scale)
            val zEnd = project3D(0.0, 0.0, 10.0, centerX, centerY, scale)

            drawLine(Color.Red, Offset(centerX, centerY), Offset(xEnd.first, xEnd.second), 2f)
            drawLine(Color.Green, Offset(centerX, centerY), Offset(yEnd.first, yEnd.second), 2f)
            drawLine(Color.Blue, Offset(centerX, centerY), Offset(zEnd.first, zEnd.second), 2f)

            // Draw axis labels
            drawText(
                textMeasurer = textMeasurer,
                text = "X",
                topLeft = Offset(xEnd.first + 5f, xEnd.second - 8f),
                style = TextStyle(
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "Y",
                topLeft = Offset(yEnd.first + 5f, yEnd.second - 8f),
                style = TextStyle(
                    color = Color.Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "Z",
                topLeft = Offset(zEnd.first + 5f, zEnd.second - 8f),
                style = TextStyle(
                    color = Color.Blue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Draw grid
            for (i in -10..10 step 2) {
                val startX = project3D(i.toDouble(), 0.0, -10.0, centerX, centerY, scale)
                val endX = project3D(i.toDouble(), 0.0, 10.0, centerX, centerY, scale)
                drawLine(Color.LightGray.copy(alpha = 0.5f), Offset(startX.first, startX.second), Offset(endX.first, endX.second), 0.5f)

                val startZ = project3D(-10.0, 0.0, i.toDouble(), centerX, centerY, scale)
                val endZ = project3D(10.0, 0.0, i.toDouble(), centerX, centerY, scale)
                drawLine(Color.LightGray.copy(alpha = 0.5f), Offset(startZ.first, startZ.second), Offset(endZ.first, endZ.second), 0.5f)
            }

            // Draw trajectory path
            val path = Path()
            var isFirst = true

            points.forEach { point ->
                val (x, y) = project3D(point.x, point.y, point.z, centerX, centerY, scale)

                if (isFirst) {
                    path.moveTo(x, y)
                    isFirst = false
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = TrajectoryColors.Purple,
                style = Stroke(width = 4f)
            )

            // Draw points along trajectory with gradient colors
            points.forEachIndexed { index, point ->
                val (x, y) = project3D(point.x, point.y, point.z, centerX, centerY, scale)
                val alpha = (index.toFloat() / points.size) * 0.8f + 0.2f
                drawCircle(
                    color = TrajectoryColors.Purple.copy(alpha = alpha),
                    radius = 4f,
                    center = Offset(x, y)
                )
            }

            // Draw start point (green)
            val (startX, startY) = project3D(points[0].x, points[0].y, points[0].z, centerX, centerY, scale)
            drawCircle(Color.Green, 8f, Offset(startX, startY))
            drawCircle(Color.White, 4f, Offset(startX, startY))

            // Draw end point (red)
            val (endX, endY) = project3D(points.last().x, points.last().y, points.last().z, centerX, centerY, scale)
            drawCircle(Color.Red, 8f, Offset(endX, endY))
            drawCircle(Color.White, 4f, Offset(endX, endY))
        }
    }
}

private fun project3D(
    x: Double,
    y: Double,
    z: Double,
    centerX: Float,
    centerY: Float,
    scale: Float
): Pair<Float, Float> {
    // Simple isometric projection
    val angle = 45.0 // degrees
    val rad = Math.toRadians(angle)
    val cos = cos(rad).toFloat()
    val sin = sin(rad).toFloat()

    val screenX = centerX + ((x - z) * cos).toFloat() * scale
    val screenY = centerY + ((x + z) * sin - y).toFloat() * scale

    return screenX to screenY
}

@Composable
private fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TrajectoryColors.Purple,
        modifier = modifier
    )
}
// Table Header



@Composable
private fun TrajectoryRow(
    trajectory: TrajectoryResult,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
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

        IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.weight(0.5f)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = TrajectoryColors.Purple.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Trajectory") },
            text = { Text("Are you sure you want to delete this trajectory?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = TrajectoryColors.Purple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}