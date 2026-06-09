import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import ui.screens.ProjectileData
import kotlin.math.PI



class HomeViewModel {
    // Projectile state
    var projectile by mutableStateOf(ProjectileData())
        private set

    // Environment state
    var environment by mutableStateOf(EnvironmentData())
        private set

    // UI state for expanded sections
    var isProjectileExpanded by mutableStateOf(true)
    var isEnvironmentExpanded by mutableStateOf(true)

    // Update projectile properties
    fun updateProjectileMass(mass: Double) {
        projectile = projectile.copy(mass = mass)
    }

    fun updateProjectileRadius(radius: Double) {
        val volume = (4.0/3.0) * PI * radius * radius * radius
        projectile = projectile.copy(
            radius = radius,
            diameter = radius * 2,
            volume = volume
        )
    }

    fun updateProjectileMaterial(material: String) {
        projectile = projectile.copy(material = material)
    }

    fun updateProjectileColor(color: Color) {
        projectile = projectile.copy(color = color)
    }

    // Update environment properties
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
        // Air density changes with temperature
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

    private fun updateAirDensityFromConditions() {
        // Simplified air density calculation
        // More accurate formula would include humidity
        val baseDensity = environment.pressure / (287.05 * (environment.temperature + 273.15))
        environment = environment.copy(airDensity = baseDensity)
    }
}