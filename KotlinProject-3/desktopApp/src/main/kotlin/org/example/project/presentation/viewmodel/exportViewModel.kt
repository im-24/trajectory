package presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.example.project.domain.Trajectory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.usecases.ExportTrajectoryUseCase
import org.example.project.domain.usecases.GenerateReportUseCase
import org.example.project.domain.usecases.LoadTrajectoriesUseCase
import org.example.project.domain.usecases.ReportSettings

class ExportViewModel(
    private val loadTrajectoriesUseCase: LoadTrajectoriesUseCase,
    private val exportTrajectoryUseCase: ExportTrajectoryUseCase,
    private val generateReportUseCase: GenerateReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState

    // Properties are public mutable - use them directly in UI
    var selectedTrajectoryId by mutableStateOf<String?>(null)
        private set

    var exportFormat by mutableStateOf("CSV")
    var reportFormat by mutableStateOf("WORD")
    var exportLocation by mutableStateOf("")
    var reportLocation by mutableStateOf("")

    var isGenerating by mutableStateOf(false)
        private set

    var progressMessage by mutableStateOf("")
        private set

    init {
        loadTrajectories()
    }

    fun loadTrajectories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = loadTrajectoriesUseCase()
            result.fold(
                onSuccess = { trajectories ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        trajectories = trajectories
                    )
                    if (trajectories.isNotEmpty() && selectedTrajectoryId == null) {
                        selectedTrajectoryId = trajectories.first().id
                    }
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

    fun selectTrajectory(id: String) {
        selectedTrajectoryId = id
    }

    fun exportData() {
        val trajectory = getSelectedTrajectory()
        if (trajectory == null || exportLocation.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Please select a trajectory and export location"
            )
            return
        }

        viewModelScope.launch {
            isGenerating = true
            progressMessage = "Exporting data..."

            val fileName = "trajectory_${trajectory.id}.${exportFormat.lowercase()}"
            val filePath = "$exportLocation/$fileName"

            val result = exportTrajectoryUseCase.saveToFile(trajectory, exportFormat, filePath)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        lastExportPath = filePath
                    )
                    progressMessage = "Export completed!"
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Export failed: ${error.message}"
                    )
                }
            )

            isGenerating = false
        }
    }

    fun generateReport(settings: ReportSettings) {
        val trajectory = getSelectedTrajectory()
        if (trajectory == null) {
            _uiState.value = _uiState.value.copy(
                error = "Please select a trajectory"
            )
            return
        }

        viewModelScope.launch {
            isGenerating = true
            progressMessage = "Generating report..."

            val result = generateReportUseCase(trajectory, settings)

            result.fold(
                onSuccess = { filePath ->
                    _uiState.value = _uiState.value.copy(
                        lastReportPath = filePath
                    )
                    progressMessage = "Report generated!"
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Report generation failed: ${error.message}"
                    )
                }
            )

            isGenerating = false
        }
    }

    private fun getSelectedTrajectory(): Trajectory? {
        return uiState.value.trajectories.find { it.id == selectedTrajectoryId }
    }
}

data class ExportUiState(
    val trajectories: List<Trajectory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastExportPath: String? = null,
    val lastReportPath: String? = null
)