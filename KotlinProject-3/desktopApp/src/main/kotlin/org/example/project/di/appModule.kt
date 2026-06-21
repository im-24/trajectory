package di

import data.repositories.*
import data.datasources.*
import data.mappers.*
import org.example.project.domain.UseCase.CalculateTrajectoryUseCase
import org.example.project.domain.repositories.ProjectRepository
import org.example.project.domain.repositories.TrajectoryRepository
import org.example.project.domain.usecases.SaveTrajectoryUseCase
import org.example.project.domain.usecases.LoadTrajectoriesUseCase
import org.example.project.domain.usecases.ExportTrajectoryUseCase
import org.example.project.domain.usecases.GenerateReportUseCase
import presentation.viewmodels.HomeViewModel
import presentation.viewmodels.ExportViewModel
import physics.TrajectoryCalculator

object AppModule {

    // Data layer
    private val localDataSource by lazy { LocalDataSource() }
    private val trajectoryMapper by lazy { TrajectoryMapper() }
    private val trajectoryCalculator by lazy { TrajectoryCalculator() }

    // Repositories
    val trajectoryRepository: TrajectoryRepository by lazy {
        TrajectoryRepositoryImpl(
            localDataSource = localDataSource,
            mapper = trajectoryMapper,
            calculator = trajectoryCalculator
        )
    }

    val projectRepository: ProjectRepository by lazy {
        ProjectRepositoryImpl()
    }

    // Use Cases - Fixed with explicit imports
    val calculateTrajectoryUseCase: CalculateTrajectoryUseCase by lazy {
        CalculateTrajectoryUseCase(trajectoryRepository)
    }

    val saveTrajectoryUseCase: SaveTrajectoryUseCase by lazy {
        SaveTrajectoryUseCase(trajectoryRepository)
    }

    val loadTrajectoriesUseCase: LoadTrajectoriesUseCase by lazy {
        LoadTrajectoriesUseCase(trajectoryRepository)
    }

    val exportTrajectoryUseCase: ExportTrajectoryUseCase by lazy {
        ExportTrajectoryUseCase()
    }

    val generateReportUseCase: GenerateReportUseCase by lazy {
        GenerateReportUseCase()
    }

    // ViewModels - for Compose injection
    fun provideHomeViewModel(): HomeViewModel {
        return HomeViewModel(
            calculateTrajectoryUseCase = calculateTrajectoryUseCase,
            saveTrajectoryUseCase = saveTrajectoryUseCase,
            loadTrajectoriesUseCase = loadTrajectoriesUseCase
        )
    }

    fun provideExportViewModel(): ExportViewModel {
        return ExportViewModel(
            loadTrajectoriesUseCase = loadTrajectoriesUseCase,
            exportTrajectoryUseCase = exportTrajectoryUseCase,
            generateReportUseCase = generateReportUseCase
        )
    }
}