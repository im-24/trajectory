package data.repositories

import data.datasources.LocalDataSource
import data.mappers.TrajectoryMapper
import org.example.project.domain.Environment
import org.example.project.domain.Projectile
import org.example.project.domain.Trajectory
import org.example.project.domain.repositories.TrajectoryRepository
import physics.TrajectoryCalculator

class TrajectoryRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val mapper: TrajectoryMapper,
    private val calculator: TrajectoryCalculator
) : TrajectoryRepository {

    override suspend fun save(trajectory: Trajectory): Result<Unit> = runCatching {
        val dataModel = mapper.toDataModel(trajectory)
        localDataSource.saveTrajectory(dataModel)
    }

    override suspend fun loadAll(): Result<List<Trajectory>> = runCatching {
        localDataSource.loadAllTrajectories()
            .map { mapper.toDomainModel(it) }
    }

    override suspend fun loadById(id: String): Result<Trajectory?> = runCatching {
        localDataSource.loadTrajectory(id)?.let { mapper.toDomainModel(it) }
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        localDataSource.deleteTrajectory(id)
    }

    override suspend fun calculateTrajectory(
        projectile: Projectile,
        environment: Environment,
        initialVelocity: Double,
        launchElevation: Double,
        launchAzimuth: Double,
        initialHeight: Double
    ): Result<Trajectory> = runCatching {
        val result = calculator.calculateTrajectory(
            projectile = mapper.toDataProjectile(projectile),
            environment = mapper.toDataEnvironment(environment),
            initialVelocity = initialVelocity,
            launchElevation = launchElevation,
            launchAzimuth = launchAzimuth,
            initialHeight = initialHeight
        )
        mapper.toDomainModel(result)
    }
}