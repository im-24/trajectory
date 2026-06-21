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
import org.example.project.domain.Projectile  // Use domain Projectile
import org.example.project.presentation.ui.them.TrajectoryColors

@Composable
fun ProjectileSection(
    projectile: Projectile,  // Use domain Projectile
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onUpdate: (Projectile) -> Unit
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
                    Icon(Icons.Default.Category, "Projectile", tint = TrajectoryColors.Purple)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Projectile", style = MaterialTheme.typography.titleMedium)
                }
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    "Toggle"
                )
            }

            if (isExpanded) {
                Divider()
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = projectile.name,
                        onValueChange = { onUpdate(projectile.copy(name = it)) },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = projectile.mass.toString(),
                            onValueChange = {
                                it.toDoubleOrNull()?.let { onUpdate(projectile.copy(mass = it)) }
                            },
                            label = { Text("Mass (kg)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = projectile.radius.toString(),
                            onValueChange = {
                                it.toDoubleOrNull()?.let { onUpdate(projectile.copy(radius = it)) }
                            },
                            label = { Text("Radius (m)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = projectile.material,
                        onValueChange = { onUpdate(projectile.copy(material = it)) },
                        label = { Text("Material") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}