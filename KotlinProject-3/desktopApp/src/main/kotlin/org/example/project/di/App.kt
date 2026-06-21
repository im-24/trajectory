package org.example.project

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import di.AppModule
import org.example.project.presentation.navigation.Navigation
import org.example.project.presentation.ui.screens.ProjectConfigData
import org.example.project.presentation.ui.screens.ProjectManager
import org.example.project.presentation.ui.screens.RecentProject
import org.example.project.presentation.ui.screens.StartPage
import org.example.project.presentation.ui.screens.infoDialog
import org.example.project.presentation.ui.them.TrajectoryTheme
import ui.screens.NewProjectConfig
import ui.screens.NewProjectDialog

// ── Screen state ───────────────────────────────────────────────
sealed class Screen {
    object Start : Screen()
    data class Workspace(val config: NewProjectConfig) : Screen()
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Start) }
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var dialogBlur by remember { mutableStateOf(0.dp) }

    TrajectoryTheme {
        // Update blur based on dialog state
        dialogBlur = if (showNewProjectDialog || showInfoDialog) 10.dp else 0.dp

        when (val screen = currentScreen) {
            is Screen.Start -> {
                StartPage(
                    Dialogblure = dialogBlur,
                    recentProjects = sampleProjects,
                    onNewProject = {
                        showNewProjectDialog = true
                    },
                    onOpenProject = {
                        // Handle open project
                    },
                    onOpenRecent = { project ->
                        currentScreen = Screen.Workspace(
                            NewProjectConfig(
                                name = project.name,
                                location = project.path,
                                importFilePath = "",
                                autoSave = true,
                                autoSaveIntervalMinutes = 5
                            )
                        )
                    },
                    onSearch = { /* Handle search */ },
                    onInfoRequest = {
                        showInfoDialog = true
                    }
                )

                // Show New Project Dialog
                if (showNewProjectDialog) {
                    NewProjectDialog(
                        onDismiss = {
                            showNewProjectDialog = false
                        },
                        onCreate = { config ->
                            showNewProjectDialog = false
                            // Save the project using ProjectManager
                            val projectManager = ProjectManager()
                            val projectConfig = ProjectConfigData(
                                name = config.name,
                                location = config.location,
                                importFilePath = config.importFilePath,
                                autoSave = config.autoSave,
                                autoSaveIntervalMinutes = config.autoSaveIntervalMinutes
                            )
                            if (projectManager.saveNewProject(projectConfig)) {
                                currentScreen = Screen.Workspace(config)
                            }
                        }
                    )
                }

                // Show Info Dialog
                if (showInfoDialog) {
                    infoDialog(
                        onDismiss = {
                            showInfoDialog = false
                        }
                    )
                }
            }

            is Screen.Workspace -> {
                // FIXED: Actually render the HomeScreen
                val viewModel = AppModule.provideHomeViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    projectConfig = screen.config
                )
            }
        }
    }
}

// ── Sample data ────────────────────────────────────────────────
private val sampleProjects = listOf(
    RecentProject("Test 1", "5 hours ago", "C:/User/home/...", 54),
    RecentProject("Test 2", "2 days ago", "C:/User/home/...", 32),
    RecentProject("Test 3", "1 week ago", "C:/User/home/...", 12)
)