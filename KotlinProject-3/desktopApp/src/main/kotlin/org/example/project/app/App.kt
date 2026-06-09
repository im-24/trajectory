package org.example.project

import RecentProject
import StartPage
import TrajectoryTheme
import androidx.compose.runtime.*
import org.expample.projcet.viewmodel.HomeViewModel.HomeScreen
import ui.screens.NewProjectConfig
import ui.screens.NewProjectDialog


// ── Screen state ───────────────────────────────────────────────
sealed class Screen {
    object Start : Screen()
    data class Workspace(val config: NewProjectConfig) : Screen()
}

@Composable
fun App() {
    var currentScreen        by remember { mutableStateOf<Screen>(Screen.Start) }
    var showNewProjectDialog  by remember { mutableStateOf(false) }

    TrajectoryTheme {

        when (val screen = currentScreen) {

            // ── Landing / start page ───────────────────────
            is Screen.Start -> {
                StartPage(
                    recentProjects = sampleProjects,
                    onNewProject   = { showNewProjectDialog = true },
                    onOpenProject  = { /* TODO: file picker */ },
                    onOpenRecent   = { project ->
                        currentScreen = Screen.Workspace(
                            NewProjectConfig(
                                name                    = project.name,
                                location                = project.path,
                                importFilePath          = "",
                                autoSave                = true,
                                autoSaveIntervalMinutes = 5
                            )
                        )
                    },
                    onSearch = { /* TODO: filter list */ }
                )

                // Dialog floats on top of StartPage
                if (showNewProjectDialog) {
                    NewProjectDialog(
                        onDismiss = { showNewProjectDialog = false },
                        onCreate  = { config ->
                            showNewProjectDialog = false
                            currentScreen = Screen.Workspace(config)
                        }
                    )
                }
            }

            // ── Main workspace ─────────────────────────────
            is Screen.Workspace -> {
                HomeScreen(projectConfig = screen.config)
            }
        }
    }
}



// ── Sample data (move to a ViewModel/Repository later) ────────
private val sampleProjects = listOf(
    RecentProject("Test 1", "5 hours ago", "C:/User/home/...", 54),
    RecentProject("Test 2", "2 days ago",  "C:/User/home/...", 32),
    RecentProject("Test 3", "1 week ago",  "C:/User/home/...", 12)
)