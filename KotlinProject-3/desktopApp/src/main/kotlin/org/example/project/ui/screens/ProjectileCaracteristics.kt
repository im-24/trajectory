// ui/screens/ProjectileCharacteristicsScreen.kt
package ui.screens

import TrajectoryColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.models.ProjectileData

@Composable
fun ProjectileCharacteristicsScreen(
    projectileData: ProjectileData
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Adjust,
                    contentDescription = null,
                    tint = TrajectoryColors.Purple,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Projectile Characteristics",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrajectoryColors.Purple,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Configure projectile properties",
                    fontSize = 14.sp,
                    color = TrajectoryColors.TextSecondary
                )
            }
        }

        // Current Values Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Current Configuration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrajectoryColors.TextPrimary
                )

                Divider(color = TrajectoryColors.Divider)

                PropertyRow("Name", projectileData.name)
                PropertyRow("Mass", "${projectileData.mass} kg")
                PropertyRow("Radius", "${projectileData.radius} m")
                PropertyRow("Diameter", "${projectileData.diameter} m")
                PropertyRow("Volume", "${String.format("%.4f", projectileData.volume)} m³")
                PropertyRow("Material", projectileData.material)
            }
        }

        // Coming Soon Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Badge(
                    containerColor = TrajectoryColors.Purple.copy(alpha = 0.1f),
                    contentColor = TrajectoryColors.Purple
                ) {
                    Text("Coming Soon", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Advanced characteristics editor",
                    fontSize = 14.sp,
                    color = TrajectoryColors.TextSecondary
                )
                Text(
                    text = "Material library, presets, and more will be available",
                    fontSize = 12.sp,
                    color = TrajectoryColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TrajectoryColors.TextSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TrajectoryColors.TextPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
}