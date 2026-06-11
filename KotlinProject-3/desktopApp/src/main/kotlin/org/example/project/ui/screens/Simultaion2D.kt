// ui/screens/Simulation2D.kt
package ui.screens

import org.example.project.ui.them.TrajectoryColors
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.DataManager
import data.models.EnvironmentData
import data.models.ProjectileData
import data.models.TrajectoryPoint
import data.models.TrajectoryResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import physics.TrajectoryCalculator
import kotlin.math.*

/** The three orthogonal planes available for the 2D view. */
enum class ViewPlane(val label: String, val xLabel: String, val yLabel: String) {
    XY("XY (Side)", "Distance X (m)", "Height Y (m)"),
    XZ("XZ (Top)", "Distance X (m)", "Lateral Z (m)"),
    YZ("YZ (Front)", "Height Y (m)", "Lateral Z (m)")
}

/** Extracts the (horizontal, vertical) values for a point given a plane. */
private fun TrajectoryPoint.componentsFor(plane: ViewPlane): Pair<Double, Double> = when (plane) {
    ViewPlane.XY -> x to y
    ViewPlane.XZ -> x to z
    ViewPlane.YZ -> y to z
}

@Composable
fun TwoDSimulationScreen(
    projectileData: ProjectileData,
    environmentData: EnvironmentData
) {
    val scope = rememberCoroutineScope()
    val dataManager = remember { DataManager() }
    val calculator = remember { TrajectoryCalculator() }

    var trajectoryResult by remember { mutableStateOf<TrajectoryResult?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTimeIndex by remember { mutableStateOf(0) }
    var animationJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Defaults for realistic simulation
    var initialVelocity by remember { mutableStateOf(100.0) }
    var launchAngle by remember { mutableStateOf(45.0) }
    var launchAzimuth by remember { mutableStateOf(0.0) }
    var initialHeight by remember { mutableStateOf(2.0) }

    var showInputPanel by remember { mutableStateOf(true) }
    var currentX by remember { mutableStateOf(0.0) }
    var currentY by remember { mutableStateOf(0.0) }
    var currentZ by remember { mutableStateOf(0.0) }

    // Selected plane for the 2D chart
    var selectedPlane by remember { mutableStateOf(ViewPlane.XY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Input parameters panel
        AnimatedVisibility(
            visible = showInputPanel,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Simulation Parameters",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrajectoryColors.Purple
                        )
                        IconButton(onClick = { showInputPanel = false }) {
                            Icon(Icons.Default.KeyboardArrowUp, "Hide")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ParameterCard(
                            label = "Initial Velocity",
                            value = initialVelocity,
                            unit = "m/s",
                            range = 10.0..500.0,
                            onValueChange = { initialVelocity = it },
                            modifier = Modifier.weight(1f)
                        )

                        ParameterCard(
                            label = "Launch Angle",
                            value = launchAngle,
                            unit = "°",
                            range = 0.0..90.0,
                            onValueChange = { launchAngle = it },
                            modifier = Modifier.weight(1f)
                        )

                        ParameterCard(
                            label = "Launch Azimuth",
                            value = launchAzimuth,
                            unit = "°",
                            range = 0.0..360.0,
                            onValueChange = { launchAzimuth = it },
                            modifier = Modifier.weight(1f)
                        )

                        ParameterCard(
                            label = "Initial Height",
                            value = initialHeight,
                            unit = "m",
                            range = 0.0..100.0,
                            onValueChange = { initialHeight = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            animationJob?.cancel()
                            isPlaying = false
                            trajectoryResult = calculator.calculateTrajectory(
                                projectile = projectileData,
                                environment = environmentData,
                                initialVelocity = initialVelocity,
                                launchElevation = launchAngle,
                                launchAzimuth = launchAzimuth,
                                initialHeight = initialHeight
                            )
                            currentTimeIndex = 0
                            trajectoryResult?.let { result ->
                                dataManager.saveTrajectory(result)
                                if (result.points.isNotEmpty()) {
                                    currentX = result.points[0].x
                                    currentY = result.points[0].y
                                    currentZ = result.points[0].z
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrajectoryColors.Purple
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, "Calculate")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate Trajectory")
                    }
                }
            }
        }

        if (!showInputPanel) {
            Button(
                onClick = { showInputPanel = true },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrajectoryColors.Purple.copy(alpha = 0.8f)
                )
            ) {
                Icon(Icons.Default.KeyboardArrowDown, "Show Parameters")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Show Parameters")
            }
        }

        if (trajectoryResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatisticsRow(trajectoryResult!!)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Plane selector
                    PlaneSelector(
                        selected = selectedPlane,
                        onSelect = { selectedPlane = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SafeTrajectoryChart(
                        result = trajectoryResult!!,
                        currentPoint = if (isPlaying || currentTimeIndex > 0)
                            trajectoryResult!!.points.getOrNull(currentTimeIndex) else null,
                        plane = selectedPlane
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimationControls(
                        isPlaying = isPlaying,
                        currentTimeIndex = currentTimeIndex,
                        totalPoints = trajectoryResult!!.points.size,
                        currentX = currentX,
                        currentY = currentY,
                        currentZ = currentZ,
                        onPlayPause = {
                            if (isPlaying) {
                                animationJob?.cancel()
                                isPlaying = false
                            } else {
                                animationJob = scope.launch {
                                    for (i in currentTimeIndex until trajectoryResult!!.points.size) {
                                        currentTimeIndex = i
                                        currentX = trajectoryResult!!.points[i].x
                                        currentY = trajectoryResult!!.points[i].y
                                        currentZ = trajectoryResult!!.points[i].z
                                        delay(16)
                                    }
                                    isPlaying = false
                                }
                                isPlaying = true
                            }
                        },
                        onReset = {
                            animationJob?.cancel()
                            isPlaying = false
                            currentTimeIndex = 0
                            currentX = trajectoryResult!!.points[0].x
                            currentY = trajectoryResult!!.points[0].y
                            currentZ = trajectoryResult!!.points[0].z
                        },
                        onEnd = {
                            animationJob?.cancel()
                            isPlaying = false
                            currentTimeIndex = trajectoryResult!!.points.size - 1
                            val lastPoint = trajectoryResult!!.points.last()
                            currentX = lastPoint.x
                            currentY = lastPoint.y
                            currentZ = lastPoint.z
                        },
                        onSliderChange = { value ->
                            animationJob?.cancel()
                            isPlaying = false
                            currentTimeIndex = value
                            currentX = trajectoryResult!!.points[value].x
                            currentY = trajectoryResult!!.points[value].y
                            currentZ = trajectoryResult!!.points[value].z
                        }
                    )
                }
            }
        }
    }
}

/** Tab-style selector for the three orthogonal view planes. */
@Composable
fun PlaneSelector(
    selected: ViewPlane,
    onSelect: (ViewPlane) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TrajectoryColors.Background, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ViewPlane.entries.forEach { plane ->
            val isSelected = plane == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) TrajectoryColors.Purple else Color.Transparent)
                    .clickable { onSelect(plane) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = plane.label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else TrajectoryColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun SafeTrajectoryChart(
    result: TrajectoryResult,
    currentPoint: TrajectoryPoint?,
    plane: ViewPlane
) {
    val points = result.points

    // Compute bounds for the selected plane's two axes
    val horizValues = points.map { it.componentsFor(plane).first }
    val vertValues = points.map { it.componentsFor(plane).second }

    val minH = horizValues.minOrNull() ?: 0.0
    val maxH = horizValues.maxOrNull() ?: 1.0
    val minV = vertValues.minOrNull() ?: 0.0
    val maxV = vertValues.maxOrNull() ?: 1.0

    // Range with safe fallback, and include 0 in range for axes that can go negative (Z, etc.)
    val rangeH = (maxH - minH).let { if (it <= 0) 100.0 else it }
    val rangeV = (maxV - minV).let { if (it <= 0) 50.0 else it }

    val originH = min(0.0, minH)
    val originV = min(0.0, minV)
    val spanH = max(maxH, 0.0) - originH
    val spanV = max(maxV, 0.0) - originV

    val safeSpanH = if (spanH <= 0) 100.0 else spanH
    val safeSpanV = if (spanV <= 0) 50.0 else spanV

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Y-axis title
            Text(
                text = plane.yLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TrajectoryColors.TextPrimary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .rotate(-90f)
                    .padding(end = 8.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, TrajectoryColors.Divider, RoundedCornerShape(8.dp))
                ) {
                    val padding = 60f
                    val graphWidth = size.width - 2 * padding
                    val graphHeight = size.height - 2 * padding

                    if (graphWidth > 10 && graphHeight > 10) {
                        drawSafeGridLines(graphWidth, graphHeight, padding)
                        drawSafeAxes(graphWidth, graphHeight, padding, originH, originV, safeSpanH, safeSpanV)

                        if (points.isNotEmpty()) {
                            val path = Path().apply {
                                points.forEachIndexed { index, point ->
                                    val (h, v) = point.componentsFor(plane)
                                    val xPos = padding + ((h - originH) / safeSpanH).toFloat() * graphWidth
                                    val yPos = size.height - padding - ((v - originV) / safeSpanV).toFloat() * graphHeight

                                    val safeX = xPos.coerceIn(padding, size.width - padding)
                                    val safeY = yPos.coerceIn(padding, size.height - padding)

                                    if (index == 0) moveTo(safeX, safeY)
                                    else lineTo(safeX, safeY)
                                }
                            }

                            drawPath(
                                path = path,
                                color = TrajectoryColors.Purple,
                                style = Stroke(width = 3f)
                            )

                            currentPoint?.let { point ->
                                val (h, v) = point.componentsFor(plane)
                                val xPos = padding + ((h - originH) / safeSpanH).toFloat() * graphWidth
                                val yPos = size.height - padding - ((v - originV) / safeSpanV).toFloat() * graphHeight

                                val safeX = xPos.coerceIn(padding, size.width - padding)
                                val safeY = yPos.coerceIn(padding, size.height - padding)

                                drawCircle(
                                    color = TrajectoryColors.LimeGreen,
                                    radius = 8f,
                                    center = Offset(safeX, safeY)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 4f,
                                    center = Offset(safeX, safeY)
                                )
                            }
                        }
                    }
                }

                // X-axis labels
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .offset(y = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0..4) {
                        val value = (originH + (i / 4f) * safeSpanH).toInt()
                        Text(
                            text = value.toString(),
                            fontSize = 10.sp,
                            color = TrajectoryColors.TextSecondary
                        )
                    }
                }

                // Y-axis labels
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .offset(x = (-36).dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0..4) {
                        val value = (originV + (4 - i) / 4f * safeSpanV).toInt()
                        Text(
                            text = value.toString(),
                            fontSize = 10.sp,
                            color = TrajectoryColors.TextSecondary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // X-axis title
        Text(
            text = plane.xLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TrajectoryColors.TextPrimary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}

private fun DrawScope.drawSafeGridLines(
    graphWidth: Float,
    graphHeight: Float,
    padding: Float
) {
    val gridColor = Color(0xFFE0E0E0)

    for (i in 0..4) {
        val x = padding + (i / 4f) * graphWidth
        if (x in padding..(size.width - padding)) {
            drawLine(
                color = gridColor,
                start = Offset(x, padding),
                end = Offset(x, size.height - padding),
                strokeWidth = 1f
            )
        }
    }

    for (i in 0..4) {
        val y = padding + (i / 4f) * graphHeight
        val startY = size.height - y
        if (startY in padding..(size.height - padding)) {
            drawLine(
                color = gridColor,
                start = Offset(padding, startY),
                end = Offset(size.width - padding, startY),
                strokeWidth = 1f
            )
        }
    }
}

/**
 * Draws axes, placing the origin (0,0) at its correct position within the
 * [originH, originH+spanH] x [originV, originV+spanV] data range — important
 * for planes like XZ/YZ where values can be negative.
 */
private fun DrawScope.drawSafeAxes(
    graphWidth: Float,
    graphHeight: Float,
    padding: Float,
    originH: Double,
    originV: Double,
    spanH: Double,
    spanV: Double
) {
    val left = padding
    val right = size.width - padding
    val bottom = size.height - padding
    val top = padding

    // Position of value 0 along each axis (clamped into view)
    val zeroXFrac = ((0.0 - originH) / spanH).toFloat().coerceIn(0f, 1f)
    val zeroYFrac = ((0.0 - originV) / spanV).toFloat().coerceIn(0f, 1f)

    val zeroX = left + zeroXFrac * graphWidth
    val zeroY = bottom - zeroYFrac * graphHeight

    // Horizontal axis (along X direction of the plot, at y = 0 if in range)
    drawLine(
        color = Color.Black,
        start = Offset(left, zeroY),
        end = Offset(right, zeroY),
        strokeWidth = 2f
    )
    drawLine(color = Color.Black, start = Offset(right - 12, zeroY - 6), end = Offset(right, zeroY), strokeWidth = 2f)
    drawLine(color = Color.Black, start = Offset(right - 12, zeroY + 6), end = Offset(right, zeroY), strokeWidth = 2f)

    // Vertical axis (at x = 0 if in range)
    drawLine(
        color = Color.Black,
        start = Offset(zeroX, bottom),
        end = Offset(zeroX, top),
        strokeWidth = 2f
    )
    drawLine(color = Color.Black, start = Offset(zeroX - 6, top + 12), end = Offset(zeroX, top), strokeWidth = 2f)
    drawLine(color = Color.Black, start = Offset(zeroX + 6, top + 12), end = Offset(zeroX, top), strokeWidth = 2f)
}

@Composable
fun ParameterCard(
    label: String,
    value: Double,
    unit: String,
    range: ClosedFloatingPointRange<Double>,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            fontSize = 12.sp,
            color = TrajectoryColors.TextSecondary
        )

        OutlinedTextField(
            value = value.toInt().toString(),
            onValueChange = { text ->
                text.toDoubleOrNull()?.let {
                    if (it in range) onValueChange(it)
                }
            },
            trailingIcon = { Text(unit, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TrajectoryColors.Purple
            )
        )

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = TrajectoryColors.Purple,
                activeTrackColor = TrajectoryColors.Purple
            )
        )
    }
}

@Composable
fun StatisticsRow(result: TrajectoryResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TrajectoryColors.Background, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatisticCard("Max Distance", "${result.maxDistance.toInt()} m")
        StatisticCard("Max Height", "${result.maxHeight.toInt()} m")
        StatisticCard("Time of Flight", "${result.timeOfFlight.toInt()} s")
        StatisticCard("Impact Velocity", "${result.impactVelocity.toInt()} m/s")
    }
}

@Composable
fun StatisticCard(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            fontSize = 11.sp,
            color = TrajectoryColors.TextSecondary
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TrajectoryColors.Purple,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun AnimationControls(
    isPlaying: Boolean,
    currentTimeIndex: Int,
    totalPoints: Int,
    currentX: Double,
    currentY: Double,
    currentZ: Double,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
    onEnd: () -> Unit,
    onSliderChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TrajectoryColors.Background, RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CoordinateDisplay("Position X", "${currentX.toInt()} m")
            CoordinateDisplay("Position Y", "${currentY.toInt()} m")
            CoordinateDisplay("Position Z", "${currentZ.toInt()} m")
            CoordinateDisplay("Progress", "${if (totalPoints > 0) (currentTimeIndex * 100 / totalPoints) else 0}%")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = currentTimeIndex.toFloat(),
            onValueChange = { onSliderChange(it.toInt()) },
            valueRange = 0f..(totalPoints - 1).coerceAtLeast(1).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = TrajectoryColors.Purple,
                activeTrackColor = TrajectoryColors.Purple
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onReset, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.FirstPage, "Reset", tint = TrajectoryColors.Purple)
            }

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(56.dp)
                    .background(TrajectoryColors.Purple, CircleShape)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = onEnd, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.LastPage, "End", tint = TrajectoryColors.Purple)
            }
        }
    }
}

@Composable
fun CoordinateDisplay(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = TrajectoryColors.TextSecondary)
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TrajectoryColors.Purple,
            fontFamily = FontFamily.Monospace
        )
    }
}

// ==================== COMPACT UI COMPONENTS ====================

@Composable
fun CompactParam(
    label: String,
    value: Double,
    unit: String,
    onChange: (Double) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TrajectoryColors.TextSecondary
        )
        OutlinedTextField(
            value = value.toInt().toString(),
            onValueChange = { text ->
                text.toDoubleOrNull()?.let { onChange(it) }
            },
            modifier = Modifier.width(78.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
        )
    }
}

@Composable
fun CompactEnvParam(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TrajectoryColors.TextSecondary
        )
        OutlinedTextField(
            value = String.format("%.2f", value),
            onValueChange = { text ->
                text.toDoubleOrNull()?.let { onValueChange(it) }
            },
            modifier = Modifier.width(78.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
        )
    }
}