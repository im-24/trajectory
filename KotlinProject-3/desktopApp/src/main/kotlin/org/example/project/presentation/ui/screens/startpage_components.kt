package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.presentation.ui.screens.RecentProject
import org.example.project.presentation.ui.them.TrajectoryColors

@Composable
fun StartPageTopBar(searchQuery: String, onSearchChange: (String) -> Unit, appInfo: () -> Unit) {
    val textFieldState = rememberTextFieldState(initialText = searchQuery)

    LaunchedEffect(textFieldState.text) {
        onSearchChange(textFieldState.text.toString())
    }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            state = textFieldState,
            placeholder = {
                Text(
                    "Find Project ...",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )
            },
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            },
            modifier = Modifier.height(38.dp),
            shape = CircleShape,
            lineLimits = TextFieldLineLimits.SingleLine,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = TrajectoryColors.Background.copy(0.2f),
                focusedTextColor = TrajectoryColors.Background,
                unfocusedLeadingIconColor = TrajectoryColors.Background.copy(0.2f),
                focusedLeadingIconColor = TrajectoryColors.Background,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = TrajectoryColors.Background,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.25f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.5f),
            )
        )

        Spacer(Modifier.width(12.dp))

        IconButton(onClick = { appInfo() }) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Default.Info,
                contentDescription = "Information Icon",
                tint = TrajectoryColors.Background.copy(0.25f),
            )
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(TrajectoryColors.TextMuted, shape = CircleShape)
        )
    }
}

@Composable
fun RecentProjectsTable(
    projects: List<RecentProject>,
    onOpen: (RecentProject) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrajectoryColors.PurpleDark.copy(0.2f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 4.dp)) {
            Text("Name", modifier = Modifier.weight(2f), color = TrajectoryColors.Background, fontSize = 13.sp)
            Text("Recent", modifier = Modifier.weight(2f), color = TrajectoryColors.Background, fontSize = 13.sp)
            Text("Path", modifier = Modifier.weight(3f), color = TrajectoryColors.Background, fontSize = 13.sp)
            Text(
                "Size",
                modifier = Modifier.weight(1f),
                color = TrajectoryColors.Background,
                fontSize = 13.sp,
                textAlign = TextAlign.End
            )
        }

        HorizontalDivider(color = TrajectoryColors.Divider.copy(0.2f))

        if (projects.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "No Projects",
                    color = TrajectoryColors.Background.copy(0.2f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            LazyColumn {
                items(projects) { project ->
                    ProjectRow(project = project, onClick = { onOpen(project) })
                    HorizontalDivider(color = TrajectoryColors.Divider.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun ProjectRow(project: RecentProject, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(project.name,            color = TrajectoryColors.Background,
            modifier = Modifier.weight(2f), fontSize = 13.sp)
        Text(project.lastOpened, modifier = Modifier.weight(2f), fontSize = 13.sp, color = TrajectoryColors.Background)
        Text(
            project.path,
            modifier = Modifier.weight(3f),
            fontSize = 13.sp,
            color = TrajectoryColors.Background,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            "${project.sizeMB} MB",
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            color = TrajectoryColors.Background
        )
    }
}