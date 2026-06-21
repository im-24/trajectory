package org.example.project.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import di.AppModule
import org.example.project.HomeScreen
import org.example.project.Screen
import org.example.project.domain.repositories.ProjectRepository
import org.example.project.domain.repositories.TrajectoryRepository
import org.example.project.presentation.ui.screens.RecentProject
import org.example.project.presentation.ui.screens.StartPage
import ui.screens.NewProjectConfig
import ui.screens.NewProjectDialog

@Composable
fun Navigation(
    trajectoryRepository: TrajectoryRepository,
    projectRepository: ProjectRepository
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Start) }
    var projectConfig by remember { mutableStateOf<NewProjectConfig?>(null) }
    var showNewProjectDialog by remember { mutableStateOf(false) }

    // Sample projects for demo
    val sampleProjects = listOf(
        RecentProject("Test 1", "5 hours ago", "C:/User/home/...", 54),
        RecentProject("Test 2", "2 days ago", "C:/User/home/...", 32),
        RecentProject("Test 3", "1 week ago", "C:/User/home/...", 12)
    )

    when (currentScreen) {
        is Screen.Start -> {
            StartPage(
                Dialogblure = if (showNewProjectDialog) 10.dp else 0.dp,
                recentProjects = sampleProjects,
                onNewProject = {
                    showNewProjectDialog = true
                },
                onOpenProject = {
                    // Handle opening project
                },
                onOpenRecent = { project ->
                    projectConfig = NewProjectConfig(
                        name = project.name,
                        location = project.path,
                        importFilePath = "",
                        autoSave = true,
                        autoSaveIntervalMinutes = 5
                    )
                    currentScreen = Screen.Workspace(projectConfig!!)
                },
                onSearch = { /* Handle search */ },
                onInfoRequest = { /* Show info dialog */ }
            )

            // Show New Project Dialog
            if (showNewProjectDialog) {
                NewProjectDialog(
                    onDismiss = {
                        showNewProjectDialog = false
                    },
                    onCreate = { config ->
                        showNewProjectDialog = false
                        currentScreen = Screen.Workspace(config)
                    }
                )
            }
        }
        is Screen.Workspace -> {
            val viewModel = AppModule.provideHomeViewModel()
            HomeScreen(
                viewModel = viewModel,
            )
        }
    }
}