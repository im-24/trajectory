package data.repositories

import org.example.project.domain.repositories.ProjectRepository
import org.example.project.domain.repositories.Project
import org.example.project.domain.repositories.ProjectConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

class ProjectRepositoryImpl : ProjectRepository {

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

    @kotlinx.serialization.Serializable
    data class ProjectMetadata(
        val name: String,
        val path: String,
        val lastOpened: Long,
        val sizeMB: Long,
        val config: ProjectConfig
    )

    override suspend fun save(project: Project): Result<Unit> = runCatching {
        val projectDir = File(project.location, project.name)
        if (!projectDir.exists()) {
            projectDir.mkdirs()
            File(projectDir, "data").mkdir()
            File(projectDir, "exports").mkdir()
            File(projectDir, "simulations").mkdir()
        }

        val metadata = ProjectMetadata(
            name = project.name,
            path = projectDir.absolutePath,
            lastOpened = System.currentTimeMillis(),
            sizeMB = calculateDirectorySize(projectDir),
            config = project.config
        )

        addToRecentProjects(metadata)

        val configFile = File(projectDir, "project_config.json")
        configFile.writeText(json.encodeToString(metadata.config))
    }

    override suspend fun loadAll(): Result<List<Project>> = runCatching {
        loadRecentProjectsMetadata().map { metadata ->
            Project(
                name = metadata.name,
                location = metadata.path,
                lastOpened = metadata.lastOpened,
                sizeMB = metadata.sizeMB,
                config = metadata.config
            )
        }
    }

    override suspend fun loadByPath(path: String): Result<Project?> = runCatching {
        val projectDir = File(path)
        val configFile = File(projectDir, "project_config.json")

        if (!configFile.exists()) return@runCatching null

        val config = json.decodeFromString<ProjectConfig>(configFile.readText())
        val sizeMB = calculateDirectorySize(projectDir)
        val metadata = ProjectMetadata(
            name = config.name,
            path = projectDir.absolutePath,
            lastOpened = System.currentTimeMillis(),
            sizeMB = sizeMB,
            config = config
        )

        addToRecentProjects(metadata)

        Project(
            name = metadata.name,
            location = metadata.path,
            lastOpened = metadata.lastOpened,
            sizeMB = metadata.sizeMB,
            config = metadata.config
        )
    }

    override suspend fun delete(path: String): Result<Unit> = runCatching {
        val projectDir = File(path)
        projectDir.deleteRecursively()

        val projects = loadRecentProjectsMetadata().toMutableList()
        projects.removeAll { it.path == path }
        metadataFile.writeText(json.encodeToString(projects))
    }

    override suspend fun clearRecent(): Result<Unit> = runCatching {
        metadataFile.delete()
    }

    private fun addToRecentProjects(metadata: ProjectMetadata) {
        val projects = loadRecentProjectsMetadata().toMutableList()
        projects.removeAll { it.path == metadata.path }
        projects.add(0, metadata)
        val trimmed = projects.take(20)
        metadataFile.writeText(json.encodeToString(trimmed))
    }

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

    private fun calculateDirectorySize(directory: File): Long {
        return try {
            val bytes = directory.walk().filter { it.isFile }.sumOf { it.length() }
            bytes / (1024 * 1024)
        } catch (e: Exception) {
            0
        }
    }
}