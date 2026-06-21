package presentation.viewmodels

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.Environment
import org.example.project.domain.Projectile
import org.example.project.domain.Trajectory
import org.example.project.domain.usecases.SaveTrajectoryUseCase
import org.example.project.domain.usecases.LoadTrajectoriesUseCase
import data.models.DragModel
import org.example.project.domain.UseCase.CalculateTrajectoryUseCase

class HomeViewModel(
    private val calculateTrajectoryUseCase: CalculateTrajectoryUseCase,
    private val saveTrajectoryUseCase: SaveTrajectoryUseCase,
    private val loadTrajectoriesUseCase: LoadTrajectoriesUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    // Domain state
    var projectile by mutableStateOf(Projectile())
        private set

    var environment by mutableStateOf(Environment())
        private set

    // UI-only state
    var isProjectileExpanded by mutableStateOf(true)
    var isEnvironmentExpanded by mutableStateOf(true)

    // ── Projectile update functions ──
    fun updateProjectileMass(mass: Double) {
        projectile = projectile.copy(mass = mass)
    }

    fun updateProjectileRadius(radius: Double) {
        projectile = projectile.copy(radius = radius)
    }

    fun updateProjectileMaterial(material: String) {
        projectile = projectile.copy(material = material)
    }
    // In HomeViewModel.kt - Add these functions


    fun updateEnvironment(environment: Environment) {
        this.environment = environment
    }
    // In HomeViewModel.kt - Add this function
    fun updateProjectile(projectile: Projectile) {
        this.projectile = projectile
    }
// In HomeViewModel.kt

    fun updateProjectileDragModel(model: org.example.project.domain.DragModel) {
        projectile = projectile.copy(dragModel = model)
    }
    fun updateProjectileBallisticCoefficient(value: Double) {
        projectile = projectile.copy(ballisticCoefficient = value)
    }

    fun updateProjectileSpinRate(value: Double) {
        projectile = projectile.copy(spinRate = value)
    }

    fun updateProjectileSpinAxisYaw(value: Double) {
        projectile = projectile.copy(spinAxisYaw = value)
    }

    fun updateProjectileSpinAxisPitch(value: Double) {
        projectile = projectile.copy(spinAxisPitch = value)
    }

    // ── Environment update functions ──
    fun updateGravity(gravity: Double) {
        environment = environment.copy(gravity = gravity)
    }

    fun updateAirDensity(density: Double) {
        environment = environment.copy(airDensity = density)
    }

    fun updateWindSpeed(speed: Double) {
        environment = environment.copy(windSpeed = speed)
    }

    fun updateWindDirection(direction: Double) {
        environment = environment.copy(windDirection = direction)
    }

    fun updateTemperature(temp: Double) {
        environment = environment.copy(temperature = temp)
        updateAirDensityFromConditions()
    }

    fun updatePressure(pressure: Double) {
        environment = environment.copy(pressure = pressure)
        updateAirDensityFromConditions()
    }

    fun updateHumidity(humidity: Double) {
        environment = environment.copy(humidity = humidity)
        updateAirDensityFromConditions()
    }

    fun updateAltitude(value: Double) {
        environment = environment.copy(altitude = value)
    }

    fun updateTemperatureLapseRate(value: Double) {
        environment = environment.copy(temperatureLapseRate = value)
    }

    fun updateWindGustSpeed(value: Double) {
        environment = environment.copy(windGustSpeed = value)
    }

    fun updateWindGustFrequency(value: Double) {
        environment = environment.copy(windGustFrequency = value)
    }

    fun updateTurbulenceIntensity(value: Double) {
        environment = environment.copy(turbulenceIntensity = value)
    }

    private fun updateAirDensityFromConditions() {
        val baseDensity = environment.pressure / (287.05 * (environment.temperature + 273.15))
        environment = environment.copy(airDensity = baseDensity)
    }

    // ── UI toggle functions ──
    fun toggleProjectileExpanded() {
        isProjectileExpanded = !isProjectileExpanded
    }

    fun toggleEnvironmentExpanded() {
        isEnvironmentExpanded = !isEnvironmentExpanded
    }

    // ── Trajectory calculation ──
    fun calculateTrajectory(
        initialVelocity: Double,
        launchElevation: Double,
        launchAzimuth: Double,
        initialHeight: Double
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = calculateTrajectoryUseCase(
                projectile = projectile,
                environment = environment,
                initialVelocity = initialVelocity,
                launchElevation = launchElevation,
                launchAzimuth = launchAzimuth,
                initialHeight = initialHeight
            )

            result.fold(
                onSuccess = { trajectory ->
                    saveTrajectoryUseCase(trajectory)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        trajectory = trajectory
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            )
        }
    }
}

data class HomeUiState(
    val projectile: Projectile = Projectile(),
    val environment: Environment = Environment(),
    val trajectory: Trajectory? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val trajectories: List<Trajectory> = emptyList()
)