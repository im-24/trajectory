package presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.domain.Environment  // Use domain Environment
import org.example.project.presentation.ui.them.TrajectoryColors

@Composable
fun EnvironmentSection(
    environment: Environment,  // Use domain Environment
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onUpdate: (Environment) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Public, "Environment", tint = TrajectoryColors.Purple)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Environment", style = MaterialTheme.typography.titleMedium)
                }
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    "Toggle"
                )
            }

            if (isExpanded) {
                Divider()
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = environment.gravity.toString(),
                            onValueChange = {
                                it.toDoubleOrNull()?.let { onUpdate(environment.copy(gravity = it)) }
                            },
                            label = { Text("Gravity (m/s²)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = environment.airDensity.toString(),
                            onValueChange = {
                                it.toDoubleOrNull()?.let { onUpdate(environment.copy(airDensity = it)) }
                            },
                            label = { Text("Air Density (kg/m³)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = environment.windSpeed.toString(),
                            onValueChange = {
                                it.toDoubleOrNull()?.let { onUpdate(environment.copy(windSpeed = it)) }
                            },
                            label = { Text("Wind Speed (m/s)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = environment.windDirection.toString(),
                            onValueChange = {
                                it.toDoubleOrNull()?.let { onUpdate(environment.copy(windDirection = it)) }
                            },
                            label = { Text("Wind Direction (°)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = environment.temperature.toString(),
                            onValueChange = {
                                it.toDoubleOrNull()?.let { onUpdate(environment.copy(temperature = it)) }
                            },
                            label = { Text("Temperature (°C)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = environment.pressure.toString(),
                            onValueChange = {
                                it.toDoubleOrNull()?.let { onUpdate(environment.copy(pressure = it)) }
                            },
                            label = { Text("Pressure (Pa)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}