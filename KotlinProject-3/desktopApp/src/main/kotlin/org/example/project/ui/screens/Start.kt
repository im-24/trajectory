package org.example.project.ui.screens// Start.kt
import androidx.compose.foundation.BorderStroke
import org.example.project.ui.them.MeshGradientBackground
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.platform.Font
import java.io.File
import androidx.compose.ui.text.font.FontFamily

import javax.swing.JFileChooser
import javax.swing.JOptionPane
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.example.project.RecentProjectsTable
import org.example.project.StartPageTopBar
import org.example.project.ui.them.TrajectoryColors
import org.example.project.ui.them.TrajectoryTheme
import org.example.project.ui.them.premierfont
import org.example.project.ui.them.welledge
import java.awt.Color
import javax.swing.filechooser.FileFilter
import javax.swing.plaf.basic.BasicBorders

@Serializable
data class ProjectMetadata(
    val name: String,
    val path: String,
    val lastOpened: Long,
    val sizeMB: Long,
    val config: ProjectConfigData
)

@Serializable
data class ProjectConfigData(
    val name: String,
    val location: String,
    val importFilePath: String = "",
    val autoSave: Boolean = true,
    val autoSaveIntervalMinutes: Int = 5
)

data class RecentProject(
    val name: String,
    val lastOpened: String,
    val path: String,
    val sizeMB: Int
)

@Composable
fun StartPage(
    recentProjects: List<RecentProject>,
    onNewProject: () -> Unit,
    onOpenProject: () -> Unit,
    onOpenRecent: (RecentProject) -> Unit,
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val projectManager = remember { ProjectManager() }
    var projectsList by remember { mutableStateOf(recentProjects) }

    // Load saved projects on startup
    LaunchedEffect(Unit) {
        projectsList = projectManager.loadRecentProjects()
    }

    MeshGradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────
            StartPageTopBar(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it; onSearch(it) }
            )

            // ── Main Content ────────────────────────────────
            Column(modifier = Modifier.fillMaxSize()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(start = 56.dp, top = 80.dp),
                ) {
                    // App title
                    Text(
                        text = "TRAJECTORY",
                        style = MaterialTheme.typography.displayLarge,
                        color = TrajectoryColors.Background
                    )


                }
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(32.dp)   ,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,

                )
                {
                    Button(
                        onClick = onNewProject,
                        modifier = Modifier.width(180.dp).height(48.dp).defaultMinSize(1.dp , 1.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background.copy(0f),
                        ),
                        contentPadding = PaddingValues(0.dp), // Clears inner padding
                   // Clears min siz

                        border = BorderStroke(1.dp , TrajectoryColors.Background) ,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            "New project",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }

                        Spacer(Modifier.height(12.dp))

                        // Open Project button (lime green) - with file chooser
                        Button(
                            onClick = {
                                val project = projectManager.openExistingProject()
                                if (project != null) {
                                    // Update the recent projects list
                                    projectsList = projectManager.loadRecentProjects()
                                    // Call the onOpenProject callback with the project data
                                    onOpenProject()
                                    // You can also pass the project data to navigate
                                }
                            },
                            modifier = Modifier.width(180.dp).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TrajectoryColors.LimeGreen
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        ) {
                            Text(
                                "Open project",
                                color = TrajectoryColors.TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Clear recent projects button
                        TextButton(
                            onClick = {
                                projectManager.clearRecentProjects()
                                projectsList = emptyList()
                            },
                            modifier = Modifier.width(180.dp)
                        ) {
                            Text(
                                "Clear recent projects",
                                fontSize = 11.sp,
                                color = TrajectoryColors.TextMuted
                            )
                        }
                    }
                }
                    // New Project button (purple)


                // Right: Recent Projects Table
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, end = 40.dp, start = 20.dp)
                ) {
                    RecentProjectsTable(
                        projects = projectsList.filter { project ->
                            searchQuery.isEmpty() ||
                                    project.name.contains(searchQuery, ignoreCase = true) ||
                                    project.path.contains(searchQuery, ignoreCase = true)
                        },
                        onOpen = { project ->
                            // Open the selected recent project
                            val loadedProject = projectManager.loadProjectFromPath(project.path)
                            if (loadedProject != null) {
                                onOpenRecent(project)
                            } else {
                                JOptionPane.showMessageDialog(
                                    null,
                                    "Could not load project from: ${project.path}\nThe file may have been moved or deleted.",
                                    "Project Not Found",
                                    JOptionPane.ERROR_MESSAGE
                                )
                                // Refresh the list
                                projectsList = projectManager.loadRecentProjects()
                            }
                        }
                    )
                }

    }
            // ── Footer ───────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                contentAlignment = Alignment.BottomCenter,


            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Developed by ", color = TrajectoryColors.TextMuted, fontSize = 12.sp)

                    Text(
                        "WELLEDG",
                        color = TrajectoryColors.LimeGreen,
                        fontFamily = welledge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(" team", color = TrajectoryColors.TextMuted, fontSize = 12.sp)
                }

        } }
    }


// Project Manager class to handle project operations
class ProjectManager {

    private val projectsDir = File(System.getProperty("user.home"), ".trajectory/projects")
    private val metadataFile = File(System.getProperty("user.home"), ".trajectory/recent_projects.json")
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        projectsDir.mkdirs()
        metadataFile.parentFile?.mkdirs()
    }

    /**
     * Save a new project and create its directory structure
     */
    fun saveNewProject(config: ProjectConfigData): Boolean {
        return try {
            val projectDir = File(config.location, config.name)
            if (!projectDir.exists()) {
                projectDir.mkdirs()
            }

            // Create project subdirectories
            File(projectDir, "data").mkdir()
            File(projectDir, "exports").mkdir()
            File(projectDir, "simulations").mkdir()

            // Save project metadata
            val metadata = ProjectMetadata(
                name = config.name,
                path = projectDir.absolutePath,
                lastOpened = System.currentTimeMillis(),
                sizeMB = calculateDirectorySize(projectDir),
                config = config
            )

            // Save to recent projects list
            addToRecentProjects(metadata)

            // Save config file in project directory
            val configFile = File(projectDir, "project_config.json")
            configFile.writeText(json.encodeToString(config))

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Open an existing project via file chooser
     */
    fun openExistingProject(): ProjectMetadata? {
        val fileChooser = JFileChooser(projectsDir)
        fileChooser.dialogTitle = "Open Trajectory Project"
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        fileChooser.fileFilter = object : FileFilter() {
            override fun accept(f: File): Boolean {
                return f.isDirectory && File(f, "project_config.json").exists() || f.isDirectory && f == projectsDir
            }

            override fun getDescription(): String {
                return "Trajectory Project Directory"
            }
        }

        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedDir = fileChooser.selectedFile
            return loadProjectFromPath(selectedDir.absolutePath)
        }
        return null
    }

    /**
     * Load project from a given path
     */
    fun loadProjectFromPath(path: String): ProjectMetadata? {
        return try {
            val projectDir = File(path)
            val configFile = File(projectDir, "project_config.json")

            if (!configFile.exists()) {
                return null
            }

            val config = json.decodeFromString<ProjectConfigData>(configFile.readText())
            val metadata = ProjectMetadata(
                name = config.name,
                path = projectDir.absolutePath,
                lastOpened = System.currentTimeMillis(),
                sizeMB = calculateDirectorySize(projectDir),
                config = config
            )

            // Update last opened time
            addToRecentProjects(metadata)

            metadata
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Add a project to the recent projects list
     */
    private fun addToRecentProjects(metadata: ProjectMetadata) {
        val projects = loadRecentProjectsMetadata().toMutableList()

        // Remove if already exists
        projects.removeAll { it.path == metadata.path }

        // Add to front
        projects.add(0, metadata)

        // Keep only last 20 projects
        val trimmed = projects.take(20)

        // Save to file
        metadataFile.writeText(json.encodeToString(trimmed))
    }

    /**
     * Load recent projects metadata
     */
    private fun loadRecentProjectsMetadata(): List<ProjectMetadata> {
        return try {
            if (metadataFile.exists()) {
                json.decodeFromString<List<ProjectMetadata>>(metadataFile.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Load recent projects as RecentProject objects for display
     */
    fun loadRecentProjects(): List<RecentProject> {
        return loadRecentProjectsMetadata().map { metadata ->
            RecentProject(
                name = metadata.name,
                lastOpened = formatTimeAgo(metadata.lastOpened),
                path = metadata.path,
                sizeMB = metadata.sizeMB.toInt()
            )
        }
    }

    /**
     * Clear all recent projects
     */
    fun clearRecentProjects() {
        metadataFile.delete()
    }

    /**
     * Delete a project permanently
     */
    fun deleteProject(projectPath: String): Boolean {
        return try {
            val projectDir = File(projectPath)
            projectDir.deleteRecursively()
            // Remove from recent list
            val projects = loadRecentProjectsMetadata().toMutableList()
            projects.removeAll { it.path == projectPath }
            metadataFile.writeText(json.encodeToString(projects))
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Calculate directory size in MB
     */
    private fun calculateDirectorySize(directory: File): Long {
        return try {
            val bytes = directory.walk().filter { it.isFile }.sumOf { it.length() }
            bytes / (1024 * 1024)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Format timestamp as "X hours/days ago"
     */
    private fun formatTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000} minutes ago"
            diff < 86400_000 -> "${diff / 3600_000} hours ago"
            diff < 604800_000 -> "${diff / 86400_000} days ago"
            else -> "${diff / 604800_000} weeks ago"
        }
    }
}