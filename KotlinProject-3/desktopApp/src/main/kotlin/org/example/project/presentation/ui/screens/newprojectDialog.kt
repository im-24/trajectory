package ui.screens

import Dialogwind
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.example.project.presentation.ui.them.TrajectoryColors
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

data class NewProjectConfig(
    val name: String,
    val location: String,
    val importFilePath: String,
    val autoSave: Boolean,
    val autoSaveIntervalMinutes: Int
)

@OptIn(ExperimentalMaterial3Api::class)  // Add this annotation at the top of the composable
@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (NewProjectConfig) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var projectLocation by remember { mutableStateOf("") }
    var importFilePath by remember { mutableStateOf("") }
    var autoSave by remember { mutableStateOf(true) }
    var autoSaveInterval by remember { mutableStateOf(5) }
    var nameError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }

    var isAutoSaveDropdownExpanded by remember { mutableStateOf(false) }

    val autoSaveOptions = listOf(2, 5, 10, 15, 30, 60)

    fun pickFolder() {
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = "Select project location"
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            projectLocation = chooser.selectedFile.absolutePath
            locationError = false
        }
    }

    fun pickExcelFile() {
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            dialogTitle = "Select Excel file to import"
            fileFilter = FileNameExtensionFilter("Excel files", "xlsx", "xls", "csv")
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            importFilePath = chooser.selectedFile.absolutePath
        }
    }

    fun validate(): Boolean {
        nameError = projectName.isBlank()
        locationError = projectLocation.isBlank()
        return !nameError && !locationError
    }

    Dialogwind(
        transpearence = 0.75f,
        750.dp,
        620.dp,
        modifier = Modifier,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Title ─────────────────────────────────────
            Text(
                text = "New Project",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = TrajectoryColors.TextPrimary
            )
            Text(
                text = "Configure your project settings",
                fontSize = 14.sp,
                color = TrajectoryColors.TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // ── Project Name ──────────────────────────────
            DialogLabel("Project name")
            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it; nameError = false },
                placeholder = { Text("e.g. Ballistic Test Alpha", color = TrajectoryColors.TextMuted) },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("Name is required", color = MaterialTheme.colorScheme.error) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = dialogFieldColors()
            )

            Spacer(Modifier.height(16.dp))

            // ── Project Location ──────────────────────────
            DialogLabel("Project location")
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = projectLocation,
                    onValueChange = { projectLocation = it; locationError = false },
                    placeholder = { Text("Select a folder...", color = TrajectoryColors.TextMuted) },
                    isError = locationError,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    readOnly = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = dialogFieldColors()
                )
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = ::pickFolder,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, TrajectoryColors.Purple),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = TrajectoryColors.Purple
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Browse", color = TrajectoryColors.Purple)
                }
            }
            if (locationError) {
                Text(
                    "Location is required",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Import File ───────────────────────────────
            DialogLabel("Import data file (optional)")
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = if (importFilePath.isEmpty()) "" else File(importFilePath).name,
                    onValueChange = {},
                    placeholder = { Text("No file selected", color = TrajectoryColors.TextMuted) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    readOnly = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = dialogFieldColors()
                )
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = ::pickExcelFile,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, TrajectoryColors.LimeGreen),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        Icons.Default.TableChart,
                        contentDescription = null,
                        tint = TrajectoryColors.LimeGreen
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Import", color = TrajectoryColors.LimeGreen)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Auto Save ─────────────────────────────────
            HorizontalDivider(color = TrajectoryColors.Divider)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Auto save",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = TrajectoryColors.TextPrimary
                    )
                    Text(
                        "Automatically save the project",
                        fontSize = 12.sp,
                        color = TrajectoryColors.TextMuted
                    )
                }
                Switch(
                    checked = autoSave,
                    onCheckedChange = { autoSave = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TrajectoryColors.Purple
                    )
                )
            }

            // Auto-save interval dropdown (only shown when autoSave is on)
            AnimatedVisibility(visible = autoSave) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    DialogLabel("Save interval")

                    // Dropdown using ExposedDropdownMenuBox with experimental annotation
                    ExposedDropdownMenuBox(
                        expanded = isAutoSaveDropdownExpanded,
                        onExpandedChange = { isAutoSaveDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "$autoSaveInterval min",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAutoSaveDropdownExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(

                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isAutoSaveDropdownExpanded,
                            onDismissRequest = { isAutoSaveDropdownExpanded = false }
                        ) {
                            autoSaveOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "$option min",
                                            fontSize = 14.sp,
                                            color = if (option == autoSaveInterval)
                                                TrajectoryColors.Purple
                                            else TrajectoryColors.TextPrimary
                                        )
                                    },
                                    onClick = {
                                        autoSaveInterval = option
                                        isAutoSaveDropdownExpanded = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (option == autoSaveInterval)
                                                TrajectoryColors.Purple.copy(alpha = 0.1f)
                                            else Color.Transparent
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Actions ───────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        "Cancel",
                        color = TrajectoryColors.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick = {
                        if (validate()) {
                            onCreate(
                                NewProjectConfig(
                                    name = projectName.trim(),
                                    location = projectLocation,
                                    importFilePath = importFilePath,
                                    autoSave = autoSave,
                                    autoSaveIntervalMinutes = autoSaveInterval
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrajectoryColors.Purple
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 140.dp)
                ) {
                    Icon(
                        Icons.Default.CreateNewFolder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Create Project",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────

@Composable
private fun DialogLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = TrajectoryColors.TextSecondary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = TrajectoryColors.Divider,
    focusedBorderColor = TrajectoryColors.Purple,
    unfocusedContainerColor = Color(0xFFFAFAFA),
    focusedContainerColor = Color.White
)