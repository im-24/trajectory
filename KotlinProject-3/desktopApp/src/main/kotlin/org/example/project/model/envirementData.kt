data class EnvironmentData(
    var gravity: Double = 9.81,           // m/s²
    var airDensity: Double = 1.225,       // kg/m³ at sea level
    var windSpeed: Double = 0.0,          // m/s
    var windDirection: Double = 0.0,      // degrees from North
    var temperature: Double = 20.0,       // Celsius
    var pressure: Double = 101325.0,      // Pascals
    var humidity: Double = 0.5            // 0-1
)
