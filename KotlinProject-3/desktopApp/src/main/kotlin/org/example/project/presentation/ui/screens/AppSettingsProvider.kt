package org.example.project

import androidx.compose.runtime.compositionLocalOf
import org.example.project.domain.repositories.AppSettings

// ── Global read-only access to current settings ───────────────────────────
val LocalAppSettings = compositionLocalOf { AppSettings() }

// ── Global mutable setter (write settings from any child screen) ──────────
val LocalOnSettingsChange = compositionLocalOf<(AppSettings) -> Unit> { {} }