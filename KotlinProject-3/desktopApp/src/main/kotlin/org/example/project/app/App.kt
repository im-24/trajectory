package org.example.project

import org.example.project.ui.screens.ProjectManager
import org.example.project.ui.screens.RecentProject
import org.example.project.ui.screens.StartPage
import org.example.project.ui.them.TrajectoryTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import org.bouncycastle.jcajce.provider.asymmetric.ec.GMSignatureSpi
import org.example.project.ui.screens.infoDialog
import ui.screens.NewProjectConfig
import ui.screens.NewProjectDialog


// ── Screen state ───────────────────────────────────────────────
sealed class Screen {
    object Start : Screen()
    data class Workspace(val config: NewProjectConfig) : Screen()
}

@Composable
fun App() {
    var showdialog by remember { mutableStateOf(false) }
    var dialogblur by remember {mutableStateOf(0.dp)}
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Start) }
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showDialoginfo by remember { mutableStateOf(false) }
    var returntostart by remember { mutableStateOf(false) } /*todo : add close project use ruturentostart */
    TrajectoryTheme {

        if (showdialog){
            dialogblur=10.dp
        }else{
            dialogblur=0.dp
        }

        when (val screen = currentScreen) {

            // ── Landing / start page ───────────────────────
            is Screen.Start -> {
                StartPage(
                    Dialogblure = dialogblur ,
                    recentProjects = sampleProjects,
                    onNewProject   = { showNewProjectDialog = true
                                     showdialog = true },
                    onOpenProject  = {
                        val projectManager = ProjectManager()
                    },
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
                    onInfoRequest = {showDialoginfo = true
                                    showdialog = true },
                    onSearch = { /* TODO: filter list */ }
                )

                // Dialog floats on top of StartPage
                if (showNewProjectDialog) {
                    NewProjectDialog(
                        onDismiss = {
                            showdialog = false
                            showNewProjectDialog = false },
                        onCreate  = { config ->
                            showdialog = false
                            showNewProjectDialog = false
                            currentScreen = Screen.Workspace(config)
                        }
                    )
                }
                if (showDialoginfo) {
                    infoDialog(onDismiss = {
                        showdialog = false
                        showDialoginfo = false }

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