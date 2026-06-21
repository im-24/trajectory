// ui/screens/ThreeDSimulationScreen.kt
package ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.mappers.TrajectoryMapper
import data.models.TrajectoryPoint
import data.models.TrajectoryResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.presentation.ui.them.TrajectoryColors
import physics.TrajectoryCalculator
import presentation.viewmodels.HomeViewModel
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeDSimulationScreen(viewModel: HomeViewModel) {
    val scope = rememberCoroutineScope()
    val calculator = remember { TrajectoryCalculator() }
    val mapper = remember { TrajectoryMapper() }

    // Convert domain models to data models for the calculator
    val projectileData = remember(viewModel.projectile) { mapper.toDataProjectile(viewModel.projectile) }
    val environmentData = remember(viewModel.environment) { mapper.toDataEnvironment(viewModel.environment) }

    var trajectoryResult by remember { mutableStateOf<TrajectoryResult?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTimeIndex by remember { mutableStateOf(0) }
    var animationJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Parameters
    var initialVelocity by remember { mutableStateOf(50.0) }
    var launchAngle by remember { mutableStateOf(45.0) }
    var initialHeight by remember { mutableStateOf(0.0) }
    var launchAzimuth by remember { mutableStateOf(0.0) }

    // 3D Camera (controlled via direct touch interaction now)
    var rotX by remember { mutableStateOf(25f) }
    var rotY by remember { mutableStateOf(45f) }
    var zoom by remember { mutableStateOf(8f) }

    // Real-time coordinates
    var currentX by remember { mutableStateOf(0.0) }
    var currentY by remember { mutableStateOf(0.0) }
    var currentZ by remember { mutableStateOf(0.0) }

    // UI state
    var showControlPanel by remember { mutableStateOf(true) }
    var showDashboard by remember { mutableStateOf(true) }

    // Auto-run a default trajectory on first load
    LaunchedEffect(viewModel.projectile, viewModel.environment) {
        trajectoryResult = calculator.calculateTrajectory(
            projectile = projectileData,  // Use data model
            environment = environmentData,  // Use data model
            initialVelocity = initialVelocity,
            launchElevation = launchAngle,
            launchAzimuth = launchAzimuth,
            initialHeight = initialHeight
        )
        trajectoryResult?.let { result ->
            if (result.points.isNotEmpty()) {
                currentX = result.points[0].x
                currentY = result.points[0].y
                currentZ = result.points[0].z
            }
        }
    }

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
            Text(
                "3D Trajectory Simulation",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TrajectoryColors.Purple
            )

            IconButton(onClick = { showControlPanel = !showControlPanel }) {
                Icon(
                    if (showControlPanel) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    "Toggle Controls",
                    tint = TrajectoryColors.Purple
                )
            }
        }

        // 3D View - Fixed size at top, now interactive
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
                    zoom = zoom,
                    onRotate = { dx, dy ->
                        rotY = (rotY + dx).let {
                            var v = it
                            while (v > 180f) v -= 360f
                            while (v < -180f) v += 360f
                            v
                        }
                        rotX = (rotX - dy).coerceIn(-90f, 90f)
                    },
                    onZoom = { factor ->
                        zoom = (zoom / factor).coerceIn(3f, 20f)
                    }
                )

                // Reset view button
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    IconButton(
                        onClick = {
                            rotX = 25f
                            rotY = 45f
                            zoom = 8f
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.LockReset,
                            "Reset View",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Hint text
                Text(
                    text = "Drag to rotate • Pinch / scroll to zoom",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Parameters row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom
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
                                    projectile = projectileData,  // Use data model
                                    environment = environmentData,  // Use data model
                                    initialVelocity = initialVelocity,
                                    launchElevation = launchAngle,
                                    launchAzimuth = launchAzimuth,
                                    initialHeight = initialHeight
                                )
                                currentTimeIndex = 0
                                trajectoryResult?.let { result ->
                                    if (result.points.isNotEmpty()) {
                                        currentX = result.points[0].x
                                        currentY = result.points[0].y
                                        currentZ = result.points[0].z
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TrajectoryColors.Purple),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, "Run", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Statistics and Results
        trajectoryResult?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Statistics row
                    StatisticsRow3D(result)

                    Spacer(modifier = Modifier.height(8.dp))

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
                                totalPoints = result.points.size
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Animation controls
                            AnimationControls3D(
                                isPlaying = isPlaying,
                                currentTimeIndex = currentTimeIndex,
                                totalPoints = result.points.size,
                                onPlayPause = {
                                    if (isPlaying) {
                                        animationJob?.cancel()
                                        isPlaying = false
                                    } else {
                                        animationJob = scope.launch {
                                            for (i in currentTimeIndex until result.points.size) {
                                                currentTimeIndex = i
                                                currentX = result.points[i].x
                                                currentY = result.points[i].y
                                                currentZ = result.points[i].z
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
                                    currentX = result.points[0].x
                                    currentY = result.points[0].y
                                    currentZ = result.points[0].z
                                },
                                onEnd = {
                                    animationJob?.cancel()
                                    isPlaying = false
                                    currentTimeIndex = result.points.size - 1
                                    val lastPoint = result.points.last()
                                    currentX = lastPoint.x
                                    currentY = lastPoint.y
                                    currentZ = lastPoint.z
                                },
                                onSliderChange = { value ->
                                    animationJob?.cancel()
                                    isPlaying = false
                                    currentTimeIndex = value
                                    currentX = result.points[value].x
                                    currentY = result.points[value].y
                                    currentZ = result.points[value].z
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
            Text(label, fontSize = 11.sp, color = TrajectoryColors.TextSecondary)
            Text("${value.toInt()} $unit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TrajectoryColors.Purple)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = TrajectoryColors.Purple,
                activeTrackColor = TrajectoryColors.Purple,
            )
        )
    }
}

/**
 * 3D Canvas with direct touch interaction:
 * - Drag: rotate camera (rotX / rotY)
 * - Pinch (or ctrl+scroll on desktop via transform gesture): zoom
 */
@Composable
fun ThreeDCanvas(
    result: TrajectoryResult?,
    currentPoint: TrajectoryPoint?,
    rotX: Float,
    rotY: Float,
    zoom: Float,
    onRotate: (dx: Float, dy: Float) -> Unit,
    onZoom: (factor: Float) -> Unit
) {
    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    val points = result?.points ?: emptyList()

    // Find bounds for scaling
    val maxX = if (points.isNotEmpty()) points.maxOfOrNull { abs(it.x) } ?: 10.0 else 10.0
    val maxY = if (points.isNotEmpty()) points.maxOfOrNull { it.y } ?: 8.0 else 8.0
    val maxZ = if (points.isNotEmpty()) points.maxOfOrNull { abs(it.z) } ?: 10.0 else 10.0
    val maxHorizontal = max(maxX, maxZ)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                canvasSize = androidx.compose.ui.geometry.Size(size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    onRotate(dragAmount.x * 0.4f, dragAmount.y * 0.4f)
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    if (gestureZoom != 1f) {
                        onZoom(gestureZoom)
                    } else {
                        onRotate(pan.x * 0.4f, pan.y * 0.4f)
                    }
                }
            }
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val scale = min(size.width, size.height) * 0.35f / max(zoom, 1f)

        drawGrid3D(centerX, centerY, scale, rotX, rotY, maxHorizontal)
        draw3DAxes(centerX, centerY, scale, rotX, rotY)

        if (points.isEmpty()) {
            drawCircle(
                color = Color.Gray.copy(alpha = 0.2f),
                radius = 80f,
                center = Offset(centerX, centerY)
            )
        } else {
            val path = Path()
            var isValidPath = false

            points.forEachIndexed { index, point ->
                val xPos = (point.x / maxHorizontal * 8f).toFloat()
                val yPos = (point.y / maxY * 6f).toFloat()
                val zPos = (point.z / maxHorizontal * 8f).toFloat()

                val (screenX, screenY) = project3DTo2D(xPos, yPos, zPos, rotX, rotY, centerX, centerY, scale)

                if (screenX.isFinite() && screenY.isFinite() &&
                    screenX >= 0 && screenX <= size.width &&
                    screenY >= 0 && screenY <= size.height) {
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

            currentPoint?.let { point ->
                val xPos = (point.x / maxHorizontal * 8f).toFloat()
                val yPos = (point.y / maxY * 6f).toFloat()
                val zPos = (point.z / maxHorizontal * 8f).toFloat()

                val (screenX, screenY) = project3DTo2D(xPos, yPos, zPos, rotX, rotY, centerX, centerY, scale)

                if (screenX.isFinite() && screenY.isFinite() &&
                    screenX >= 0 && screenX <= size.width &&
                    screenY >= 0 && screenY <= size.height) {
                    drawCircle(
                        color = TrajectoryColors.LimeGreen.copy(alpha = 0.3f),
                        radius = 16f,
                        center = Offset(screenX, screenY)
                    )
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

        drawOrthonormalBasisGizmo(rotX, rotY, size.width, size.height)
    }
}

/**
 * Draws a small fixed-position orthonormal basis (X/Y/Z axes) gizmo
 * in the bottom-left corner, rotating with the camera but independent of scene scale.
 */
private fun DrawScope.drawOrthonormalBasisGizmo(
    rotX: Float,
    rotY: Float,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val gizmoRadius = 36f
    val gizmoCenter = Offset(gizmoRadius + 24f, canvasHeight - gizmoRadius - 24f)
    val axisLen = gizmoRadius

    drawCircle(
        color = Color.White.copy(alpha = 0.06f),
        radius = gizmoRadius + 8f,
        center = gizmoCenter
    )

    fun project(x: Float, y: Float, z: Float): Offset {
        val (sx, sy) = project3DTo2D(x, y, z, rotX, rotY, gizmoCenter.x, gizmoCenter.y, axisLen)
        return Offset(sx, sy)
    }

    val origin = gizmoCenter

    val xEnd = project(1f, 0f, 0f)
    drawLine(Color.Red, origin, xEnd, 2.5f)
    drawCircle(Color.Red, 4f, xEnd)

    val yEnd = project(0f, 1f, 0f)
    drawLine(Color.Green, origin, yEnd, 2.5f)
    drawCircle(Color.Green, 4f, yEnd)

    val zEnd = project(0f, 0f, 1f)
    drawLine(Color.Blue, origin, zEnd, 2.5f)
    drawCircle(Color.Blue, 4f, zEnd)

    drawCircle(Color.White.copy(alpha = 0.6f), 2.5f, origin)
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

    for (i in -gridSize..gridSize step 2) {
        val valF = i.toFloat()

        val (startX, startY) = project3DTo2D(valF, 0f, -gridSize.toFloat(), rotX, rotY, centerX, centerY, scale)
        val (endX, endY) = project3DTo2D(valF, 0f, gridSize.toFloat(), rotX, rotY, centerX, centerY, scale)

        if (startX.isFinite() && startY.isFinite() && endX.isFinite() && endY.isFinite()) {
            drawLine(gridColor, Offset(startX, startY), Offset(endX, endY), 0.6f)
        }

        val (start2X, start2Y) = project3DTo2D(-gridSize.toFloat(), 0f, valF, rotX, rotY, centerX, centerY, scale)
        val (end2X, end2Y) = project3DTo2D(gridSize.toFloat(), 0f, valF, rotX, rotY, centerX, centerY, scale)

        if (start2X.isFinite() && start2Y.isFinite() && end2X.isFinite() && end2Y.isFinite()) {
            drawLine(gridColor, Offset(start2X, start2Y), Offset(end2X, end2Y), 0.6f)
        }
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

    val (xEndX, xEndY) = project3DTo2D(axisLength, 0f, 0f, rotX, rotY, centerX, centerY, scale)
    if (xEndX.isFinite() && xEndY.isFinite()) {
        drawLine(Color.Red, Offset(centerX, centerY), Offset(xEndX, xEndY), 2.5f)
        drawCircle(Color.Red.copy(alpha = 0.6f), 6f, Offset(xEndX, xEndY))
    }

    val (yEndX, yEndY) = project3DTo2D(0f, axisLength, 0f, rotX, rotY, centerX, centerY, scale)
    if (yEndX.isFinite() && yEndY.isFinite()) {
        drawLine(Color.Green, Offset(centerX, centerY), Offset(yEndX, yEndY), 2.5f)
        drawCircle(Color.Green.copy(alpha = 0.6f), 6f, Offset(yEndX, yEndY))
    }

    val (zEndX, zEndY) = project3DTo2D(0f, 0f, axisLength, rotX, rotY, centerX, centerY, scale)
    if (zEndX.isFinite() && zEndY.isFinite()) {
        drawLine(Color.Blue, Offset(centerX, centerY), Offset(zEndX, zEndY), 2.5f)
        drawCircle(Color.Blue.copy(alpha = 0.6f), 6f, Offset(zEndX, zEndY))
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

    val rxRad = rotX * PI.toFloat() / 180f
    val cosX = cos(rxRad)
    val sinX = sin(rxRad)
    val newY = py * cosX - pz * sinX
    val newZ = py * sinX + pz * cosX
    py = newY
    pz = newZ

    val ryRad = rotY * PI.toFloat() / 180f
    val cosY = cos(ryRad)
    val sinY = sin(ryRad)
    val newX = px * cosY + pz * sinY

    val screenX = centerX + newX * scale
    val screenY = centerY - py * scale

    return screenX to screenY
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
        modifier = Modifier.padding(4.dp)
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