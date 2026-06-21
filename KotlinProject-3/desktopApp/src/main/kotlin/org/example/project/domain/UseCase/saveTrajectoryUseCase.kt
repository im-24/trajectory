package org.example.project.domain.usecases

import org.example.project.domain.Trajectory
import org.example.project.domain.repositories.TrajectoryRepository

class SaveTrajectoryUseCase(
    private val repository: TrajectoryRepository
) {
    suspend operator fun invoke(trajectory: Trajectory): Result<Unit> {
        return repository.save(trajectory)
    }
}