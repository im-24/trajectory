// ui/screens/ProjectileCharacteristicsScreen.kt
package ui.screens

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.mappers.TrajectoryMapper
import org.example.project.presentation.ui.them.TrajectoryColors  // Add this import
import physics.TrajectoryCalculator
import presentation.viewmodels.HomeViewModel
import kotlin.math.*

// ── Entry point ───────────────────────────────────────────────────────────────
@Composable
fun ProjectileCharacteristicsScreen(viewModel: HomeViewModel) {
    val calculator = remember { TrajectoryCalculator() }
    val mapper = remember { TrajectoryMapper() }

    // Convert domain models to data models for the calculator
    val projectileData = remember(viewModel.projectile) { mapper.toDataProjectile(viewModel.projectile) }
    val environmentData = remember(viewModel.environment) { mapper.toDataEnvironment(viewModel.environment) }

    // Run a reference trajectory (45° optimal angle, default velocity) for error analysis
    val referenceResult by remember(viewModel.projectile, viewModel.environment) {
        derivedStateOf {
            calculator.calculateTrajectory(
                projectile = projectileData,  // Use data model
                environment = environmentData,  // Use data model
                initialVelocity = 50.0,
                launchElevation = 45.0,
                launchAzimuth = 0.0,
                initialHeight = 0.0
            )
        }
    }

    // Vacuum (drag-free) reference for error comparison
    val vacuumResult by remember(viewModel.projectile, viewModel.environment) {
        derivedStateOf {
            val vacuumEnv = environmentData.copy(
                airDensity = 0.0,
                windSpeed = 0.0,
                humidity = 0.0,
                turbulenceIntensity = 0.0,
                windGustSpeed = 0.0
            )
            calculator.calculateTrajectory(
                projectile = projectileData,
                environment = vacuumEnv,
                initialVelocity = 50.0,
                launchElevation = 45.0,
                launchAzimuth = 0.0,
                initialHeight = 0.0
            )
        }
    }

    val errors = remember(referenceResult, vacuumResult) {
        computeErrors(referenceResult, vacuumResult)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ScreenHeader()
        ProjectileInfoCard(projectileData)
        EnvironmentInfoCard(environmentData)
        SimulationSummaryCard(referenceResult)
        ErrorAnalysisCard(errors)
    }
}

// ── Section: Page header ──────────────────────────────────────────────────────
@Composable
private fun ScreenHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.Adjust,
            contentDescription = null,
            tint = TrajectoryColors.Purple,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                "Projectile Info",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TrajectoryColors.Purple,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "Configuration overview & error analysis",
                fontSize = 13.sp,
                color = TrajectoryColors.TextSecondary
            )
        }
    }
}

// ── Section: Projectile ───────────────────────────────────────────────────────
@Composable
private fun ProjectileInfoCard(p: data.models.ProjectileData) {
    InfoCard(
        title = "Projectile Characteristics",
        icon = Icons.Default.Category,
        accent = TrajectoryColors.Purple
    ) {
        TwoColumnGrid {
            InfoCell("Name", p.name, Icons.Default.Label)
            InfoCell("Material", p.material, Icons.Default.Layers)
            InfoCell("Mass", "%.4f kg".format(p.mass), Icons.Default.FitnessCenter)
            InfoCell("Radius", "%.4f m".format(p.radius), Icons.Default.RadioButtonUnchecked)
            InfoCell("Diameter", "%.4f m".format(p.diameter), Icons.Default.Straighten)
            InfoCell("Volume", "%.6f m³".format(p.volume), Icons.Default.Category)
            InfoCell("Cross-section", "%.6f m²".format(p.crossSectionalArea), Icons.Default.PanoramaFishEye)
            InfoCell("Surface", "%.4f m²".format(p.surfaceArea), Icons.Default.Texture)
        }

        SectionDivider("Ballistics")
        TwoColumnGrid {
            InfoCell("Drag Model", p.dragModel.name, Icons.Default.Air)
            InfoCell("Ballistic Coeff.", "%.3f".format(p.ballisticCoefficient), Icons.Default.Speed)
            InfoCell("Spin Rate", if (p.spinRate == 0.0) "None"
            else "%.1f rad/s".format(p.spinRate), Icons.Default.Loop)
            if (p.spinRate != 0.0) {
                InfoCell("Spin Yaw", "%.1f°".format(p.spinAxisYaw), Icons.Default.Explore)
                InfoCell("Spin Pitch", "%.1f°".format(p.spinAxisPitch), Icons.Default.Explore)
            }
        }
    }
}

// ── Section: Environment ──────────────────────────────────────────────────────
@Composable
private fun EnvironmentInfoCard(e: data.models.EnvironmentData) {
    InfoCard(
        title = "Environment Parameters",
        icon = Icons.Default.Public,
        accent = Color(0xFF0EA5E9)
    ) {
        TwoColumnGrid {
            InfoCell("Gravity", "%.4f m/s²".format(e.gravity), Icons.Default.Download)
            InfoCell("Air Density", "%.4f kg/m³".format(e.airDensity), Icons.Default.Cloud)
            InfoCell("Temperature", "%.1f °C".format(e.temperature), Icons.Default.Thermostat)
            InfoCell("Pressure", "%.0f Pa".format(e.pressure), Icons.Default.Speed)
            InfoCell("Humidity", "%.0f%%".format(e.humidity * 100), Icons.Default.WaterDrop)
            InfoCell("Altitude", "%.0f m".format(e.altitude), Icons.Default.Terrain)
        }

        SectionDivider("Wind")
        TwoColumnGrid {
            InfoCell("Wind Speed", "%.1f m/s".format(e.windSpeed), Icons.Default.Air)
            InfoCell("Wind Direction", "%.0f°".format(e.windDirection), Icons.Default.Explore)
            if (e.windGustSpeed > 0.0) {
                InfoCell("Gust Speed", "%.1f m/s".format(e.windGustSpeed), Icons.Default.Bolt)
                InfoCell("Gust Freq.", "%.2f Hz".format(e.windGustFrequency), Icons.Default.GraphicEq)
            }
            if (e.turbulenceIntensity > 0.0)
                InfoCell("Turbulence", "%.2f".format(e.turbulenceIntensity), Icons.Default.Waves)
        }

        SectionDivider("Derived")
        TwoColumnGrid {
            InfoCell("Speed of Sound", "%.1f m/s".format(e.speedOfSoundSeaLevel), Icons.Default.VolumeUp)
            InfoCell("Lapse Rate", "%.4f K/m".format(e.temperatureLapseRate), Icons.Default.TrendingDown)
        }
    }
}

// ── Section: Simulation Summary ───────────────────────────────────────────────
@Composable
private fun SimulationSummaryCard(result: data.models.TrajectoryResult) {
    InfoCard(
        title = "Simulation Summary",
        icon = Icons.Default.Analytics,
        accent = Color(0xFF10B981),
        subtitle = "Reference run: v₀ = 50 m/s, θ = 45°"
    ) {
        // Key metrics banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricBadge("Max Distance", "${result.maxDistance.toInt()} m", Color(0xFF10B981))
            MetricBadge("Max Height", "${result.maxHeight.toInt()} m", TrajectoryColors.Purple)
            MetricBadge("Flight Time", "%.2f s".format(result.timeOfFlight), Color(0xFF0EA5E9))
            MetricBadge("Impact Speed", "${result.impactVelocity.toInt()} m/s", Color(0xFFF59E0B))
        }

        Spacer(Modifier.height(12.dp))

        TwoColumnGrid {
            InfoCell("Impact Angle", "%.2f°".format(result.impactAngle), Icons.Default.TrendingDown)
            InfoCell("Data Points", "${result.points.size}", Icons.Default.DataArray)
            result.points.maxByOrNull { it.mach }?.let { p ->
                InfoCell("Peak Mach", "%.3f".format(p.mach), Icons.Default.Speed)
            }
            result.points.minByOrNull { it.dragCoefficient }?.let { p ->
                InfoCell("Min CD", "%.4f".format(p.dragCoefficient), Icons.Default.Air)
            }
            result.points.maxByOrNull { it.kineticEnergy }?.let { p ->
                InfoCell("Peak KE", "%.1f J".format(p.kineticEnergy), Icons.Default.FlashOn)
            }
            result.points.lastOrNull()?.let { p ->
                InfoCell("Final KE", "%.1f J".format(p.kineticEnergy), Icons.Default.FlashOn)
            }
        }
    }
}

// ── Section: Error Analysis ───────────────────────────────────────────────────
data class TrajectoryErrors(
    val rangeError: Double,
    val rangeErrorPct: Double,
    val heightError: Double,
    val heightErrorPct: Double,
    val timeError: Double,
    val timeErrorPct: Double,
    val impactVelocityError: Double,
    val impactVelocityErrorPct: Double,
    val impactAngleError: Double,
    val rmsePosition: Double
)

fun computeErrors(actual: data.models.TrajectoryResult, vacuum: data.models.TrajectoryResult): TrajectoryErrors {
    val rangeErr = vacuum.maxDistance - actual.maxDistance
    val heightErr = vacuum.maxHeight - actual.maxHeight
    val timeErr = vacuum.timeOfFlight - actual.timeOfFlight
    val velErr = vacuum.impactVelocity - actual.impactVelocity
    val angleErr = actual.impactAngle - vacuum.impactAngle

    val n = minOf(actual.points.size, vacuum.points.size)
    val rmse = if (n < 2) 0.0 else {
        val sumSq = (0 until n).sumOf { i ->
            val ap = actual.points[i]
            val vp = vacuum.points[i]
            val dx = ap.x - vp.x
            val dy = ap.y - vp.y
            dx * dx + dy * dy
        }
        sqrt(sumSq / n)
    }

    fun pct(delta: Double, ref: Double) = if (ref == 0.0) 0.0 else (delta / ref) * 100.0

    return TrajectoryErrors(
        rangeError = rangeErr,
        rangeErrorPct = pct(rangeErr, vacuum.maxDistance),
        heightError = heightErr,
        heightErrorPct = pct(heightErr, vacuum.maxHeight),
        timeError = timeErr,
        timeErrorPct = pct(timeErr, vacuum.timeOfFlight),
        impactVelocityError = velErr,
        impactVelocityErrorPct = pct(velErr, vacuum.impactVelocity),
        impactAngleError = angleErr,
        rmsePosition = rmse
    )
}

@Composable
private fun ErrorAnalysisCard(e: TrajectoryErrors) {
    InfoCard(
        title = "Error Analysis",
        icon = Icons.Default.ErrorOutline,
        accent = Color(0xFFF59E0B),
        subtitle = "Drag & atmosphere effect vs. ideal vacuum trajectory"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrackChanges, null,
                    tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Position RMSE",
                        fontSize = 12.sp, color = Color(0xFF92400E))
                    Text("Root-mean-square deviation across full flight path",
                        fontSize = 11.sp, color = Color(0xFFB45309))
                }
            }
            Text(
                "%.2f m".format(e.rmsePosition),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD97706),
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.height(12.dp))

        ErrorRow("Range loss", e.rangeError, "m", e.rangeErrorPct, Icons.Default.SocialDistance)
        ErrorRow("Max height loss", e.heightError, "m", e.heightErrorPct, Icons.Default.Height)
        ErrorRow("Flight time diff.", e.timeError, "s", e.timeErrorPct, Icons.Default.Timer)
        ErrorRow("Impact velocity loss", e.impactVelocityError, "m/s", e.impactVelocityErrorPct, Icons.Default.Speed)
        ErrorRow("Impact angle shift", e.impactAngleError, "°", null, Icons.Default.TrendingDown)

        Spacer(Modifier.height(8.dp))
        Text(
            "Positive values = vacuum exceeds drag-affected result. " +
                    "Reference: v₀ = 50 m/s, θ = 45°, azimuth = 0°.",
            fontSize = 11.sp,
            color = TrajectoryColors.TextMuted,
            lineHeight = 16.sp
        )
    }
}

// ── Reusable layout components ────────────────────────────────────────────────

@Composable
private fun InfoCard(
    title: String,
    icon: ImageVector,
    accent: Color,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accent.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.TextPrimary, fontFamily = FontFamily.Monospace)
                    if (subtitle != null)
                        Text(subtitle, fontSize = 11.sp, color = TrajectoryColors.TextMuted)
                }
            }
            HorizontalDivider(color = TrajectoryColors.Divider,
                modifier = Modifier.padding(vertical = 14.dp))
            content()
        }
    }
}

@Composable
private fun TwoColumnGrid(content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        content()
    }
}

@Composable
private fun InfoCell(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null,
                tint = TrajectoryColors.Purple.copy(alpha = 0.55f),
                modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, fontSize = 13.sp, color = TrajectoryColors.TextSecondary)
        }
        Text(value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TrajectoryColors.TextPrimary,
            fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun SectionDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = TrajectoryColors.Divider)
        Text("  $label  ",
            fontSize = 11.sp,
            color = TrajectoryColors.TextMuted,
            fontWeight = FontWeight.SemiBold)
        HorizontalDivider(modifier = Modifier.weight(1f), color = TrajectoryColors.Divider)
    }
}

@Composable
private fun MetricBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace)
        Text(label,
            fontSize = 11.sp,
            color = TrajectoryColors.TextSecondary)
    }
}

@Composable
private fun ErrorRow(
    label: String,
    delta: Double,
    unit: String,
    pct: Double?,
    icon: ImageVector
) {
    val isZero = delta == 0.0
    val valueColor = when {
        isZero -> TrajectoryColors.TextMuted
        delta > 0.0 -> Color(0xFFDC2626)
        else -> Color(0xFF16A34A)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null,
                tint = TrajectoryColors.TextSecondary,
                modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, color = TrajectoryColors.TextSecondary)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (pct != null) {
                Text("(%.1f%%)".format(pct),
                    fontSize = 11.sp,
                    color = valueColor.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace)
            }
            Text("%.2f %s".format(delta, unit),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                fontFamily = FontFamily.Monospace)
        }
    }
}