import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Color Palette ──────────────────────────────────────────────
object TrajectoryColors {
    val Purple       = Color(0xFF7B5EA7)
    val PurpleDark   = Color(0xFF5C3D8F)
    val LimeGreen    = Color(0xFF8DDB3A)
    val LimeGreenDk  = Color(0xFF6BB82A)
    val Background   = Color(0xFFF3F0F8)   // very light lavender-white
    val Surface      = Color(0xFFFFFFFF)
    val TextPrimary  = Color(0xFF1A1A2E)
    val TextSecondary= Color(0xFF6B7280)
    val TextMuted    = Color(0xFF9CA3AF)
    val Divider      = Color(0xFFE5E7EB)
    val TaglineColor = Color(0xFF7B5EA7)   // purple for "Model. Simulate. Visualize"
}

// ── Typography ────────────────────────────────────────────────
// "Trajectory" uses a monospace/slab font — JetBrains Mono or Courier
val TrajectoryTypography = Typography(
    displayLarge = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.ExtraBold,
        fontSize    = 52.sp,
        letterSpacing = (-1).sp
    ),
    titleMedium = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp,
        letterSpacing = 1.sp
    ),
    bodyMedium = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Normal,
        fontSize    = 12.sp,
        color       = TrajectoryColors.TextMuted
    )
)

// ── Material3 Color Scheme ────────────────────────────────────
private val TrajectoryColorScheme = lightColorScheme(
    primary         = TrajectoryColors.Purple,
    onPrimary       = Color.White,
    secondary       = TrajectoryColors.LimeGreen,
    onSecondary     = Color.White,
    background      = TrajectoryColors.Background,
    surface         = TrajectoryColors.Surface,
    onBackground    = TrajectoryColors.TextPrimary,
    onSurface       = TrajectoryColors.TextPrimary,
    outline         = TrajectoryColors.Divider,
)

// ── App Theme ─────────────────────────────────────────────────
@Composable
fun TrajectoryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TrajectoryColorScheme,
        typography  = TrajectoryTypography,
        content     = content
    )
}