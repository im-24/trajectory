// ui/screens/SettingsDialog.kt
package ui.screens

import org.example.project.LocalAppSettings
import org.example.project.LocalOnSettingsChange
import org.example.project.ui.them.TrajectoryColors
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.project.AppSettings
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

// ── Dialog entry point ────────────────────────────────────────────────────────
@Composable
fun SettingsDialog(
    settings: AppSettings,
    onChange:  (AppSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val onSettingsChange = LocalOnSettingsChange.current

    // Local working copy — committed on Save, reverted on Cancel
    var draft by remember(settings) { mutableStateOf(settings) }

    // Push visual-only changes (theme/font) live so the user sees them instantly
    fun updateDraft(new: AppSettings) {
        draft = new
        // Only propagate appearance changes immediately for live preview;
        // project/export fields are committed only on Save.
        onSettingsChange(new.copy(
            projectName            = settings.projectName,
            backupIntervalMinutes  = settings.backupIntervalMinutes,
            reportAuthorName       = settings.reportAuthorName,
            reportCompany          = settings.reportCompany,
            reportLogoPath         = settings.reportLogoPath,
            reportDepartment       = settings.reportDepartment,
            reportContact          = settings.reportContact,
            reportFootnote         = settings.reportFootnote
        ))
    }

    // Revert appearance preview if user cancels
    fun cancel() {
        onSettingsChange(settings)   // restore original
        onDismiss()
    }

    var selectedTab by remember { mutableStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .width(640.dp)
                .heightIn(min = 480.dp, max = 680.dp),
            shape  = RoundedCornerShape(16.dp),
            color  = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Dialog header ─────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TrajectoryColors.Purple)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, null,
                            tint = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, null,
                            tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                    }
                }

                // ── Pill tab selector ─────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsTabPill("Project",  Icons.Default.Folder,      0, selectedTab) { selectedTab = it }
                    SettingsTabPill("General",  Icons.Default.Tune,         1, selectedTab) { selectedTab = it }
                    SettingsTabPill("Export",   Icons.Default.Description,  2, selectedTab) { selectedTab = it }
                }

                HorizontalDivider(color = TrajectoryColors.Divider)

                // ── Tab content ───────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    when (selectedTab) {
                        0 -> ProjectSettingsTab(draft)    { updateDraft(it) }
                        1 -> GeneralSettingsTab(draft)    { updateDraft(it) }
                        2 -> ExportSettingsTab(draft)     { draft = it }   // export fields: no live preview needed
                    }
                }

                HorizontalDivider(color = TrajectoryColors.Divider)

                // ── Footer actions ────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = ::cancel) {
                        Text("Cancel", color = TrajectoryColors.TextSecondary)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onChange(draft); onDismiss() },                        colors  = ButtonDefaults.buttonColors(containerColor = TrajectoryColors.Purple),
                        shape   = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(40.dp).widthIn(min = 110.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save", fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// ── Tab: Project Settings ─────────────────────────────────────────────────────
@Composable
private fun ProjectSettingsTab(s: AppSettings, onChange: (AppSettings) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        SettingsGroup("Project Identity") {
            SettingsTextField(
                label       = "Project Name",
                value       = s.projectName,
                placeholder = "e.g. Ballistic Test Alpha",
                icon        = Icons.Default.Label,
                onValueChange = { onChange(s.copy(projectName = it)) }
            )
        }

        SettingsGroup("Auto-Backup") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Backup, null,
                            tint = TrajectoryColors.Purple.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Backup interval", fontSize = 13.sp, color = TrajectoryColors.TextSecondary)
                    }
                    Text(
                        "${s.backupIntervalMinutes} min",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TrajectoryColors.Purple,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Slider(
                    value         = s.backupIntervalMinutes.toFloat(),
                    onValueChange = { onChange(s.copy(backupIntervalMinutes = it.toInt())) },
                    valueRange    = 1f..60f,
                    steps         = 58,
                    colors        = SliderDefaults.colors(
                        thumbColor       = TrajectoryColors.Purple,
                        activeTrackColor = TrajectoryColors.Purple,
                        inactiveTrackColor = TrajectoryColors.Divider
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 min",  fontSize = 11.sp, color = TrajectoryColors.TextMuted)
                    Text("60 min", fontSize = 11.sp, color = TrajectoryColors.TextMuted)
                }
            }
        }
    }
}

// ── Tab: General Settings ─────────────────────────────────────────────────────
@Composable
private fun GeneralSettingsTab(s: AppSettings, onChange: (AppSettings) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        SettingsGroup("Appearance") {
            // Dark / Light mode toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (s.darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        null,
                        tint = TrajectoryColors.Purple.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Theme", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = TrajectoryColors.TextPrimary)
                        Text(if (s.darkMode) "Dark mode" else "Light mode",
                            fontSize = 11.sp, color = TrajectoryColors.TextMuted)
                    }
                }
                Switch(
                    checked = s.darkMode,
                    onCheckedChange = { onChange(s.copy(darkMode = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor  = Color.White,
                        checkedTrackColor  = TrajectoryColors.Purple
                    )
                )
            }

            Spacer(Modifier.height(4.dp))

            // Font size
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FormatSize, null,
                            tint = TrajectoryColors.Purple.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Font size", fontSize = 13.sp, color = TrajectoryColors.TextSecondary)
                    }
                    Text(
                        "${s.fontSize} sp",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TrajectoryColors.Purple,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Slider(
                    value         = s.fontSize.toFloat(),
                    onValueChange = { onChange(s.copy(fontSize = it.toInt())) },
                    valueRange    = 10f..24f,
                    steps         = 13,
                    colors        = SliderDefaults.colors(
                        thumbColor         = TrajectoryColors.Purple,
                        activeTrackColor   = TrajectoryColors.Purple,
                        inactiveTrackColor = TrajectoryColors.Divider
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Small (10)", fontSize = 11.sp, color = TrajectoryColors.TextMuted)
                    Text("Large (24)", fontSize = 11.sp, color = TrajectoryColors.TextMuted)
                }
            }
        }

        SettingsGroup("Language") {
            val languages = listOf("English", "French", "Arabic", "Spanish", "German")
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, null,
                        tint = TrajectoryColors.Purple.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Interface language", fontSize = 13.sp, color = TrajectoryColors.TextSecondary)
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    languages.forEach { lang ->
                        val selected = s.language == lang
                        FilterChip(
                            selected = selected,
                            onClick  = { onChange(s.copy(language = lang)) },
                            label    = { Text(lang, fontSize = 12.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TrajectoryColors.Purple,
                                selectedLabelColor     = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

// ── Tab: Export / Report Settings ────────────────────────────────────────────
@Composable
private fun ExportSettingsTab(s: AppSettings, onChange: (AppSettings) -> Unit) {

    fun pickLogo() {
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            dialogTitle = "Select logo image"
            fileFilter = FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "svg")
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            onChange(s.copy(reportLogoPath = chooser.selectedFile.absolutePath))
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        // Info banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TrajectoryColors.Purple.copy(alpha = 0.07f), RoundedCornerShape(10.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, null,
                tint = TrajectoryColors.Purple, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "These details are embedded in every generated PDF report.",
                fontSize = 12.sp,
                color = TrajectoryColors.TextSecondary,
                lineHeight = 17.sp
            )
        }

        SettingsGroup("Author & Organisation") {
            SettingsTextField(
                label = "Author / Analyst Name",
                value = s.reportAuthorName,
                placeholder = "e.g. Dr. Ali Benali",
                icon  = Icons.Default.Person,
                onValueChange = { onChange(s.copy(reportAuthorName = it)) }
            )
            Spacer(Modifier.height(12.dp))
            SettingsTextField(
                label = "Company / Institution",
                value = s.reportCompany,
                placeholder = "e.g. National Research Centre",
                icon  = Icons.Default.Business,
                onValueChange = { onChange(s.copy(reportCompany = it)) }
            )
            Spacer(Modifier.height(12.dp))
            SettingsTextField(
                label = "Department",
                value = s.reportDepartment,
                placeholder = "e.g. Ballistics & Applied Physics",
                icon  = Icons.Default.AccountTree,
                onValueChange = { onChange(s.copy(reportDepartment = it)) }
            )
            Spacer(Modifier.height(12.dp))
            SettingsTextField(
                label = "Contact / Email",
                value = s.reportContact,
                placeholder = "e.g. analyst@example.org",
                icon  = Icons.Default.Email,
                onValueChange = { onChange(s.copy(reportContact = it)) }
            )
        }

        SettingsGroup("Logo") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = if (s.reportLogoPath.isEmpty()) "" else s.reportLogoPath.substringAfterLast("/").substringAfterLast("\\"),
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("No logo selected", color = TrajectoryColors.TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Image, null,
                            tint = TrajectoryColors.Purple.copy(alpha = 0.6f))
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = TrajectoryColors.Divider,
                        focusedBorderColor   = TrajectoryColors.Purple
                    )
                )
                OutlinedButton(
                    onClick = ::pickLogo,
                    shape  = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, TrajectoryColors.Purple),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.Upload, null, tint = TrajectoryColors.Purple,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Browse", color = TrajectoryColors.Purple, fontSize = 13.sp)
                }
            }
            if (s.reportLogoPath.isNotEmpty()) {
                TextButton(
                    onClick = { onChange(s.copy(reportLogoPath = "")) },
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(Icons.Default.Clear, null,
                        tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Remove logo", color = Color(0xFFDC2626), fontSize = 12.sp)
                }
            }
        }

        SettingsGroup("Report Footer") {
            SettingsTextField(
                label = "Footer / Disclaimer text",
                value = s.reportFootnote,
                placeholder = "e.g. Confidential — for internal use only",
                icon  = Icons.Default.Notes,
                onValueChange = { onChange(s.copy(reportFootnote = it)) },
                singleLine = false,
                minLines   = 2
            )
        }
    }
}

// ── Shared primitives ─────────────────────────────────────────────────────────

@Composable
private fun SettingsTabPill(
    label:    String,
    icon:     ImageVector,
    index:    Int,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val active = index == selected
    val bg by animateColorAsState(
        if (active) TrajectoryColors.Purple else TrajectoryColors.Background,
        label = "pill_bg"
    )
    val fg by animateColorAsState(
        if (active) Color.White else TrajectoryColors.TextSecondary,
        label = "pill_fg"
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable { onSelect(index) }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = fg)
    }
}

@Composable
private fun SettingsGroup(
    title:   String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            title.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TrajectoryColors.TextMuted,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFB)),
            border = BorderStroke(1.dp, TrajectoryColors.Divider)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsTextField(
    label:        String,
    value:        String,
    placeholder:  String,
    icon:         ImageVector,
    onValueChange: (String) -> Unit,
    singleLine:   Boolean = true,
    minLines:     Int = 1
) {
    Column {
        Text(label, fontSize = 12.sp, color = TrajectoryColors.TextSecondary,
            modifier = Modifier.padding(bottom = 5.dp))
        OutlinedTextField(
            value          = value,
            onValueChange  = onValueChange,
            placeholder    = { Text(placeholder, color = TrajectoryColors.TextMuted, fontSize = 13.sp) },
            leadingIcon    = {
                Icon(icon, null, tint = TrajectoryColors.Purple.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp))
            },
            modifier  = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines  = minLines,
            shape     = RoundedCornerShape(10.dp),
            colors    = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor    = TrajectoryColors.Divider,
                focusedBorderColor      = TrajectoryColors.Purple,
                unfocusedContainerColor = Color.White,
                focusedContainerColor   = Color.White
            )
        )
    }
}