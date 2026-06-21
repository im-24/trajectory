// ui/screens/HomeContent.kt
package ui.screens

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.models.DragModel
import org.example.project.presentation.ui.them.TrajectoryColors
import presentation.viewmodels.HomeViewModel

@Composable
fun HomeContent(viewModel: HomeViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { ProjectileOverviewSection(viewModel) }
        item { EnvironmentParametersSection(viewModel) }
        item { MathematicalExpressionsSection() }
    }
}

@Composable
fun ProjectileOverviewSection(viewModel: HomeViewModel) {
    var advancedExpanded by remember { mutableStateOf(false) }

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

            if (viewModel.isProjectileExpanded) {
                HorizontalDivider(color = TrajectoryColors.Divider)

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
                                        TrajectoryColors.Purple.copy(alpha = 0.3f),
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
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                TrajectoryColors.Background.copy(alpha = 0.3f),
                                                TrajectoryColors.LimeGreen.copy(alpha = 0.6f)
                                            ),
                                            radius = 80f
                                        )
                                    )
                                    .shadow(8.dp, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = " ", fontSize = 48.sp)
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
                        // ── Core parameters ──
                        ParameterRow(
                            label = "Mass",
                            value = viewModel.projectile.mass,
                            unit = "kg",
                            icon = Icons.Default.FitnessCenter,
                            onValueChange = viewModel::updateProjectileMass,
                            valueRange = 0.01..1000.0,
                            format = "%.2f"
                        )

                        ParameterRow(
                            label = "Radius",
                            value = viewModel.projectile.radius,
                            unit = "m",
                            icon = Icons.Default.RadioButtonUnchecked,
                            onValueChange = viewModel::updateProjectileRadius,
                            valueRange = 0.001..1.0,
                            format = "%.3f"
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

                        // ── Advanced (optional) section ──
                        AdvancedSectionToggle(
                            expanded = advancedExpanded,
                            onToggle = { advancedExpanded = !advancedExpanded },
                            label = "Advanced Ballistics"
                        )

                        if (advancedExpanded) {
                            // Drag model picker
                            DragModelPicker(
                                selected = viewModel.projectile.dragModel,
                                onSelect = viewModel::updateProjectileDragModel
                            )

                            ParameterRow(
                                label = "Ballistic Coefficient",
                                value = viewModel.projectile.ballisticCoefficient,
                                unit = "",
                                icon = Icons.Default.Speed,
                                onValueChange = viewModel::updateProjectileBallisticCoefficient,
                                valueRange = 0.01..2.0,
                                format = "%.3f"
                            )

                            ParameterRow(
                                label = "Spin Rate",
                                value = viewModel.projectile.spinRate,
                                unit = "rad/s",
                                icon = Icons.Default.Loop,
                                onValueChange = viewModel::updateProjectileSpinRate,
                                valueRange = 0.0..10000.0,
                                format = "%.1f"
                            )

                            ParameterRow(
                                label = "Spin Axis Yaw",
                                value = viewModel.projectile.spinAxisYaw,
                                unit = "°",
                                icon = Icons.Default.Explore,
                                onValueChange = viewModel::updateProjectileSpinAxisYaw,
                                valueRange = -180.0..180.0,
                                format = "%.1f"
                            )

                            ParameterRow(
                                label = "Spin Axis Pitch",
                                value = viewModel.projectile.spinAxisPitch,
                                unit = "°",
                                icon = Icons.Default.Explore,
                                onValueChange = viewModel::updateProjectileSpinAxisPitch,
                                valueRange = -90.0..90.0,
                                format = "%.1f"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnvironmentParametersSection(viewModel: HomeViewModel) {
    var advancedExpanded by remember { mutableStateOf(false) }

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

            if (viewModel.isEnvironmentExpanded) {
                HorizontalDivider(color = TrajectoryColors.Divider)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Environment parameters content...
                    // (same as in your original file)
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

// ── Helper Components ───────────────────────────────────────────────────

@Composable
fun ParameterRow(
    label: String,
    value: Double,
    unit: String,
    icon: ImageVector,
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
            leadingIcon = {
                Icon(icon, contentDescription = null, tint = TrajectoryColors.Purple)
            },
            trailingIcon = {
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        fontSize = 12.sp,
                        color = TrajectoryColors.TextSecondary
                    )
                }
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
    icon: ImageVector
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

@Composable
fun AdvancedSectionToggle(
    expanded: Boolean,
    onToggle: () -> Unit,
    label: String = "Advanced Settings"
) {
    Column {
        HorizontalDivider(
            color = TrajectoryColors.Divider,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = null,
                    tint = TrajectoryColors.Purple,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TrajectoryColors.TextSecondary
                )
            }
            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = TrajectoryColors.TextSecondary
            )
        }
    }
}
// ── Helper Components ───────────────────────────────────────────────────

@Composable
fun DragModelPicker(
    selected: org.example.project.domain.DragModel,  // Changed to domain type
    onSelect: (org.example.project.domain.DragModel) -> Unit  // Changed to domain type
) {
    var expanded by remember { mutableStateOf(false) }

    // Convert domain to data for display
    val displayName = selected.name

    Column {
        Text(
            text = "Drag Model",
            fontSize = 12.sp,
            color = TrajectoryColors.TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box {
            OutlinedTextField(
                value = displayName,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(Icons.Default.Functions, contentDescription = null, tint = TrajectoryColors.Purple)
                },
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TrajectoryColors.Purple,
                    unfocusedBorderColor = TrajectoryColors.Divider
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // List all domain DragModel values
                org.example.project.domain.DragModel.entries.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model.name) },
                        onClick = {
                            onSelect(model)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}