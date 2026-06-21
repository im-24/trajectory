package org.example.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.example.project.domain.repositories.AppSettings
import org.example.project.presentation.ui.them.TrajectoryColors
import presentation.viewmodels.HomeViewModel
import ui.screens.*

// ── Tab definitions ────────────────────────────────────────────
enum class AppTab(val label: String) {
    HOME("Home"),
    PROJECTILE("Projectile Info"),
    SIM_2D("2D Simulation"),
    SIM_3D("3D Simulation"),
    DATA("Data"),
    EXPORT("Export")
}

// ── Sidebar items ──────────────────────────────────────────────
data class SidebarItem(val label: String)

fun sidebarItemsFor(tab: AppTab): List<SidebarItem> = when (tab) {
    AppTab.HOME -> listOf(
        SidebarItem("Overview"),
        SidebarItem("Notes"),
        SidebarItem("History")
    )
    AppTab.PROJECTILE -> listOf(
        SidebarItem("Parameters"),
        SidebarItem("Materials"),
        SidebarItem("Presets")
    )
    AppTab.SIM_2D -> listOf(
        SidebarItem("Controls"),
        SidebarItem("Overlay"),
        SidebarItem("Markers")
    )
    AppTab.SIM_3D -> listOf(
        SidebarItem("Camera"),
        SidebarItem("Controls"),
        SidebarItem("Environment")
    )
    AppTab.DATA -> listOf(
        SidebarItem("Filter"),
        SidebarItem("Sort"),
        SidebarItem("Columns")
    )
    AppTab.EXPORT -> listOf(
        SidebarItem("Format"),
        SidebarItem("Range"),
        SidebarItem("Options")
    )
}

// ── Main HomeScreen ────────────────────────────────────────────
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    projectConfig: NewProjectConfig? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(AppTab.HOME) }
    var isSidebarExpanded by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // ── Collapsible Sidebar ──────────────────────────────
        CollapsibleSidebar(
            expanded = isSidebarExpanded,
            onExpand = { isSidebarExpanded = true },
            onCollapse = { isSidebarExpanded = false },
            items = sidebarItemsFor(selectedTab),
            onNewProject = { /* Handle new project */ },
            onOpenProject = { /* Handle open project */ },
            onSaveProject = { /* Handle save */ },
            onCloseProject = { /* Handle close */ },
            onOpenSettings = { showSettingsDialog = true }
        )

        // ── Main Content ──────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Top Bar
            TopTabBar(
                selectedTab = selectedTab,
                projectName = projectConfig?.name ?: "Untitled Project",
                onTabSelected = { selectedTab = it }
            )

            // Content Area - Using existing screen components
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith
                                fadeOut(animationSpec = tween(200))
                    }
                ) { tab ->
                    when (tab) {
                        AppTab.HOME -> {
                            // Use existing HomeContent from ui.screens
                            HomeContent(viewModel)
                        }
                        AppTab.PROJECTILE -> {
                            // Use existing ProjectileCharacteristicsScreen
                            ProjectileCharacteristicsScreen(viewModel)
                        }
                        AppTab.SIM_2D -> {
                            // Use existing TwoDSimulationScreen
                            TwoDSimulationScreen(
                                projectile = viewModel.projectile,
                                environment = viewModel.environment
                            )
                        }
                        AppTab.SIM_3D -> {
                            // Use existing ThreeDSimulationScreen
                            ThreeDSimulationScreen(viewModel)
                        }
                        AppTab.DATA -> {
                            // Use existing DataScreen
                            DataScreen()
                        }
                        AppTab.EXPORT -> {
                            // Use existing ExportScreen
                            ExportScreen()
                        }
                    }
                }
            }
        }
    }

    // ── Settings Dialog ──────────────────────────────────────
    // In Home.kt - Update the SettingsDialog call
    if (showSettingsDialog) {
        SettingsDialog(
            settings = AppSettings(),
            onChange = { newSettings ->
                // Apply settings
                showSettingsDialog = false
            },
            onDismiss = { showSettingsDialog = false }
        )
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
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp)
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
                    Text(
                        "Saved",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppTab.values().forEach { tab ->
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

@Composable
fun TabItem(tab: AppTab, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) TrajectoryColors.Purple.copy(alpha = 0.1f)
    else Color.Transparent
    val contentColor = if (selected) TrajectoryColors.Purple
    else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Spacer(Modifier.width(4.dp))
            Text(
                text = tab.label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(2.dp)
                    .background(TrajectoryColors.Purple, RoundedCornerShape(2.dp))
            )
        }
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
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(200)
            ) + fadeIn(tween(150)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(180)
            ) + fadeOut(tween(120))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(220.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
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
                        IconButton(
                            onClick = onCollapse,
                            modifier = Modifier.size(24.dp)
                        ) {
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
                        items.forEach { item ->
                            SidebarRow(item = item)
                        }
                    }

                    // BOTTOM ZONE: Persistent actions
                    HorizontalDivider(color = TrajectoryColors.Divider)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        SidebarSectionLabel("Project")
                        SidebarActionRow(
                            "New Project",
                            Icons.Default.CreateNewFolder,
                            onNewProject
                        )
                        SidebarActionRow(
                            "Open Project",
                            Icons.Default.FolderOpen,
                            onOpenProject
                        )
                        SidebarActionRow(
                            "Save",
                            Icons.Default.Save,
                            onSaveProject
                        )
                        SidebarActionRow(
                            "Close Project",
                            Icons.Default.Close,
                            onCloseProject,
                            Color(0xFFDC2626)
                        )

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

// ── Missing Imports ─────────────────────────────────────────────
