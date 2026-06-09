package ui.Navigation

import RecentProject
import StartPage
import androidx.compose.runtime.*
import org.expample.projcet.viewmodel.HomeViewModel.HomeScreen
import ui.screens.*

sealed class Screen {
    object Start                             : Screen()
    data class Workspace(val config: NewProjectConfig) : Screen()
}

@Composable
fun AppNavigation() {
    var currentScreen       by remember { mutableStateOf<Screen>(Screen.Start) }
    var showNewProjectDialog by remember { mutableStateOf(false) }

    when (val screen = currentScreen) {

        is Screen.Start -> {
            StartPage(
                recentProjects = sampleProjects(),
                onNewProject   = { showNewProjectDialog = true },
                onOpenProject  = { /* file picker → navigate to workspace */ },
                onOpenRecent   = { project ->
                    // Build a config from a saved project and navigate
                    currentScreen = Screen.Workspace(
                        NewProjectConfig(
                            name                   = project.name,
                            location               = project.path,
                            importFilePath         = "",
                            autoSave               = true,
                            autoSaveIntervalMinutes = 5
                        )
                    )
                },
                onSearch = { /* filter handled inside StartPage */ }
            )

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

        is Screen.Workspace -> {
            HomeScreen(projectConfig = screen.config)
        }
    }
}

private fun sampleProjects() = listOf(
    RecentProject("Test 1", "5 hours ago", "C:/User/home/...", 54),
    RecentProject("Test 2", "2 days ago",  "C:/User/home/...", 32),
    RecentProject("Test 3", "1 week ago",  "C:/User/home/...", 12)
)