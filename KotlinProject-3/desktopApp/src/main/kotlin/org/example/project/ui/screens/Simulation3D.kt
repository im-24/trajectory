// ui/screens/ThreeDSimulationScreen.kt
package ui.screens

import HomeViewModel
import TrajectoryColors
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.models.TrajectoryPoint
import data.models.TrajectoryResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import physics.TrajectoryCalculator
import kotlin.math.*

@Composable
fun ThreeDSimulationScreen(viewModel: HomeViewModel) {
    val scope = rememberCoroutineScope()
    val calculator = remember { TrajectoryCalculator() }

    var trajectoryResult by remember { mutableStateOf<TrajectoryResult?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTimeIndex by remember { mutableStateOf(0) }
    var animationJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Parameters
    var initialVelocity by remember { mutableStateOf(50.0) }
    var launchAngle by remember { mutableStateOf(45.0) }
    var initialHeight by remember { mutableStateOf(0.0) }

    // 3D Camera (with proper limits)
    var rotX by remember { mutableStateOf(25f) }
    var rotY by remember { mutableStateOf(45f) }
    var zoom by remember { mutableStateOf(8f) } // Start at reasonable value

    // Real-time coordinates
    var currentX by remember { mutableStateOf(0.0) }
    var currentY by remember { mutableStateOf(0.0) }
    var currentZ by remember { mutableStateOf(0.0) }

    var launchAzimuth by remember { mutableStateOf(0.0) }  // 0° = along X axis
    // UI state
    var showControlPanel by remember { mutableStateOf(true) }
    var showDashboard by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            // 3D View - Fixed size at top
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ThreeDCanvas(
                        result = trajectoryResult,
                        currentPoint = if (isPlaying || currentTimeIndex > 0)
                            trajectoryResult?.points?.getOrNull(currentTimeIndex) else null,
                        rotX = rotX,
                        rotY = rotY,
                        zoom = zoom
                    )

                    // Zoom indicators
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        IconButton(
                            onClick = { zoom = min(zoom + 0.5f, 20f) },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.ZoomIn, "Zoom In", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { zoom = max(zoom - 0.5f, 3f) },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.ZoomOut, "Zoom Out", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = {
                                rotX = 25f
                                rotY = 45f
                                zoom = 8f
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.LockReset,
                                "Reset View",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Control Panel (compact, below 3D view)
            AnimatedVisibility(
                visible = showControlPanel,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Parameters row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompactParameter(
                                label = "Azimuth",
                                value = launchAzimuth,
                                unit = "°",
                                range = 0.0..360.0,
                                onValueChange = { launchAzimuth = it },
                                modifier = Modifier.weight(1f)
                            )
                            CompactParameter(
                                label = "Velocity",
                                value = initialVelocity,
                                unit = "m/s",
                                range = 0.0..200.0,
                                onValueChange = { initialVelocity = it },
                                modifier = Modifier.weight(1f)
                            )

                            CompactParameter(
                                label = "Launch Angle",
                                value = launchAngle,
                                unit = "°",
                                range = 0.0..90.0,
                                onValueChange = { launchAngle = it },
                                modifier = Modifier.weight(1f)
                            )

                            CompactParameter(
                                label = "Initial Height",
                                value = initialHeight,
                                unit = "m",
                                range = 0.0..50.0,
                                onValueChange = { initialHeight = it },
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    animationJob?.cancel()
                                    isPlaying = false
                                    trajectoryResult = calculator.calculateTrajectory(
                                        projectile = viewModel.projectile,
                                        environment = viewModel.environment,
                                        initialVelocity = initialVelocity,
                                        launchElevation = launchAngle,      // reuse variable or rename
                                        launchAzimuth = launchAzimuth,
                                        initialHeight = initialHeight
                                    )
                                    currentTimeIndex = 0
                                    trajectoryResult?.let { result ->
                                        if (result.points.isNotEmpty()) {
                                            currentX = result.points[0].x
                                            currentY = result.points[0].y
                                            currentZ = 0.0
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TrajectoryColors.Purple),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, "Run", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run", fontSize = 12.sp)
                            }
                        }


                        // Camera controls row (compact)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Camera:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TrajectoryColors.TextSecondary
                            )

                            CompactSlider("Rot X", rotX, -90f..90f, { rotX = it }, Modifier.weight(1f))
                            CompactSlider("Rot Y", rotY, -180f..180f, { rotY = it }, Modifier.weight(1f))
                            CompactSlider("Zoom", zoom, 3f..20f, { zoom = it }, Modifier.weight(1f))

                            CameraPresetButton("Reset") {
                                rotX = 25f
                                rotY = 45f
                                zoom = 8f
                            }
                            CameraPresetButton("Top") {
                                rotX = 90f
                                rotY = 0f
                                zoom = 10f
                            }
                            CameraPresetButton("Front") {
                                rotX = 0f
                                rotY = 0f
                                zoom = 12f
                            }
                            CameraPresetButton("Side") {
                                rotX = 0f
                                rotY = 90f
                                zoom = 12f
                            }
                        }
                    }
                }
            }

            // Statistics and Results
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Statistics row
                    StatisticsRow3D(trajectoryResult!!)


                    // Dashboard with real-time coordinates and animation controls
                    AnimatedVisibility(
                        visible = showDashboard,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            // Real-time coordinates
                            RealTimeCoordinates(
                                currentX = currentX,
                                currentY = currentY,
                                currentZ = currentZ,
                                currentTimeIndex = currentTimeIndex,
                                totalPoints = trajectoryResult!!.points.size
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Animation controls
                            AnimationControls3D(
                                isPlaying = isPlaying,
                                currentTimeIndex = currentTimeIndex,
                                totalPoints = trajectoryResult!!.points.size,
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
                                                currentZ = 0.0
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
                                    currentZ = 0.0
                                },
                                onEnd = {
                                    animationJob?.cancel()
                                    isPlaying = false
                                    currentTimeIndex = trajectoryResult!!.points.size - 1
                                    val lastPoint = trajectoryResult!!.points.last()
                                    currentX = lastPoint.x
                                    currentY = lastPoint.y
                                    currentZ = 0.0
                                },
                                onSliderChange = { value ->
                                    animationJob?.cancel()
                                    isPlaying = false
                                    currentTimeIndex = value
                                    currentX = trajectoryResult!!.points[value].x
                                    currentY = trajectoryResult!!.points[value].y
                                    currentZ = 0.0
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactParameter(
    label: String,
    value: Double,
    unit: String,
    range: ClosedFloatingPointRange<Double>,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, fontSize = 10.sp, color = TrajectoryColors.TextSecondary)
            Text("${value.toInt()} $unit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TrajectoryColors.Purple)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            modifier = Modifier.height(200.dp),
            colors = SliderDefaults.colors(
                thumbColor = TrajectoryColors.Purple,
                activeTrackColor = TrajectoryColors.Purple,
            )
        )
    }
}

@Composable
fun CompactSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("$label: ${value.toInt()}°", fontSize = 10.sp, color = TrajectoryColors.TextSecondary)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = TrajectoryColors.Purple,
                activeTrackColor = TrajectoryColors.Purple,
            )
        )
    }
}

@Composable
fun CameraPresetButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text(label, fontSize = 10.sp)
    }
}
@Composable
fun ThreeDCanvas(
    result: TrajectoryResult?,
    currentPoint: TrajectoryPoint?,
    rotX: Float,
    rotY: Float,
    zoom: Float
) {
    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    val points = result?.points ?: emptyList()

    // Find bounds for scaling
    val maxX = points.maxOfOrNull { abs(it.x) } ?: 10.0
    val maxY = points.maxOfOrNull { it.y } ?: 8.0
    val maxZ = points.maxOfOrNull { abs(it.z) } ?: 10.0
    val maxHorizontal = max(maxX, maxZ)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                canvasSize = androidx.compose.ui.geometry.Size(size.width.toFloat(), size.height.toFloat())
            }
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val scale = min(size.width, size.height) * 0.35f / (max(zoom, 1f))

        // Background grid
        drawGrid3D(centerX, centerY, scale, rotX, rotY, maxHorizontal)

        // 3D Axes
        draw3DAxes(centerX, centerY, scale, rotX, rotY)

        if (points.isEmpty()) {
            drawCenteredText("Click 'Run' to start 3D simulation", Offset(centerX, centerY))
            return@Canvas
        }

        // Draw Trajectory Path
        val path = Path()
        var isValidPath = false

        points.forEachIndexed { index, point ->
            val xPos = (point.x / maxHorizontal * 8f).toFloat()
            val yPos = (point.y / maxY * 6f).toFloat()
            val zPos = (point.z / maxHorizontal * 8f).toFloat()

            val (screenX, screenY) = project3DTo2D(xPos, yPos, zPos, rotX, rotY, centerX, centerY, scale)

            if (screenX.isFinite() && screenY.isFinite()) {
                if (index == 0) {
                    path.moveTo(screenX, screenY)
                    isValidPath = true
                } else {
                    path.lineTo(screenX, screenY)
                }
            }
        }

        if (isValidPath) {
            drawPath(
                path = path,
                color = TrajectoryColors.Purple,
                style = Stroke(width = 3.5f)
            )
        }

        // Draw current projectile position
        currentPoint?.let { point ->
            val xPos = (point.x / maxHorizontal * 8f).toFloat()
            val yPos = (point.y / maxY * 6f).toFloat()
            val zPos = (point.z / maxHorizontal * 8f).toFloat()

            val (screenX, screenY) = project3DTo2D(xPos, yPos, zPos, rotX, rotY, centerX, centerY, scale)

            if (screenX.isFinite() && screenY.isFinite()) {
                // Glow
                drawCircle(
                    color = TrajectoryColors.LimeGreen.copy(alpha = 0.25f),
                    radius = 16f,
                    center = Offset(screenX, screenY)
                )
                // Main dot
                drawCircle(
                    color = TrajectoryColors.LimeGreen,
                    radius = 9f,
                    center = Offset(screenX, screenY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(screenX, screenY)
                )
            }
        }
    }
}
private fun DrawScope.drawGrid3D(
    centerX: Float,
    centerY: Float,
    scale: Float,
    rotX: Float,
    rotY: Float,
    maxHorizontal: Double
) {
    val gridColor = Color(0xFF334155)
    val gridSize = 8
    val step = 2f

    for (i in -gridSize..gridSize step 2) {
        val valF = i.toFloat()

        // Lines parallel to X axis
        val start1 = project3DTo2D(valF, 0f, -gridSize.toFloat(), rotX, rotY, centerX, centerY, scale)
        val end1 = project3DTo2D(valF, 0f, gridSize.toFloat(), rotX, rotY, centerX, centerY, scale)
        drawLine(gridColor, Offset(start1.first, start1.second), Offset(end1.first, end1.second), 0.6f)

        // Lines parallel to Z axis
        val start2 = project3DTo2D(-gridSize.toFloat(), 0f, valF, rotX, rotY, centerX, centerY, scale)
        val end2 = project3DTo2D(gridSize.toFloat(), 0f, valF, rotX, rotY, centerX, centerY, scale)
        drawLine(gridColor, Offset(start2.first, start2.second), Offset(end2.first, end2.second), 0.6f)
    }
}

private fun DrawScope.draw3DAxes(
    centerX: Float,
    centerY: Float,
    scale: Float,
    rotX: Float,
    rotY: Float
) {
    val axisLength = 7f

    // X Axis (Red)
    val xEnd = project3DTo2D(axisLength, 0f, 0f, rotX, rotY, centerX, centerY, scale)
    drawLine(Color.Red, Offset(centerX, centerY), Offset(xEnd.first, xEnd.second), 2.5f)

    // Y Axis (Green - Up)
    val yEnd = project3DTo2D(0f, axisLength, 0f, rotX, rotY, centerX, centerY, scale)
    drawLine(Color.Green, Offset(centerX, centerY), Offset(yEnd.first, yEnd.second), 2.5f)

    // Z Axis (Blue)
    val zEnd = project3DTo2D(0f, 0f, axisLength, rotX, rotY, centerX, centerY, scale)
    drawLine(Color.Blue, Offset(centerX, centerY), Offset(zEnd.first, zEnd.second), 2.5f)

    // Labels
    drawCircle(Color.Red.copy(alpha = 0.6f), 6f, Offset(xEnd.first, xEnd.second))
    drawCircle(Color.Green.copy(alpha = 0.6f), 6f, Offset(yEnd.first, yEnd.second))
    drawCircle(Color.Blue.copy(alpha = 0.6f), 6f, Offset(zEnd.first, zEnd.second))
}
private fun rotate3D(x: Float, y: Float, z: Float, rotX: Float, rotY: Float): Pair<Float, Float> {
    var px = x
    var py = y
    var pz = z

    val rxRad = rotX * PI.toFloat() / 180f
    val ryRad = rotY * PI.toFloat() / 180f

    // Rotate around X axis
    val cosX = cos(rxRad)
    val sinX = sin(rxRad)
    val newY = py * cosX - pz * sinX
    val newZ = py * sinX + pz * cosX
    py = newY
    pz = newZ

    // Rotate around Y axis
    val cosY = cos(ryRad)
    val sinY = sin(ryRad)
    val newX = px * cosY + pz * sinY
    val newZ2 = -px * sinY + pz * cosY
    px = newX

    return px to py
}

private fun DrawScope.drawTextLabel(text: String, x: Float, y: Float, color: Color) {
    // Simple text drawing - in production, use proper text rendering
    drawCircle(color.copy(alpha = 0.5f), 8f, Offset(x, y))
}

private fun DrawScope.drawCenteredText(text: String, center: Offset) {
    // You can improve this with proper text rendering later
    drawCircle(Color.Gray.copy(alpha = 0.25f), 70f, center)
}

@Composable
fun StatisticsRow3D(result: TrajectoryResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TrajectoryColors.Background, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard3D("Max Distance", "${result.maxDistance.toInt()} m")
        StatCard3D("Max Height", "${result.maxHeight.toInt()} m")
        StatCard3D("Time of Flight", "${result.timeOfFlight.toInt()} s")
        StatCard3D("Impact Velocity", "${result.impactVelocity.toInt()} m/s")
    }
}

@Composable
fun StatCard3D(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = TrajectoryColors.TextSecondary)
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TrajectoryColors.Purple,
            fontFamily = FontFamily.Monospace
        )
    }
}

private fun project3DTo2D(
    x: Float, y: Float, z: Float,
    rotX: Float, rotY: Float,
    centerX: Float, centerY: Float,
    scale: Float
): Pair<Float, Float> {
    var px = x
    var py = y
    var pz = z

    // Rotation around X axis
    val rxRad = rotX * PI.toFloat() / 180f
    val cosX = cos(rxRad)
    val sinX = sin(rxRad)
    val newY = py * cosX - pz * sinX
    val newZ = py * sinX + pz * cosX
    py = newY
    pz = newZ

    // Rotation around Y axis
    val ryRad = rotY * PI.toFloat() / 180f
    val cosY = cos(ryRad)
    val sinY = sin(ryRad)
    val newX = px * cosY + pz * sinY
    // val newZ2 = -px * sinY + pz * cosY  // not needed for projection

    val screenX = centerX + newX * scale
    val screenY = centerY - py * scale   // Y is up

    return screenX to screenY
}
@Composable
fun RealTimeCoordinates(
    currentX: Double,
    currentY: Double,
    currentZ: Double,
    currentTimeIndex: Int,
    totalPoints: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TrajectoryColors.Background),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CoordinateCard("Position X", "${currentX.toInt()} m", Icons.Default.NavigateNext)
            CoordinateCard("Position Y", "${currentY.toInt()} m", Icons.Default.NavigateNext)
            CoordinateCard("Position Z", "${currentZ.toInt()} m", Icons.Default.NavigateNext)
            CoordinateCard("Progress", "${(currentTimeIndex * 100 / totalPoints)}%", Icons.Default.Timeline)
        }
    }
}

@Composable
fun CoordinateCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = TrajectoryColors.Purple)
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(label, fontSize = 10.sp, color = TrajectoryColors.TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TrajectoryColors.Purple, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun AnimationControls3D(
    isPlaying: Boolean,
    currentTimeIndex: Int,
    totalPoints: Int,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
    onEnd: () -> Unit,
    onSliderChange: (Int) -> Unit
) {
    Column {
        // Progress Slider
        Slider(
            value = currentTimeIndex.toFloat(),
            onValueChange = { onSliderChange(it.toInt()) },
            valueRange = 0f..(totalPoints - 1).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = TrajectoryColors.Purple,
                activeTrackColor = TrajectoryColors.Purple
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onReset,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.FirstPage, "Reset", tint = TrajectoryColors.Purple)
            }

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(56.dp)
                    .background(TrajectoryColors.Purple, CircleShape)
                    .shadow(4.dp, CircleShape)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onEnd,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.LastPage, "End", tint = TrajectoryColors.Purple)
            }
        }
    }
}