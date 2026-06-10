// ui/screens/HomeContent.kt
package ui.screens

import HomeViewModel
import TrajectoryColors
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeContent() {
    val viewModel = remember { HomeViewModel() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section 1: Projectile Overview & Characteristics
        item {
            ProjectileOverviewSection(viewModel)
        }

        // Section 2: Environment Parameters
        item {
            EnvironmentParametersSection(viewModel)
        }

        // Section 3: Mathematical Expressions (placeholder)
        item {
            MathematicalExpressionsSection()
        }
    }
}

@Composable
fun ProjectileOverviewSection(viewModel: HomeViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            // Header with expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.isProjectileExpanded = !viewModel.isProjectileExpanded }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        tint = TrajectoryColors.Purple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Projectile Characteristics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Icon(
                    if (viewModel.isProjectileExpanded) Icons.Default.ExpandLess
                    else Icons.Default.ExpandMore,
                    contentDescription = if (viewModel.isProjectileExpanded) "Collapse" else "Expand",
                    tint = TrajectoryColors.TextSecondary
                )
            }

            // Expandable content
            if (viewModel.isProjectileExpanded) {
                HorizontalDivider(color = TrajectoryColors.Divider)

                // Two-column layout: 3D preview + parameters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left: 3D Preview Area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(viewModel.projectile.colorRed, viewModel.projectile.colorGreen, viewModel.projectile.colorBlue, 0.3.toInt()),
                                        TrajectoryColors.Background
                                    ),
                                    center = Offset(150f, 150f),
                                    radius = 200f
                                )
                            )
                            .border(1.dp, TrajectoryColors.Divider, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 3D Sphere representation
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(viewModel.projectile.colorRed, viewModel.projectile.colorGreen, viewModel.projectile.colorBlue),
                                                Color(viewModel.projectile.colorRed, viewModel.projectile.colorGreen, viewModel.projectile.colorBlue).copy(alpha = 0.6f)
                                            ),
                                            radius = 80f
                                        )
                                    )
                                    .shadow(8.dp, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🎯",
                                    fontSize = 48.sp
                                )
                            }

                            Text(
                                text = viewModel.projectile.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TrajectoryColors.TextPrimary
                            )

                            Text(
                                text = viewModel.projectile.material,
                                fontSize = 12.sp,
                                color = TrajectoryColors.TextSecondary
                            )
                        }
                    }

                    // Right: Parameters form
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Mass input
                        ParameterRow(
                            label = "Mass",
                            value = viewModel.projectile.mass,
                            unit = "kg",
                            onValueChange = viewModel::updateProjectileMass,
                            valueRange = 0.01..1000.0,
                            format = "%.2f"
                        )

                        // Radius input
                        ParameterRow(
                            label = "Radius",
                            value = viewModel.projectile.radius,
                            unit = "m",
                            onValueChange = viewModel::updateProjectileRadius,
                            valueRange = 0.001..1.0,
                            format = "%.3f"
                        )

                        ParameterRow(
                            label = "Mass",
                            value = viewModel.projectile.mass,
                            unit = "kg",
                            onValueChange = viewModel::updateProjectileMass,
                            valueRange = 0.01..1000.0,
                            format = "%.2f"
                        )
                        // Read-only derived properties
                        DerivedPropertyRow(
                            label = "Diameter",
                            value = viewModel.projectile.diameter,
                            unit = "m",
                            icon = Icons.Default.Straighten
                        )

                        DerivedPropertyRow(
                            label = "Volume",
                            value = viewModel.projectile.volume,
                            unit = "m³",
                            icon = Icons.Default.Calculate
                        )

                        DerivedPropertyRow(
                            label = "Surface Area",
                            value = viewModel.projectile.surfaceArea,
                            unit = "m²",
                            icon = Icons.Default.GridOn
                        )

                        // Material picker
                        OutlinedTextField(
                            value = viewModel.projectile.material,
                            onValueChange = viewModel::updateProjectileMaterial,
                            label = { Text("Material") },
                            leadingIcon = {
                                Icon(Icons.Default.Category, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TrajectoryColors.Purple,
                                unfocusedBorderColor = TrajectoryColors.Divider
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnvironmentParametersSection(viewModel: HomeViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            // Header with expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.isEnvironmentExpanded = !viewModel.isEnvironmentExpanded }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Public,
                        contentDescription = null,
                        tint = TrajectoryColors.Purple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Environment Parameters",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Icon(
                    if (viewModel.isEnvironmentExpanded) Icons.Default.ExpandLess
                    else Icons.Default.ExpandMore,
                    contentDescription = if (viewModel.isEnvironmentExpanded) "Collapse" else "Expand",
                    tint = TrajectoryColors.TextSecondary
                )
            }

            // Expandable content
            if (viewModel.isEnvironmentExpanded) {
                HorizontalDivider(color = TrajectoryColors.Divider)

                // Grid layout for environment parameters
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Row 1: Gravity & Air Density
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ParameterRow(
                            label = "Gravity",
                            value = viewModel.environment.gravity,
                            unit = "m/s²",
                            onValueChange = viewModel::updateGravity,
                            valueRange = 0.0..30.0,
                            format = "%.2f",
                            modifier = Modifier.weight(1f)
                        )

                        ParameterRow(
                            label = "Air Density",
                            value = viewModel.environment.airDensity,
                            unit = "kg/m³",
                            onValueChange = viewModel::updateAirDensity,
                            valueRange = 0.0..2.0,
                            format = "%.3f",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Wind Speed & Direction
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ParameterRow(
                            label = "Wind Speed",
                            value = viewModel.environment.windSpeed,
                            unit = "m/s",
                            onValueChange = viewModel::updateWindSpeed,
                            valueRange = 0.0..50.0,
                            format = "%.1f",
                            modifier = Modifier.weight(1f)
                        )

                        ParameterRow(
                            label = "Wind Direction",
                            value = viewModel.environment.windDirection,
                            unit = "°",
                            onValueChange = viewModel::updateWindDirection,
                            valueRange = 0.0..360.0,
                            format = "%.0f",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Temperature & Pressure
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ParameterRow(
                            label = "Temperature",
                            value = viewModel.environment.temperature,
                            unit = "°C",
                            onValueChange = viewModel::updateTemperature,
                            valueRange = -50.0..100.0,
                            format = "%.1f",
                            modifier = Modifier.weight(1f)
                        )

                        ParameterRow(
                            label = "Pressure",
                            value = viewModel.environment.pressure,
                            unit = "Pa",
                            onValueChange = viewModel::updatePressure,
                            valueRange = 50000.0..150000.0,
                            format = "%.0f",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 4: Humidity slider
                    Column {
                        Text(
                            text = "Humidity",
                            fontSize = 12.sp,
                            color = TrajectoryColors.TextSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = TrajectoryColors.Purple,
                                modifier = Modifier.size(20.dp)
                            )

                            Slider(
                                value = viewModel.environment.humidity.toFloat(),
                                onValueChange = { viewModel.updateHumidity(it.toDouble()) },
                                valueRange = 0f..1f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = TrajectoryColors.Purple,
                                    activeTrackColor = TrajectoryColors.Purple
                                )
                            )

                            Text(
                                text = "${(viewModel.environment.humidity * 100).toInt()}%",
                                fontSize = 13.sp,
                                color = TrajectoryColors.TextPrimary,
                                modifier = Modifier.width(40.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    // Quick preset buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetButton(
                            text = "Earth Standard",
                            onClick = {
                                viewModel.updateGravity(9.81)
                                viewModel.updateAirDensity(1.225)
                                viewModel.updateTemperature(20.0)
                                viewModel.updatePressure(101325.0)
                            }
                        )

                        PresetButton(
                            text = "Moon",
                            onClick = {
                                viewModel.updateGravity(1.62)
                                viewModel.updateAirDensity(0.0)
                                viewModel.updateTemperature(-20.0)
                                viewModel.updatePressure(0.0)
                            }
                        )

                        PresetButton(
                            text = "Mars",
                            onClick = {
                                viewModel.updateGravity(3.71)
                                viewModel.updateAirDensity(0.020)
                                viewModel.updateTemperature(-60.0)
                                viewModel.updatePressure(600.0)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MathematicalExpressionsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Calculate,
                        contentDescription = null,
                        tint = TrajectoryColors.Purple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Mathematical Expressions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrajectoryColors.TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Badge(
                    containerColor = TrajectoryColors.Purple.copy(alpha = 0.1f),
                    contentColor = TrajectoryColors.Purple
                ) {
                    Text("Coming Soon", fontSize = 10.sp)
                }
            }

            HorizontalDivider(
                color = TrajectoryColors.Divider,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Placeholder content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        TrajectoryColors.Background.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Functions,
                        contentDescription = null,
                        tint = TrajectoryColors.TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Mathematical expressions and equations will be displayed here",
                        fontSize = 14.sp,
                        color = TrajectoryColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Drag force equations, trajectory calculations, etc.",
                        fontSize = 12.sp,
                        color = TrajectoryColors.TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun ParameterRow(
    label: String,
    value: Double,
    unit: String,
    onValueChange: (Double) -> Unit,
    valueRange: ClosedFloatingPointRange<Double>,
    format: String = "%.2f",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TrajectoryColors.TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = String.format(format, value),
            onValueChange = { text ->
                text.toDoubleOrNull()?.let {
                    if (it in valueRange) onValueChange(it)
                }
            },

            trailingIcon = {
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = TrajectoryColors.TextSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TrajectoryColors.Purple,
                unfocusedBorderColor = TrajectoryColors.Divider
            )
        )
    }
}

@Composable
fun DerivedPropertyRow(
    label: String,
    value: Double,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TrajectoryColors.Background.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = TrajectoryColors.TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TrajectoryColors.TextSecondary
            )
        }

        Text(
            text = String.format("%.4f", value) + " $unit",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TrajectoryColors.TextPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun PresetButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TrajectoryColors.Purple
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(TrajectoryColors.Purple.copy(alpha = 0.3f))
        )
    ) {
        Text(text, fontSize = 12.sp)
    }
}