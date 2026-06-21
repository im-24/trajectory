// Home.kt - Fixed version
package org.example.project

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.models.ProjectileData
import org.example.project.presentation.ui.them.TrajectoryTheme
import presentation.components.EnvironmentSection
import presentation.components.ErrorMessage
import presentation.components.HomeHeader
import presentation.components.LoadingIndicator
import presentation.components.ProjectileSection
import presentation.components.TrajectoryResults
import presentation.viewmodels.HomeViewModel
import ui.screens.*

// In Home.kt or a new file like utils/ColorUtils.kt

import androidx.compose.ui.graphics.Color
import org.example.project.domain.Projectile
import org.example.project.presentation.ui.them.TrajectoryColors

// Helper function to convert domain Projectile to Color
fun projectileColor(projectile: Projectile): Color {
    return when (projectile.material.lowercase()) {
        "steel" -> Color(0xFF7B8D9E)
        "aluminum" -> Color(0xFFC0C0C0)
        "copper" -> Color(0xFFB87333)
        "lead" -> Color(0xFF434B4D)
        "titanium" -> Color(0xFF8A8F9A)
        "tungsten" -> Color(0xFF4F5B62)
        "gold" -> Color(0xFFFFD700)
        "silver" -> Color(0xFFC0C0C0)
        "iron" -> Color(0xFF434B4D)
        "brass" -> Color(0xFFB5A642)
        "plastic" -> Color(0xFF4CAF50)
        "wood" -> Color(0xFF8D6E63)
        "glass" -> Color(0xFF90CAF9)
        "rubber" -> Color(0xFF424242)
        "ceramic" -> Color(0xFFE8E8E8)
        "composite" -> Color(0xFF2E2E2E)
        else -> TrajectoryColors.Purple
    }
}
// ── Tab definitions ────────────────────────────────────────────
enum class AppTab(val label: String) {
    HOME("Home"),
    SIM_2D("2D Simulation"),
    SIM_3D("3D Simulation"),
    PROJECTILE("Projectile info"),
    DATA("Data"),
    EXPORT("Export")
}

// ── Sidebar items (per tab — extend later) ─────────────────────
data class SidebarItem(val label: String, val icon: ImageVector)

fun sidebarItemsFor(tab: AppTab): List<SidebarItem> = when (tab) {
    AppTab.HOME -> listOf(
        SidebarItem("Overview", Icons.Default.Dashboard),
        SidebarItem("Notes", Icons.Default.EditNote),
        SidebarItem("History", Icons.Default.History)
    )
    AppTab.PROJECTILE -> listOf(
        SidebarItem("Parameters", Icons.Default.Tune),
        SidebarItem("Materials", Icons.Default.Layers),
        SidebarItem("Presets", Icons.Default.BookmarkBorder)
    )
    AppTab.SIM_2D -> listOf(
        SidebarItem("Controls", Icons.Default.PlayArrow),
        SidebarItem("Overlay", Icons.Default.Layers),
        SidebarItem("Markers", Icons.Default.Place)
    )
    AppTab.SIM_3D -> listOf(
        SidebarItem("Camera", Icons.Default.Videocam),
        SidebarItem("Controls", Icons.Default.PlayArrow),
        SidebarItem("Environment", Icons.Default.Public)
    )
    AppTab.DATA -> listOf(
        SidebarItem("Filter", Icons.Default.FilterList),
        SidebarItem("Sort", Icons.Default.Sort),
        SidebarItem("Columns", Icons.Default.ViewColumn)
    )
    AppTab.EXPORT -> listOf(
        SidebarItem("Format", Icons.Default.Description),
        SidebarItem("Range", Icons.Default.DateRange),
        SidebarItem("Options", Icons.Default.Settings)
    )
}

// ── Settings state ─────────────────────────────────────────────
data class AppSettings(
    val projectName: String = "Untitled Project",
    val backupIntervalMinutes: Int = 10,
    val darkMode: Boolean = false,
    val language: String = "English",
    val fontSize: Int = 14,
    val reportAuthorName: String = "",
    val reportCompany: String = "",
    val reportLogoPath: String = "",
    val reportDepartment: String = "",
    val reportContact: String = "",
    val reportFootnote: String = ""
)

// ── Main home/workspace composable ────────────────────────────
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,  // Changed: now accepts viewModel as parameter
    projectConfig: NewProjectConfig? = null  // Added: optional project config
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with project name
        HomeHeader(
            projectile = viewModel.projectile,
            projectName = projectConfig?.name ?: "Untitled Project",
            onProjectileUpdate = viewModel::updateProjectile
        )

        // Projectile Section
        ProjectileSection(
            projectile = viewModel.projectile,
            isExpanded = viewModel.isProjectileExpanded,
            onToggle = viewModel::toggleProjectileExpanded,
            onUpdate = viewModel::updateProjectile
        )

        // Environment Section
        EnvironmentSection(
            environment = viewModel.environment,
            isExpanded = viewModel.isEnvironmentExpanded,
            onToggle = viewModel::toggleEnvironmentExpanded,
            onUpdate = viewModel::updateEnvironment
        )

        // Results
        if (uiState.isLoading) {
            LoadingIndicator()
        }

        uiState.trajectory?.let { trajectory ->
            TrajectoryResults(
                trajectory = trajectory,
                onExport = { /* Handle export */ }
            )
        }

        uiState.error?.let { error ->
            ErrorMessage(error)
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
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
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
                    fontSize = 13.sp,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FiberManualRecord,
                        contentDescription = "Saved",
                        tint = TrajectoryColors.LimeGreen,
                        modifier = Modifier.size(8.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Saved", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedTab.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrajectoryColors.Purple,
                    fontFamily = FontFamily.Monospace
                )

                // Tabs row
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppTab.entries.forEach { tab ->
                        TabItem(
                            tab = tab,
                            selected = tab == selectedTab,
                            onClick = { onTabSelected(tab) }
                        )
                    }
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
            Spacer(Modifier.width(6.dp))
            Text(
                text = tab.label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor
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
    expanded: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    items: List<SidebarItem>,
    onNewProject: () -> Unit,
    onOpenProject: () -> Unit,
    onSaveProject: () -> Unit,
    onCloseProject: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(modifier = Modifier.fillMaxHeight()) {
        if (!expanded) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(TrajectoryColors.Purple.copy(alpha = 0.15f))
                    .clickable { onExpand() }
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(200)) + fadeIn(tween(150)),
            exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(180)) + fadeOut(tween(120))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(220.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val inBounds = event.changes.any { c ->
                                    c.position.x in 0f..size.width.toFloat() &&
                                            c.position.y in 0f..size.height.toFloat()
                                }
                                if (!inBounds) onCollapse()
                            }
                        }
                    },
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TrajectoryColors.Purple)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Workspace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(onClick = onCollapse, modifier = Modifier.size(24.dp)) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = "Collapse",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // TOP ZONE: Context items
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp)
                    ) {
                        SidebarSectionLabel("Current View")
                        items.forEach { item -> SidebarRow(item = item) }
                    }

                    // BOTTOM ZONE: Persistent actions
                    HorizontalDivider(color = TrajectoryColors.Divider)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        SidebarSectionLabel("Project")
                        SidebarActionRow("New Project", Icons.Default.CreateNewFolder, onNewProject)
                        SidebarActionRow("Open Project", Icons.Default.FolderOpen, onOpenProject)
                        SidebarActionRow("Save", Icons.Default.Save, onSaveProject)
                        SidebarActionRow("Close Project", Icons.Default.Close, onCloseProject, Color(0xFFDC2626))

                        HorizontalDivider(
                            color = TrajectoryColors.Divider,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenSettings() }
                                .background(TrajectoryColors.Purple.copy(alpha = 0.06f))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = TrajectoryColors.Purple,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "Settings",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TrajectoryColors.Purple
                                )
                            }
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TrajectoryColors.Purple.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarSectionLabel(label: String) {
    Text(
        text = label.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = TrajectoryColors.TextMuted,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
fun SidebarRow(item: SidebarItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = TrajectoryColors.Purple,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(item.label, fontSize = 13.sp, color = TrajectoryColors.TextPrimary)
    }
}

@Composable
private fun SidebarActionRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color = TrajectoryColors.TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint.copy(alpha = 0.75f),
            modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(label, fontSize = 13.sp, color = tint)
    }
}

// Helper function to convert RGB Ints to Color
// In Home.kt - Update the projectileColor function
// Helper function to convert domain Projectile to Color


// ── Main Content Area ──────────────────────────────────────────
@Composable
fun MainContentArea(
    tab: AppTab,
    homeViewModel: HomeViewModel,
) {
    when (tab) {
        AppTab.HOME -> {
            HomeContent(homeViewModel)
        }
        AppTab.PROJECTILE -> {
            ProjectileCharacteristicsScreen(homeViewModel)
        }
        AppTab.SIM_2D -> {
            TwoDSimulationScreen(
                projectile = homeViewModel.projectile,
                environment = homeViewModel.environment
            )
        }
        AppTab.SIM_3D -> {
            ThreeDSimulationScreen(homeViewModel)
        }
        AppTab.DATA -> {
            DataScreen()
        }
        AppTab.EXPORT -> {
            ExportScreen()
        }
    }
}

// ── Remaining helper functions ────────────────────────────────
// (ProjectileOverviewSection, EnvironmentParametersSection,
//  MathematicalExpressionsSection, ParameterRow, DerivedPropertyRow,
//  PresetButton remain the same as in your original file)