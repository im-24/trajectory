package org.example.project.domain.UseCase

import org.example.project.domain.Environment
import org.example.project.domain.Projectile
import org.example.project.domain.Trajectory
import org.example.project.domain.repositories.TrajectoryRepository


class CalculateTrajectoryUseCase(
    private val repository: TrajectoryRepository
) {
    suspend operator fun invoke(
        projectile: Projectile,
        environment: Environment,
        initialVelocity: Double,
        launchElevation: Double,
        launchAzimuth: Double = 0.0,
        initialHeight: Double = 0.0
    ): Result<Trajectory> {
        return repository.calculateTrajectory(
            projectile = projectile,
            environment = environment,
            initialVelocity = initialVelocity,
            launchElevation = launchElevation,
            launchAzimuth = launchAzimuth,
            initialHeight = initialHeight
        )
    }
}