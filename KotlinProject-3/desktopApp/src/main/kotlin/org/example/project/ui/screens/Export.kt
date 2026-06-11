// ui/screens/Export.kt
package ui.screens

import org.example.project.ui.them.TrajectoryColors
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import data.DataManager
import data.models.*
import org.example.project.LocalAppSettings
import services.ReportGenerator
import java.io.File
import java.text.SimpleDateFormat
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.Double

@Composable
fun ExportScreen() {
    val settings      = LocalAppSettings.current          // ← live settings
    val dataManager   = remember { DataManager() }
    val reportGenerator = remember { ReportGenerator() }
    var trajectories by remember { mutableStateOf<List<TrajectoryResult>>(emptyList()) }
    var selectedTrajectoryId by remember { mutableStateOf<String?>(null) }
    var selectedTrajectory by remember { mutableStateOf<TrajectoryResult?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Export states
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("CSV") }
    var exportLocation by remember { mutableStateOf("") }

    // Report states
    var showReportDialog by remember { mutableStateOf(false) }
    var reportFormat by remember { mutableStateOf("WORD") }
    var reportLocation by remember { mutableStateOf("") }

    // UI states
    var isGenerating by remember { mutableStateOf(false) }
    var progressMessage by remember { mutableStateOf("") }
    var expandedSection by remember { mutableStateOf("data") }

    LaunchedEffect(Unit) {
        trajectories = dataManager.loadAllTrajectories()
        if (trajectories.isNotEmpty() && selectedTrajectoryId == null) {
            selectedTrajectoryId = trajectories.first().id
            selectedTrajectory = trajectories.first()
        }
    }

    LaunchedEffect(selectedTrajectoryId) {
        selectedTrajectory = trajectories.find { it.id == selectedTrajectoryId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                        text = "Export Center",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.Purple,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Export simulation data and generate professional reports",
                        fontSize = 14.sp,
                        color = TrajectoryColors.TextSecondary
                    )
                }
                Icon(
                    Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = TrajectoryColors.Purple,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Trajectory Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select Simulation",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrajectoryColors.TextPrimary
                )

                Divider(color = TrajectoryColors.Divider)

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
                                text = "No simulations available",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TrajectoryColors.TextSecondary
                            )
                            Text(
                                text = "Run a simulation first to see it here",
                                fontSize = 12.sp,
                                color = TrajectoryColors.TextMuted
                            )
                        }
                    }
                } else {
                    // Standard dropdown menu without experimental API
                    Column {
                        // Dropdown button
                        OutlinedButton(
                            onClick = { isDropdownExpanded = !isDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedTrajectory?.let {
                                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                                        "${dateFormat.format(java.util.Date(it.timestamp))} - ${it.maxDistance.toInt()}m"
                                    } ?: "Select a simulation",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 13.sp
                                )
                                Icon(
                                    if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Select"
                                )
                            }
                        }

                        // Dropdown menu
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            trajectories.forEach { trajectory ->
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = dateFormat.format(java.util.Date(trajectory.timestamp)),
                                                fontWeight = if (trajectory.id == selectedTrajectoryId) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Distance: ${trajectory.maxDistance.toInt()}m | Height: ${trajectory.maxHeight.toInt()}m | Time: ${trajectory.timeOfFlight.toInt()}s",
                                                fontSize = 11.sp,
                                                color = TrajectoryColors.TextSecondary
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedTrajectoryId = trajectory.id
                                        selectedTrajectory = trajectory
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Preview info
                    selectedTrajectory?.let { traj ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = TrajectoryColors.Background),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PreviewInfo("Max Distance", "${traj.maxDistance.toInt()} m")
                                PreviewInfo("Max Height", "${traj.maxHeight.toInt()} m")
                                PreviewInfo("Time of Flight", "${traj.timeOfFlight.toInt()} s")
                                PreviewInfo("Points", "${traj.points.size}")
                            }
                        }
                    }
                }
            }
        }

        // Data Export Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = null,
                            tint = TrajectoryColors.Purple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Data Export",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrajectoryColors.TextPrimary
                        )
                    }
                    IconButton(onClick = { expandedSection = if (expandedSection == "data") "" else "data" }) {
                        Icon(
                            if (expandedSection == "data") Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            null
                        )
                    }
                }

                AnimatedVisibility(visible = expandedSection == "data") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Divider(color = TrajectoryColors.Divider)

                        Text(
                            text = "Select Format",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TrajectoryColors.TextSecondary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilterChip(
                                selected = exportFormat == "CSV",
                                onClick = { exportFormat = "CSV" },
                                label = { Text("CSV", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TrajectoryColors.Purple.copy(alpha = 0.2f)
                                )
                            )
                            FilterChip(
                                selected = exportFormat == "JSON",
                                onClick = { exportFormat = "JSON" },
                                label = { Text("JSON", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TrajectoryColors.Purple.copy(alpha = 0.2f)
                                )
                            )
                            FilterChip(
                                selected = exportFormat == "Excel",
                                onClick = { exportFormat = "Excel" },
                                label = { Text("Excel", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TrajectoryColors.Purple.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // Export location
                        OutlinedTextField(
                            value = exportLocation,
                            onValueChange = { exportLocation = it },
                            label = { Text("Export Location") },
                            placeholder = { Text("Select where to save the file") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val chooser = JFileChooser()
                                    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                        exportLocation = chooser.selectedFile.absolutePath
                                    }
                                }) {
                                    Icon(Icons.Default.FolderOpen, "Browse")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (selectedTrajectory != null && exportLocation.isNotBlank()) {
                                    val fileName = "trajectory_${selectedTrajectory!!.id}.${exportFormat.lowercase()}"
                                    val filePath = File(exportLocation, fileName).absolutePath

                                    when (exportFormat) {
                                        "CSV" -> dataManager.exportToCSV(selectedTrajectory!!, filePath)
                                        "JSON" -> dataManager.exportToJSON(selectedTrajectory!!, filePath)
                                        "Excel" -> dataManager.exportToExcel(selectedTrajectory!!, filePath)
                                    }

                                    JOptionPane.showMessageDialog(
                                        null,
                                        "Export completed!\n\nFile saved to:\n$filePath",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE
                                    )
                                } else {
                                    JOptionPane.showMessageDialog(
                                        null,
                                        "Please select a simulation and export location",
                                        "Export Failed",
                                        JOptionPane.WARNING_MESSAGE
                                    )
                                }
                            },
                            enabled = selectedTrajectory != null && exportLocation.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = TrajectoryColors.Purple),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.FileDownload, "Export")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Data")
                        }
                    }
                }
            }
        }

        // Report Export Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = TrajectoryColors.LimeGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Report Generation",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrajectoryColors.TextPrimary
                        )
                    }
                    IconButton(onClick = { expandedSection = if (expandedSection == "report") "" else "report" }) {
                        Icon(
                            if (expandedSection == "report") Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            null
                        )
                    }
                }

                AnimatedVisibility(visible = expandedSection == "report") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Divider(color = TrajectoryColors.Divider)

                        Text(
                            text = "Report Format",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TrajectoryColors.TextSecondary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilterChip(
                                selected = reportFormat == "WORD",
                                onClick = { reportFormat = "WORD" },
                                label = { Text("Word Document", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TrajectoryColors.LimeGreen.copy(alpha = 0.2f)
                                )
                            )
                            FilterChip(
                                selected = reportFormat == "PDF",
                                onClick = { reportFormat = "PDF" },
                                label = { Text("PDF Document", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TrajectoryColors.LimeGreen.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // Report location
                        OutlinedTextField(
                            value = reportLocation,
                            onValueChange = { reportLocation = it },
                            label = { Text("Report Location") },
                            placeholder = { Text("Select where to save the report") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val chooser = JFileChooser()
                                    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                        reportLocation = chooser.selectedFile.absolutePath
                                    }
                                }) {
                                    Icon(Icons.Default.FolderOpen, "Browse")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Report features preview
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = TrajectoryColors.Background),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Report includes:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrajectoryColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                ReportFeature("Cover page with simulation details")
                                ReportFeature("Program information and summary")
                                ReportFeature("Projectile characteristics table")
                                ReportFeature("Environment conditions table")
                                ReportFeature("Simulation results with statistics")
                                ReportFeature("Complete trajectory data table")
                            }
                        }

                        Button(
                            onClick = {
                                if (selectedTrajectory != null && reportLocation.isNotBlank()) {
                                    isGenerating = true
                                    progressMessage = "Preparing report..."

                                    val fileName = "Trajectory_Report_${System.currentTimeMillis()}.${if (reportFormat == "WORD") "docx" else "pdf"}"
                                    val filePath = File(reportLocation, fileName).absolutePath

                                    val reportData = ReportData(
                                        title = "${settings.projectName} — Trajectory Simulation Report",
                                        date  = java.util.Date(),
                                        programInfo = ProgramInfo(
                                            name        = "Trajectory Pro",
                                            version     = "1.0.0",
                                            description = buildString {
                                                if (settings.reportCompany.isNotBlank())
                                                    append("${settings.reportCompany} · ")
                                                if (settings.reportDepartment.isNotBlank())
                                                    append("${settings.reportDepartment} · ")
                                                append("Advanced projectile motion simulation software.")
                                            },
                                            features = listOf(
                                                if (settings.reportAuthorName.isNotBlank())
                                                    "Analyst: ${settings.reportAuthorName}" else null,
                                                if (settings.reportContact.isNotBlank())
                                                    "Contact: ${settings.reportContact}" else null,
                                                "Realistic physics simulation with air drag",
                                                "2D and 3D trajectory visualization",
                                                "Environmental condition modeling",
                                                "Data export and reporting",
                                                if (settings.reportFootnote.isNotBlank())
                                                    settings.reportFootnote else null
                                            ).filterNotNull()
                                        ),
                                        projectileParameters = ProjectileReportData(
                                            name = selectedTrajectory!!.projectileData.name,
                                            mass = selectedTrajectory!!.projectileData.mass,
                                            radius = selectedTrajectory!!.projectileData.radius,
                                            diameter = selectedTrajectory!!.projectileData.diameter,
                                            volume = selectedTrajectory!!.projectileData.volume,
                                            surfaceArea = selectedTrajectory!!.projectileData.surfaceArea,
                                            material = selectedTrajectory!!.projectileData.material
                                        ),
                                        environmentParameters = EnvironmentReportData(
                                            gravity = selectedTrajectory!!.environmentData.gravity,
                                            airDensity = selectedTrajectory!!.environmentData.airDensity,
                                            windSpeed = selectedTrajectory!!.environmentData.windSpeed,
                                            windDirection = selectedTrajectory!!.environmentData.windDirection,
                                            temperature = selectedTrajectory!!.environmentData.temperature,
                                            pressure = selectedTrajectory!!.environmentData.pressure,
                                            humidity = selectedTrajectory!!.environmentData.humidity
                                        ),
                                        simulationResults = SimulationResultsData(
                                            initialVelocity = selectedTrajectory!!.initialVelocity,
                                            launchAngle = selectedTrajectory!!.launchElevation,
                                            launchAzimuth= 0.0, // Add this

                                            initialHeight = selectedTrajectory!!.initialHeight,
                                            maxDistance = selectedTrajectory!!.maxDistance,
                                            maxHeight = selectedTrajectory!!.maxHeight,
                                            timeOfFlight = selectedTrajectory!!.timeOfFlight,
                                            impactVelocity = selectedTrajectory!!.impactVelocity,
                                            impactAngle = selectedTrajectory!!.impactAngle,
                                            maxSpeed = selectedTrajectory!!.points.maxOfOrNull { it.speed } ?: 0.0,
                                            avgSpeed = selectedTrajectory!!.points.map { it.speed }.average(),
                                            totalEnergy = selectedTrajectory!!.points.lastOrNull()?.kineticEnergy ?: 0.0
                                        ),
                                        trajectoryPoints = selectedTrajectory!!.points
                                    )

                                    try {
                                        progressMessage = "Generating ${if (reportFormat == "WORD") "Word" else "PDF"} report..."

                                        if (reportFormat == "WORD") {
                                            reportGenerator.generateWordReport(reportData, filePath)
                                        } else {
                                            reportGenerator.generatePDFReport(reportData, filePath)
                                        }

                                        val result = JOptionPane.showConfirmDialog(
                                            null,
                                            "Report generated!\n\nFile saved to:\n$filePath\n\nOpen it now?",
                                            "Report Generated",
                                            JOptionPane.YES_NO_OPTION
                                        )

                                        if (result == JOptionPane.YES_OPTION) {
                                            java.awt.Desktop.getDesktop().open(File(filePath))
                                        }
                                    } catch (e: Exception) {
                                        JOptionPane.showMessageDialog(
                                            null,
                                            "Error: ${e.message}",
                                            "Report Failed",
                                            JOptionPane.ERROR_MESSAGE
                                        )
                                    } finally {
                                        isGenerating = false
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(
                                        null,
                                        "Please select a simulation and report location",
                                        "Generation Failed",
                                        JOptionPane.WARNING_MESSAGE
                                    )
                                }
                            },
                            enabled = selectedTrajectory != null && reportLocation.isNotBlank() && !isGenerating,
                            colors = ButtonDefaults.buttonColors(containerColor = TrajectoryColors.LimeGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(progressMessage)
                            } else {
                                Icon(Icons.Default.Description, "Generate")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Report")
                            }
                        }
                    }
                }
            }
        }

        // Quick Export Section
        if (exportLocation.isNotBlank() || reportLocation.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Quick Export",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.TextPrimary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (exportLocation.isNotBlank() && selectedTrajectory != null) {
                            Button(
                                onClick = {
                                    val fileName = "trajectory_${selectedTrajectory!!.id}.${exportFormat.lowercase()}"
                                    val filePath = File(exportLocation, fileName).absolutePath
                                    when (exportFormat) {
                                        "CSV" -> dataManager.exportToCSV(selectedTrajectory!!, filePath)
                                        "JSON" -> dataManager.exportToJSON(selectedTrajectory!!, filePath)
                                        "Excel" -> dataManager.exportToExcel(selectedTrajectory!!, filePath)
                                    }
                                    JOptionPane.showMessageDialog(null, "Export completed!", "Success", JOptionPane.INFORMATION_MESSAGE)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TrajectoryColors.Purple),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quick Data Export", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewInfo(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = TrajectoryColors.TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TrajectoryColors.Purple)
    }
}

@Composable
fun ReportFeature(feature: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = TrajectoryColors.LimeGreen,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(feature, fontSize = 11.sp, color = TrajectoryColors.TextSecondary)
    }
}