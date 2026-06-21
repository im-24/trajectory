package org.example.project.domain.repositories

import org.example.project.domain.Environment
import org.example.project.domain.Projectile
import org.example.project.domain.Trajectory


interface TrajectoryRepository {
    suspend fun save(trajectory: Trajectory): Result<Unit>
    suspend fun loadAll(): Result<List<Trajectory>>
    suspend fun loadById(id: String): Result<Trajectory?>
    suspend fun delete(id: String): Result<Unit>
    suspend fun calculateTrajectory(
        projectile: Projectile,
        environment: Environment,
        initialVelocity: Double,
        launchElevation: Double,
        launchAzimuth: Double,
        initialHeight: Double
    ): Result<Trajectory>
}