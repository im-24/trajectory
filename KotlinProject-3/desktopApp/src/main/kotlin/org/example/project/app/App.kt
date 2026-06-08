package org.example.project

import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun App() {
    fun saveexcel(
        xValues: List<Float>,
        yValues: List<Float>
    ){
        if (xValues.isEmpty()) return

        val vlues = mutableListOf<Pair<Float , Float>>()

        for (i in xValues.indices){
            vlues.add(Pair(xValues[i], yValues[i]))
        }

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet()

        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("X")
        header.createCell(1).setCellValue("Y")

        vlues.forEachIndexed { index, pair ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(pair.first.toString())
            row.createCell(1).setCellValue(pair.second.toString())
        }

        val fileOut = FileOutputStream("trajectory.xlsx")
        workbook.write(fileOut)
        fileOut.close()
        workbook.close()
    }

    fun exportWord(
        Velocity : Float,
        angle : Float,
        xValues : Float,
        yValues : Float,
    ) {
        val document = XWPFDocument()

        val paragraph = document.createParagraph()
        val run = paragraph.createRun()

        run.setText("Projectile Information")
        run.addBreak()
        run.setText("Velocity: $Velocity m/s")
        run.addBreak()
        run.setText("Angle: ${angle}°")
        run.addBreak()
        run.setText("Final Position: ($xValues, $yValues)")

        val out = FileOutputStream("report.docx")
        document.write(out)
        out.close()
        document.close()
    }

    var input_V by remember { mutableStateOf("") }
    var input_a by remember { mutableStateOf("") }

    var velocity_s by remember { mutableStateOf(0f) }
    var angle_s by remember { mutableStateOf(0f) }

    var time by remember { mutableStateOf(0f) }
    var start by remember { mutableStateOf(false) }
    var maxTime by remember { mutableStateOf(0f) }

    val velocityanm by animateFloatAsState(
        targetValue = velocity_s,
        animationSpec = tween(600),
        label = ""
    )

    val angleanm by animateFloatAsState(
        targetValue = angle_s,
        animationSpec = tween(600),
        label = ""
    )

    val g = 9.8f
    var Velocity by remember { mutableStateOf(0f) }
    var angle by remember { mutableStateOf(0f) }

    var priorit by remember { mutableStateOf(false) }

    fun getTrajectoryX(t: Float, v: Float, ang: Double): Float {
        return (v * kotlin.math.cos(ang) * t).toFloat()
    }

    fun getTrajectoryY(t: Float, v: Float, ang: Double): Float {
        val vy = v * kotlin.math.sin(ang)
        return (vy * t - 0.5f * g * t * t).toFloat()
    }

    // Calculate total flight time
    fun calculateMaxTime(v: Float, ang: Double): Float {
        val vy = v * kotlin.math.sin(ang)
        if (vy <= 0) return 0f
        return (2 * vy / g).toFloat()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    center = Offset.Zero,
                    radius = 900f,
                    colors = listOf(
                        Color(0xFFFAE4DF),
                        Color(0xFFFFC9CF),
                        Color(0xFFF3D1F2)
                    ),
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text(
                    "Velocity",
                    modifier = Modifier
                        .width(75.dp)
                        .size(24.dp),
                    fontSize = 16.sp
                )
                TextField(
                    value = input_V,
                    modifier = Modifier.width(200.dp),
                    onValueChange = { input_V = it
                        priorit = false
                        println("text changed ")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x65F8F0E7),
                        unfocusedContainerColor = Color(0xFFFFCDBE),
                        focusedTextColor = Color.DarkGray,
                        unfocusedTextColor = Color(0xFFFA704D),
                        focusedIndicatorColor = Color(0xFFFF8D13),
                        unfocusedIndicatorColor = Color(0xFFF88B31)
                    )
                )
                Slider(
                    value = velocity_s,
                    modifier = Modifier.width(300.dp),
                    onValueChange = {
                        velocity_s = it
                        priorit = true
                        println(priorit)
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFA704D),
                        activeTrackColor = Color(0xFFFA704D),
                        inactiveTrackColor = Color(0x65F8F0E7),
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text("Angle", modifier = Modifier.width(75.dp).size(24.dp), fontSize = 16.sp)
                TextField(value = input_a,
                    onValueChange = { input_a = it
                        priorit = false
                        println("text changed ")
                    },
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x65F8F0E7),
                        unfocusedContainerColor = Color(0xFFFFCDBE),
                        focusedTextColor = Color.DarkGray,
                        unfocusedTextColor = Color(0xFFFA704D),
                        focusedIndicatorColor = Color(0xFFFF8D13),
                        unfocusedIndicatorColor = Color(0xFFF88B31)
                    )
                )
                Slider(
                    value = angle_s,
                    modifier = Modifier.width(300.dp),
                    onValueChange = {
                        angle_s = it
                        priorit = true
                        println("slide changed ")
                    },
                    valueRange = 0f..90f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFA704D),
                        activeTrackColor = Color(0xFFFA704D),
                        inactiveTrackColor = Color(0x65F8F0E7),
                    )
                )
            }
        }

        Button(onClick = {
            if (input_V.isNotEmpty()) {
                Velocity = input_V.toFloat()
            }
            if (input_a.isNotEmpty()) {
                angle = input_a.toFloat()
            }
            if (priorit) {
                angle = angle_s
                Velocity = velocity_s
            }

            // Reset animation
            time = 0f
            start = true

            // Calculate max time for this trajectory
            val angRad = Math.toRadians(angle.toDouble())
            maxTime = calculateMaxTime(Velocity, angRad)
        },
            modifier = Modifier.padding(16.dp),
            enabled = true,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF88B31),
                contentColor = Color(0xFFFCF7F4),
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp,
                pressedElevation = 4.dp,
                disabledElevation = 0.dp,
                hoveredElevation = 6.dp,
            ),
            contentPadding = PaddingValues(16.dp)) {
            Text(text = "Simulation", fontSize = 14.sp)
        }

        // Animation loop
        LaunchedEffect(start, maxTime) {
            while (start && time <= maxTime) {
                delay(16)
                time += 0.05f

                if (time >= maxTime) {
                    start = false
                    val xValues = mutableListOf<Float>()
                    val yValues = mutableListOf<Float>()
                    var t = 0f
                    val angRad = Math.toRadians(angle.toDouble())
                    while (t <= maxTime) {
                        val x = getTrajectoryX(t, Velocity, angRad)
                        val y = getTrajectoryY(t, Velocity, angRad)
                        if (y >= 0) {
                            xValues.add(x)
                            yValues.add(y)
                        }
                        t += 0.05f
                    }

                    if (xValues.isNotEmpty()) {
                        saveexcel(xValues, yValues)
                        exportWord(
                            Velocity,
                            angle,
                            xValues.last(),
                            yValues.last()
                        )
                    }
                }
            }
        }

        Canvas(
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF6EBE6))
                .border(1.dp, Color(0xFFCDC2D5), RoundedCornerShape(10.dp))
        ) {
            var offset_trajectory = 30f
            val width = size.width.toInt()
            val height = size.height.toInt()
            val st = 50

            // grid
            for (x in offset_trajectory.toInt() until (width + offset_trajectory.toInt()) step st) {
                clipRect {
                    drawLine(
                        color = Color(0xFF999392),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), width.toFloat()),
                        strokeWidth = 0.5f
                    )
                }
            }
            for (y in offset_trajectory.toInt() until (height.toInt()) step (st)) {
                clipRect {
                    drawLine(
                        color = Color(0xFF999392),
                        start = Offset(0f, height - y.toFloat()),
                        end = Offset(width.toFloat(), height - y.toFloat()),
                        strokeWidth = 0.5f
                    )
                }
            }

            // axes y, x
            clipRect {
                drawLine(
                    color = Color(0xFF1D1C1C),
                    start = Offset(offset_trajectory, 0f),
                    end = Offset(offset_trajectory, height.toFloat() - offset_trajectory),
                    strokeWidth = 1f
                )
            }
            clipRect {
                drawLine(
                    color = Color(0xFF1D1C1C),
                    start = Offset(offset_trajectory, height - offset_trajectory),
                    end = Offset(width.toFloat(), height - offset_trajectory),
                    strokeWidth = 1f
                )
            }

            // preview trajectory test commit (dotted line)
            var tprev = 0f
            var currentprev: Offset? = null
            val angPrevRad = Math.toRadians(angleanm.toDouble())
            val maxTimePrev = calculateMaxTime(velocityanm, angPrevRad)

            while (tprev <= maxTimePrev) {
                val x = getTrajectoryX(tprev, velocityanm, angPrevRad)
                val y = getTrajectoryY(tprev, velocityanm, angPrevRad)
                val canvasY = height - y - offset_trajectory

                if (y >= 0 && canvasY >= 0 && canvasY <= height) {
                    currentprev?.let {
                        clipRect {
                            drawLine(
                                color = Color(0xFFA489C7),
                                start = currentprev,
                                end = Offset(x + offset_trajectory, canvasY),
                                strokeWidth = 2f
                            )
                        }
                    }
                    currentprev = Offset(x + offset_trajectory, canvasY)
                }
                tprev += 0.05f
            }

            // main trajectory (solid line)
            var t = 0f
            var current: Offset? = null
            val angRad = Math.toRadians(angle.toDouble())
            val maxT = calculateMaxTime(Velocity, angRad)

            while (t <= maxT) {
                val x = getTrajectoryX(t, Velocity, angRad)
                val y = getTrajectoryY(t, Velocity, angRad)
                val canvasY = height - y - offset_trajectory

                if (y >= 0 && canvasY >= 0 && canvasY <= height) {
                    current?.let {
                        clipRect {
                            drawLine(
                                color = Color(0xFF391965),
                                start = current,
                                end = Offset(x + offset_trajectory, canvasY),
                                strokeWidth = 2f
                            )
                        }
                    }
                    current = Offset(x + offset_trajectory, canvasY)
                }
                t += 0.05f
            }

            // Animated ball position
            val angRadAnim = Math.toRadians(angle.toDouble())
            val currentTime = if (start && time <= maxTime) time else 0f
            val ballX = getTrajectoryX(currentTime, Velocity, angRadAnim)
            val ballY = getTrajectoryY(currentTime, Velocity, angRadAnim)
            val canvasBallX = ballX + offset_trajectory
            val canvasBallY = height - ballY - offset_trajectory

            if (ballY >= 0 && canvasBallY >= 0 && canvasBallY <= height) {
                clipRect {
                    drawCircle(
                        color = Color(0xff202020),
                        radius = 8f,
                        center = Offset(canvasBallX, canvasBallY)
                    )
                }
            }
        }
    }
}