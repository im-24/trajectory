package org.example.project


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.screens.RecentProject

@Composable
fun StartPageTopBar(searchQuery: String, onSearchChange: (String) -> Unit) {
    Row(
        modifier = Modifier.padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(

            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Find Project ...",
                color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.Background ,
                )},
            textStyle = MaterialTheme.typography.labelLarge,

            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.Background
                )
            },
            modifier = Modifier.padding(
                start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp
            ).background(Color.Transparent),
            shape = CircleShape,

            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.Background,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.25f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.5f),
            ),
        )

        Spacer(Modifier.width(12.dp))

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextSecondary
            )
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(_root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextMuted, shape = CircleShape)
        )
    }
}

@Composable
fun RecentProjectsTable(
    projects: List<RecentProject>,
    onOpen: (RecentProject) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Name",   modifier = Modifier.weight(2f), color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextSecondary, fontSize = 13.sp)
            Text("Recent", modifier = Modifier.weight(2f), color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextSecondary, fontSize = 13.sp)
            Text("Path",   modifier = Modifier.weight(3f), color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextSecondary, fontSize = 13.sp)
            Text("Size",   modifier = Modifier.weight(1f), color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextSecondary, fontSize = 13.sp,
                textAlign = TextAlign.End)
        }

        HorizontalDivider(color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.Divider)

        LazyColumn {
            items(projects) { project ->
                ProjectRow(project = project, onClick = { onOpen(project) })
                HorizontalDivider(color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.Divider.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun ProjectRow(project: RecentProject, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(project.name,       modifier = Modifier.weight(2f), fontSize = 13.sp)
        Text(project.lastOpened, modifier = Modifier.weight(2f), fontSize = 13.sp, color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextSecondary)
        Text(project.path,       modifier = Modifier.weight(3f), fontSize = 13.sp, color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextSecondary,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("${project.sizeMB} MB", modifier = Modifier.weight(1f), fontSize = 13.sp,
            textAlign = TextAlign.End, color = _root_ide_package_.org.example.project.ui.them.TrajectoryColors.TextSecondary)
    }
}