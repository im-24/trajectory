package presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.domain.Projectile  // Use domain Projectile
import org.example.project.presentation.ui.them.TrajectoryColors

@Composable
fun HomeHeader(
    projectile: Projectile,  // Use domain Projectile
    projectName: String = "Untitled Project",
    onProjectileUpdate: (Projectile) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = projectName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrajectoryColors.Purple
                )
                Text(
                    text = "Projectile: ${projectile.name}",
                    fontSize = 14.sp,
                    color = TrajectoryColors.TextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { /* TODO: Save project */ }) {
                    Icon(Icons.Default.Save, "Save")
                }
                IconButton(onClick = { /* TODO: Export */ }) {
                    Icon(Icons.Default.Share, "Export")
                }
            }
        }
    }
}