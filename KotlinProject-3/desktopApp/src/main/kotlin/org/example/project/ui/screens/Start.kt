package org.example.project.ui.screens// Start.kt
import SecondaryButtonLarge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import org.example.project.ui.them.MeshGradientBackground
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import mainButtonLarge
import org.example.project.RecentProjectsTable
import org.example.project.StartPageTopBar
import org.example.project.ui.them.TrajectoryColors
import org.example.project.ui.them.TrajectoryTyp
import javax.swing.filechooser.FileFilter

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
    Dialogblure: Dp,
    recentProjects: List<RecentProject>,
    onNewProject: () -> Unit,
    onOpenProject: () -> Unit,
    onOpenRecent: (RecentProject) -> Unit,
    onSearch: (String) -> Unit,
    onInfoRequest: ()   -> Unit ,
) {
    var searchQuery by remember { mutableStateOf("") }
    val projectManager = remember { ProjectManager() }
    var projectsList by remember { mutableStateOf(recentProjects) }
    var masklistcoordinat by remember {mutableStateOf(Offset.Zero) }
    var listwidth by remember { mutableStateOf(0) }
    var listheight by remember { mutableStateOf(0) }

    val projectlist = GenericShape{size , _ ->
        moveTo(masklistcoordinat.x , (masklistcoordinat.y))
        lineTo(masklistcoordinat.x+ listwidth , masklistcoordinat.y  )
        lineTo(masklistcoordinat.x+ listwidth , masklistcoordinat.y+listheight  )
        lineTo(masklistcoordinat.x , masklistcoordinat.y+listheight)
        close()
    }

    // Load saved projects on startup
    LaunchedEffect(Unit) {
        projectsList = projectManager.loadRecentProjects()
    }
    MeshGradientBackground (blurposition = projectlist ){

        Column(modifier = Modifier.fillMaxSize()
            .padding(horizontal = 32.dp)
            .blur(Dialogblure)) {

            // ── Top Bar ──────────────────────────────────────

            StartPageTopBar(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it; onSearch(it) } ,
                appInfo = onInfoRequest
            )

            Column(
                modifier = Modifier.fillMaxWidth().weight(4f)
                    .padding(16.dp,48.dp),
            )
            {
                    // App title
                    Text(
                        text = "TRAJECTORY",
                        style = MaterialTheme.typography.displayLarge,
                        color = TrajectoryColors.Background
                    )
                Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Model . Simulate . Predict",
                        style = MaterialTheme.typography.displayMedium,
                        color = TrajectoryColors.PurpleLight
                    )
                Spacer(Modifier.height(56.dp))
            }

            Row(
                modifier = Modifier.weight(5f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                Column(modifier = Modifier.weight(1f).padding(32.dp)
                    .fillMaxHeight()
                    ,verticalArrangement = Arrangement.Center,) {

                    mainButtonLarge (onNewProject){
                        Text(
                            "New project",
                            fontFamily = TrajectoryTyp.premierfont,
                            fontWeight = MaterialTheme.typography.displayLarge.fontWeight,

                            color = TrajectoryColors.TextPrimary
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    SecondaryButtonLarge  (
                        onClick = {
                            val project = projectManager.openExistingProject()
                            if (project != null) {
                                projectsList = projectManager.loadRecentProjects()
                                onOpenProject()
                            }
                        },

                    )
                    {
                        Text(
                            "Open project",
                            color = TrajectoryColors.Background,
                            fontWeight = FontWeight.Medium,
                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        )

                    }


                }

                Column(
                    modifier = Modifier.weight(2f),
                    Arrangement.Top,
                )
                {


                    Box(modifier = Modifier
                    .height(275.dp)
                        .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                        .onGloballyPositioned {coordinates ->
                            listwidth = coordinates.size.width
                            listheight = coordinates.size.height
                            masklistcoordinat = coordinates.positionInWindow()

                        }

                    .border(BorderStroke(width = 1.dp, color = TrajectoryColors.Divider.copy(0.2f))
                        ,shape = RoundedCornerShape(8.dp))

                    )
                    {

                        RecentProjectsTable(
                            projects = projectsList.filter { project ->
                            searchQuery.isEmpty() ||
                                    project.name.contains(searchQuery, ignoreCase = true) ||
                                    project.path.contains(searchQuery, ignoreCase = true)
                            },
                            onOpen = { project ->
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
                    TextButton(
                        onClick = {
                            projectManager.clearRecentProjects()
                            projectsList = emptyList()
                        },
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                            .align(Alignment.End).padding(16.dp).weight(1f),
                    )
                    {
                        Text(
                            "Clear recent projects",
                            color = TrajectoryColors.TextMuted,
                            fontFamily = TrajectoryTyp.secondaryfont,
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,

                            )
                    }
                }


            }

        // ── Footer ───────────────────────────────────────


        Row(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            Text("Developed by ", color = TrajectoryColors.Background, fontSize = 12.sp)

            Text(
                "WELLEDG",
                color = TrajectoryColors.LimeGreen,
                fontFamily = TrajectoryTyp.welledge ,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(" team", color = TrajectoryColors.Background, fontSize = 12.sp)
        }}
    }
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