import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment

data class RecentProject(
    val name: String,
    val lastOpened: String,
    val path: String,
    val sizeMB: Int
)

@Composable
fun StartPage(
    recentProjects: List<RecentProject>,
    onNewProject: () -> Unit,
    onOpenProject: () -> Unit,
    onOpenRecent: (RecentProject) -> Unit,
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    MeshGradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────
            StartPageTopBar(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it; onSearch(it) }
            )

            // ── Main Content ─────────────────────────────────
            Row(modifier = Modifier.fillMaxSize()) {

                // Left: Branding + Actions
                Column(
                    modifier = Modifier
                        .width(380.dp)
                        .fillMaxHeight()
                        .padding(start = 56.dp, top = 80.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    // App title
                    Text(
                        text = "Trajectory",
                        style = MaterialTheme.typography.displayLarge,
                        color = TrajectoryColors.TextPrimary
                    )

                    Spacer(Modifier.height(8.dp))

                    // Tagline
                    Text(
                        text = "Model. Simulate. Visualize.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = TrajectoryColors.TaglineColor,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(Modifier.height(72.dp))

                    // New Project button (purple)
                    Button(
                        onClick = onNewProject,
                        modifier = Modifier.width(180.dp).height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrajectoryColors.Purple
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            "New project",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Open Project button (lime green)
                    Button(
                        onClick = onOpenProject,
                        modifier = Modifier.width(180.dp).height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrajectoryColors.LimeGreen
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            "Open project",
                            color = TrajectoryColors.TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Right: Recent Projects Table
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, end = 40.dp, start = 20.dp)
                ) {
                    RecentProjectsTable(
                        projects = recentProjects,
                        onOpen = onOpenRecent
                    )
                }
            }

            // ── Footer ───────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Developed by ", color = TrajectoryColors.TextMuted, fontSize = 12.sp)
                    Text(
                        "WELLEDG",
                        color = TrajectoryColors.LimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(" team", color = TrajectoryColors.TextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}