// AppSettingsProvider.kt
package org.example.project

import androidx.compose.runtime.staticCompositionLocalOf

// ── Global read-only access to current settings ───────────────────────────
val LocalAppSettings = staticCompositionLocalOf { AppSettings() }

// ── Global mutable setter (write settings from any child screen) ──────────
val LocalOnSettingsChange = staticCompositionLocalOf<(AppSettings) -> Unit> { {} }