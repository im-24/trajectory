package org.example.project.domain.usecases

import org.example.project.domain.Trajectory
import org.example.project.domain.repositories.TrajectoryRepository

class LoadTrajectoriesUseCase(
    private val repository: TrajectoryRepository
) {
    suspend operator fun invoke(): Result<List<Trajectory>> {
        return repository.loadAll()
    }
}