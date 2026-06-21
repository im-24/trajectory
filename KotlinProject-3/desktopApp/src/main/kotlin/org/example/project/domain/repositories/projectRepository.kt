package org.example.project.domain.repositories

import kotlinx.serialization.Serializable

interface ProjectRepository {
    suspend fun save(project: Project): Result<Unit>
    suspend fun loadAll(): Result<List<Project>>
    suspend fun loadByPath(path: String): Result<Project?>
    suspend fun delete(path: String): Result<Unit>
    suspend fun clearRecent(): Result<Unit>
}

data class Project(
    val name: String,
    val location: String,
    val lastOpened: Long,
    val sizeMB: Long,
    val config: ProjectConfig
)

@Serializable
data class ProjectConfig(
    val name: String = "Untitled",
    val importFilePath: String = "",
    val autoSave: Boolean = true,
    val autoSaveIntervalMinutes: Int = 5
)