package org.example.project

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import di.AppModule
import org.example.project.presentation.navigation.Navigation
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
    TrajectoryTheme {
        Navigation(
            trajectoryRepository = AppModule.trajectoryRepository,
            projectRepository = AppModule.projectRepository
        )
    }
}

// ── Sample data (move to a ViewModel/Repository later) ────────
private val sampleProjects = listOf(
    RecentProject("Test 1", "5 hours ago", "C:/User/home/...", 54),
    RecentProject("Test 2", "2 days ago", "C:/User/home/...", 32),
    RecentProject("Test 3", "1 week ago", "C:/User/home/...", 12)
)