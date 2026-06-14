package org.example.project.ui.them

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

// ════════════════════════════════════════════════════════════════════════════
//  FONTS
//  TrajectoryTyp.premierfont  — JetBrainsMono  (technical values, code, monospace readouts)
//  secondaryfont — Google Sans Flex  (UI text, labels, body copy)
// ════════════════════════════════════════════════════════════════════════════
object TrajectoryTyp{
val welledge = FontFamily(
    Font("fontFamily/IBMPlexMono-Bold.ttf", FontWeight.Bold),
Font("fontFamily/IBMPlexMono-Regular.ttf", FontWeight.Normal),)

val premierfont = FontFamily(
    Font("fontFamily/JetBrainsMono-Regular.ttf",  FontWeight.Normal),
    Font("fontFamily/JetBrainsMono-SemiBold.ttf",   FontWeight.Medium),
    Font("fontFamily/JetBrainsMono-ExtraBold.ttf",     FontWeight.Bold)
)

val secondaryfont = FontFamily(
    Font("fontFamily/GoogleSansFlex_9pt-Thin.ttf",   FontWeight.Light),
    Font("fontFamily/GoogleSansFlex_24pt-Regular.ttf", FontWeight.Normal),
    Font("fontFamily/GoogleSansFlex_24pt-Medium.ttf",  FontWeight.Medium),
    Font("fontFamily/GoogleSansFlex_72pt-Black.ttf",   FontWeight.Bold)
)
}
// ════════════════════════════════════════════════════════════════════════════
//  COLOR PALETTE
// ════════════════════════════════════════════════════════════════════════════

object TrajectoryColors {
    // Brand
    val Purple       = Color(0xFF4E3761)
    val PurpleDark   = Color(0xFF190C23)
    val PurpleLight  = Color(0xFF9D49FF)
    val LimeGreen    = Color(0xFF82E716)
    val LimeGreenDk  = Color(0xFF458701)

    // Light-mode surfaces & text
    val Background   = Color(0xFFF3F0F8)
    val Surface      = Color(0xFFF3F3F3)
    val TextPrimary  = Color(0xFF1A1A2E)
    val TextSecondary= Color(0xFF6B7280)
    val TextMuted    = Color(0xFF9CA3AF)
    val Divider      = Color(0xFF8A53DE)
    val TaglineColor = Color(0xFF641CC4)

    // Semantic
    val Success      = Color(0xFF16A34A)
    val Warning      = Color(0xFFF59E0B)
    val Error        = Color(0xFFDC2626)
    val Info         = Color(0xFF0EA5E9)
}

// ════════════════════════════════════════════════════════════════════════════
//  COLOR SCHEMES
// ════════════════════════════════════════════════════════════════════════════

private val LightColorScheme = lightColorScheme(
    // Primary — purple brand
    primary              = TrajectoryColors.Purple,
    onPrimary            = Color(0xFFF2E8FC),
    primaryContainer     = Color(0xFFEDD9FF),
    onPrimaryContainer   = Color(0xFF2D0060),

    // Secondary — lime accent
    secondary            = TrajectoryColors.LimeGreen,
    onSecondary          = Color(0xFF1A2E00),
    secondaryContainer   = Color(0xFFD6F7A3),
    onSecondaryContainer = Color(0xFF1A2E00),

    // Tertiary — teal highlight
    tertiary             = Color(0xFF0EA5E9),
    onTertiary           = Color.White,
    tertiaryContainer    = Color(0xFFCCEEFF),
    onTertiaryContainer  = Color(0xFF00354F),

    // Surfaces
    background           = TrajectoryColors.Background,
    onBackground         = TrajectoryColors.TextPrimary,
    surface              = TrajectoryColors.Surface,
    onSurface            = TrajectoryColors.TextPrimary,
    surfaceVariant       = Color(0xFFEDE7F6),
    onSurfaceVariant     = TrajectoryColors.TextSecondary,
    surfaceTint          = TrajectoryColors.Purple,

    // Error
    error                = TrajectoryColors.Error,
    onError              = Color.White,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),

    // Outline / divider
    outline              = TrajectoryColors.Divider,
    outlineVariant       = Color(0xFFF3F0F8),
    scrim                = Color(0xFF000000),
    inverseSurface       = Color(0xFF1A1A2E),
    inverseOnSurface     = Color(0xFFF3F0F8),
    inversePrimary       = TrajectoryColors.PurpleLight,
)

private val DarkColorScheme = darkColorScheme(
    // Primary
    primary              = Color(0xFF8481B1),
    onPrimary            = Color(0xFF7925CD),
    primaryContainer     = Color(0xFFC166E8),
    onPrimaryContainer   = Color(0xFFEFB0F3),

    // Secondary
    secondary            = Color(0xFFA8E65A),
    onSecondary          = Color(0xFF1A2E00),
    secondaryContainer   = Color(0xFF2D5100),
    onSecondaryContainer = Color(0xFFCBF5A0),

    // Tertiary
    tertiary             = Color(0xFF7DD4FC),
    onTertiary           = Color(0xFF003548),
    tertiaryContainer    = Color(0xFF004E6A),
    onTertiaryContainer  = Color(0xFFC4E9FF),

    // Surfaces
    background           = Color(0xFF0F0F18),
    onBackground         = Color(0xFF1D1D1F),
    surface              = Color(0xFF1A1A26),
    onSurface            = Color(0xFF4A4A50),
    surfaceVariant       = Color(0xFF252535),
    onSurfaceVariant     = Color(0xFFB0B0C8),
    surfaceTint          = Color(0xFFCFA0F5),

    // Error
    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),

    // Outline
    outline              = Color(0xFF3A3A50),
    outlineVariant       = Color(0xFF2A2A3C),
    scrim                = Color(0xFF000000),
    inverseSurface       = Color(0xFFE8E8F0),
    inverseOnSurface     = Color(0xFF1A1A26),
    inversePrimary       = TrajectoryColors.Purple,
)

// ════════════════════════════════════════════════════════════════════════════
//  TYPOGRAPHY
//
//  Role         │ Font             │ Use in app
//  ─────────────┼──────────────────┼──────────────────────────────────────────
//  displayLarge │ JetBrainsMono    │ Hero numbers (speed, distance readouts)
//  displayMedium│ JetBrainsMono    │ Large stat cards
//  displaySmall │ JetBrainsMono    │ Section big numbers
//  headlineLarge│ Google Sans Bold │ Screen titles ("2D Simulation")
//  headlineMed  │ Google Sans Bold │ Card section headings
//  headlineSmall│ Google Sans Med  │ Sub-section headings
//  titleLarge   │ Google Sans Bold │ Dialog / panel titles
//  titleMedium  │ Google Sans Med  │ Card titles, tab labels
//  titleSmall   │ Google Sans Med  │ Sidebar section labels
//  bodyLarge    │ Google Sans Norm │ Main descriptive body text
//  bodyMedium   │ Google Sans Norm │ Standard UI body text (default)
//  bodySmall    │ Google Sans Light│ Secondary descriptions, hints
//  labelLarge   │ JetBrainsMono    │ Button labels, chip text
//  labelMedium  │ JetBrainsMono    │ Field units, tags, badges
//  labelSmall   │ Google Sans Norm │ Captions, timestamps, footnotes
// ════════════════════════════════════════════════════════════════════════════

/**
 * Build a full [Typography] scaled proportionally from [baseFontSize] (default 14sp).
 * All sizes are derived from the baseline so settings → font-size works app-wide.
 */
fun dynamicTypography(baseFontSize: Int = 14): Typography {
    val b = baseFontSize.toFloat()   // baseline body size

    return Typography(

        // ── Display — JetBrainsMono, for large numerical readouts ─────────
        displayLarge = TextStyle(
            fontFamily    = TrajectoryTyp.premierfont,
            fontWeight    = FontWeight.Bold,
            fontSize      = (b * 6.57f).sp,   // ~64sp @ 14
            lineHeight    = (b * 5.14f).sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = TextStyle(
            fontFamily    = TrajectoryTyp.premierfont,
            fontWeight    = FontWeight.Medium,
            fontSize      = (24).sp,   // ~46sp @ 14
            lineHeight    = (b * 3.86f).sp,
            letterSpacing = (-0.25).sp
        ),
        displaySmall = TextStyle(
            fontFamily    = TrajectoryTyp.premierfont,
            fontWeight    = FontWeight.Normal,
            fontSize      = (b * 2.57f).sp,   // ~36sp @ 14
            lineHeight    = (b * 3.14f).sp,
            letterSpacing = 0.sp
        ),

        // ── Headline — Google Sans Bold, for screen & card headings ───────
        headlineLarge = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Bold,
            fontSize      = (b * 2.29f).sp,   // ~32sp @ 14
            lineHeight    = (b * 2.86f).sp,
            letterSpacing = (-0.25).sp
        ),
        headlineMedium = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Bold,
            fontSize      = (b * 2.0f).sp,    // ~28sp @ 14
            lineHeight    = (b * 2.57f).sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Medium,
            fontSize      = (b * 1.71f).sp,   // ~24sp @ 14
            lineHeight    = (b * 2.29f).sp,
            letterSpacing = 0.sp
        ),

        // ── Title — Google Sans, for panel/dialog titles and tabs ─────────
        titleLarge = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Bold,
            fontSize      = (b * 1.57f).sp,   // ~22sp @ 14
            lineHeight    = (b * 2.0f).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Medium,
            fontSize      = (b * 1.14f).sp,   // ~16sp @ 14
            lineHeight    = (b * 1.71f).sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Medium,
            fontSize      = (b * 1.0f).sp,    // ~14sp @ 14  (sidebar section labels)
            lineHeight    = (b * 1.43f).sp,
            letterSpacing = 0.1.sp
        ),

        // ── Body — Google Sans, main readable content ─────────────────────
        bodyLarge = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Normal,
            fontSize      = (b * 1.14f).sp,   // ~16sp @ 14
            lineHeight    = (b * 1.71f).sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Normal,
            fontSize      = b.sp,             // exactly baseFontSize
            lineHeight    = (b * 1.43f).sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Light,
            fontSize      = (b * 0.86f).sp,   // ~12sp @ 14
            lineHeight    = (b * 1.29f).sp,
            letterSpacing = 0.4.sp
        ),


        // ── Label — JetBrainsMono for interactive elements & captions ─────
        labelLarge = TextStyle(
            fontFamily    = TrajectoryTyp.welledge,
            fontWeight    = FontWeight.Bold,
            fontSize      = (b * 1.0f).sp,    // ~14sp @ 14  (buttons, chips)
            lineHeight    = (b * 1.43f).sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Light,
            fontSize      = (b * 0.86f).sp,   // ~12sp @ 14  (units, tags)
            lineHeight    = (b * 1.14f).sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily    = TrajectoryTyp.secondaryfont,
            fontWeight    = FontWeight.Light,
            fontSize      = (b * 0.71f).sp,   // ~10sp @ 14  (captions, timestamps)
            lineHeight    = (b * 1.14f).sp,
            letterSpacing = 0.5.sp
        ),
    )
}

// ════════════════════════════════════════════════════════════════════════════
//  APP THEME
//  Driven by AppSettings (dark mode + font size) injected from HomeScreen.
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun TrajectoryTheme(
    darkMode: Boolean = false,
    fontSize: Int     = 14,
    content:  @Composable () -> Unit
) {
    val colorScheme = if (darkMode) DarkColorScheme else LightColorScheme
    val typography  = remember(fontSize) { dynamicTypography(fontSize) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = typography,
        content     = content
    )
}