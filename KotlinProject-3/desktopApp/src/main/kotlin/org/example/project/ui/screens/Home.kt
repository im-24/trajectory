package org.expample.projcet.viewmodel.HomeViewModel

import HomeViewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.screens.NewProjectConfig


// ── Tab definitions ────────────────────────────────────────────
enum class AppTab(val label: String, val icon: ImageVector) {
    HOME        ("Home",            Icons.Default.Home),
    PROJECTILE  ("Projectile info", Icons.Default.Adjust),
    SIM_2D      ("2D Simulation",   Icons.Default.ShowChart),
    SIM_3D      ("3D Simulation",   Icons.Default.ViewInAr),
    DATA        ("Data",            Icons.Default.TableChart),
    EXPORT      ("Export",          Icons.Default.FileDownload)
}

// ── Sidebar items (per tab — extend later) ─────────────────────
data class SidebarItem(val label: String, val icon: ImageVector)

fun sidebarItemsFor(tab: AppTab): List<SidebarItem> = when (tab) {
    AppTab.HOME       -> listOf(
        SidebarItem("Overview",   Icons.Default.Dashboard),
        SidebarItem("Notes",      Icons.Default.EditNote),
        SidebarItem("History",    Icons.Default.History)
    )
    AppTab.PROJECTILE -> listOf(
        SidebarItem("Parameters", Icons.Default.Tune),
        SidebarItem("Materials",  Icons.Default.Layers),
        SidebarItem("Presets",    Icons.Default.BookmarkBorder)
    )
    AppTab.SIM_2D     -> listOf(
        SidebarItem("Controls",   Icons.Default.PlayArrow),
        SidebarItem("Overlay",    Icons.Default.Layers),
        SidebarItem("Markers",    Icons.Default.Place)
    )
    AppTab.SIM_3D     -> listOf(
        SidebarItem("Camera",     Icons.Default.Videocam),
        SidebarItem("Controls",   Icons.Default.PlayArrow),
        SidebarItem("Environment",Icons.Default.Public)
    )
    AppTab.DATA       -> listOf(
        SidebarItem("Filter",     Icons.Default.FilterList),
        SidebarItem("Sort",       Icons.Default.Sort),
        SidebarItem("Columns",    Icons.Default.ViewColumn)
    )
    AppTab.EXPORT     -> listOf(
        SidebarItem("Format",     Icons.Default.Description),
        SidebarItem("Range",      Icons.Default.DateRange),
        SidebarItem("Options",    Icons.Default.Settings)
    )
}

// ── Main home/workspace composable ────────────────────────────
@Composable
fun HomeScreen(projectConfig: NewProjectConfig) {
    var selectedTab      by remember { mutableStateOf(AppTab.HOME) }
    var sidebarExpanded  by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Top Tab Bar ──────────────────────────────────────
        TopTabBar(
            selectedTab = selectedTab,
            projectName = projectConfig.name,
            onTabSelected = { selectedTab = it }
        )

        // ── Body: sidebar + main area ────────────────────────
        Row(modifier = Modifier.fillMaxSize()) {

            // Sidebar
            CollapsibleSidebar(
                expanded   = sidebarExpanded,
                onExpand   = { sidebarExpanded = true },
                onCollapse = { sidebarExpanded = false },
                items      = sidebarItemsFor(selectedTab)
            )

            // Main content area
            MainContentArea(tab = selectedTab)
        }
    }
}

// ── Top Tab Bar ────────────────────────────────────────────────
@Composable
fun TopTabBar(
    selectedTab: AppTab,
    projectName: String,
    onTabSelected: (AppTab) -> Unit
) {
    Surface(
        color     = Color.White,
        shadowElevation = 1.dp,
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column {
            // Project name strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TrajectoryColors.Purple)
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = projectName,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp,
                    color      = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FiberManualRecord,
                        contentDescription = "Saved",
                        tint   = TrajectoryColors.LimeGreen,
                        modifier = Modifier.size(8.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Saved", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }

            // Tabs row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTab.entries.forEach { tab ->
                    TabItem(
                        tab      = tab,
                        selected = tab == selectedTab,
                        onClick  = { onTabSelected(tab) }
                    )
                }
            }
        }
    }
}

@Composable
fun TabItem(tab: AppTab, selected: Boolean, onClick: () -> Unit) {
    val contentColor = if (selected) TrajectoryColors.Purple else TrajectoryColors.TextSecondary

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector      = tab.icon,
                contentDescription = tab.label,
                tint             = contentColor,
                modifier         = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text      = tab.label,
                fontSize  = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color     = contentColor
            )
        }
        // Active indicator bar
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(if (selected) 60.dp else 0.dp)
                .background(
                    color = if (selected) TrajectoryColors.Purple else Color.Transparent,
                    shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                )
        )
    }
}

// ── Collapsible Sidebar ────────────────────────────────────────
@Composable
fun CollapsibleSidebar(
    expanded:   Boolean,
    onExpand:   () -> Unit,
    onCollapse: () -> Unit,
    items:      List<SidebarItem>
) {
    // Hover detection strip — always visible, triggers expand
    Row(modifier = Modifier.fillMaxHeight()) {

        // Thin hover-sensitive strip when collapsed
        if (!expanded) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(TrajectoryColors.Purple.copy(alpha = 0.15f))
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.changes.any { it.pressed.not() }) {
                                    onExpand()
                                }
                            }
                        }
                    }
                    .clickable { onExpand() }   // fallback click
            )
        }

        // Animated sidebar panel
        AnimatedVisibility(
            visible = expanded,
            enter   = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec  = tween(200)
            ) + fadeIn(tween(150)),
            exit    = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(180)
            ) + fadeOut(tween(120))
        ) {
            Surface(
                modifier        = Modifier
                    .fillMaxHeight()
                    .width(220.dp)
                    .pointerInput(Unit) {
                        // collapse when pointer leaves the sidebar
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val inBounds = event.changes.any { change ->
                                    change.position.x in 0f..size.width.toFloat() &&
                                            change.position.y in 0f..size.height.toFloat()
                                }
                                if (!inBounds) onCollapse()
                            }
                        }
                    },
                color           = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)) {

                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "Options",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp,
                            color      = TrajectoryColors.TextPrimary
                        )
                        IconButton(
                            onClick  = onCollapse,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = "Hide sidebar",
                                tint = TrajectoryColors.TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        color    = TrajectoryColors.Divider,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Sidebar items
                    items.forEach { item ->
                        SidebarRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarRow(item: SidebarItem) {
    var hovered by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* will wire to actions later */ }
            .background(
                if (hovered) TrajectoryColors.Purple.copy(alpha = 0.06f)
                else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector      = item.icon,
            contentDescription = item.label,
            tint             = TrajectoryColors.Purple,
            modifier         = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(item.label, fontSize = 13.sp, color = TrajectoryColors.TextPrimary)
    }
}

// ── Main Content Area ( TO CHANGE )──────────────────────────────────────────
@Composable
fun MainContentArea(tab: AppTab) {
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


// ── Projectile Overview Section ─────────────────────────────────────────
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
                                        viewModel.projectile.color.copy(alpha = 0.3f),
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
                                                viewModel.projectile.color,
                                                viewModel.projectile.color.copy(alpha = 0.6f)
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
                            icon = Icons.Default.FitnessCenter,
                            onValueChange = viewModel::updateProjectileMass,
                            valueRange = 0.01..1000.0,
                            format = "%.2f"
                        )

                        // Radius input
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
                    }
                }
            }
        }
    }
}

// ── Environment Parameters Section ──────────────────────────────────────
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
                            icon = Icons.Default.Download,
                            onValueChange = viewModel::updateGravity,
                            valueRange = 0.0..30.0,
                            format = "%.2f",
                            modifier = Modifier.weight(1f)
                        )

                        ParameterRow(
                            label = "Air Density",
                            value = viewModel.environment.airDensity,
                            unit = "kg/m³",
                            icon = Icons.Default.Air,
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
                            icon = Icons.Default.Speed,
                            onValueChange = viewModel::updateWindSpeed,
                            valueRange = 0.0..50.0,
                            format = "%.1f",
                            modifier = Modifier.weight(1f)
                        )

                        ParameterRow(
                            label = "Wind Direction",
                            value = viewModel.environment.windDirection,
                            unit = "°",
                            icon = Icons.Default.Explore,
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
                            icon = Icons.Default.Thermostat,
                            onValueChange = viewModel::updateTemperature,
                            valueRange = -50.0..100.0,
                            format = "%.1f",
                            modifier = Modifier.weight(1f)
                        )

                        ParameterRow(
                            label = "Pressure",
                            value = viewModel.environment.pressure,
                            unit = "Pa",
                            icon = Icons.Default.Speed,
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

// ── Mathematical Expressions Section (Placeholder) ──────────────────────
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

// ── Helper Components ───────────────────────────────────────────────────
@Composable
fun ParameterRow(
    label: String,
    value: Double,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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