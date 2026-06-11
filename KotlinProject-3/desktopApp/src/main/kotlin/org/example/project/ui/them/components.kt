import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.them.TrajectoryColors
import org.example.project.ui.them.TrajectoryTheme


@Composable
fun mainButton(
    onClick: () -> Unit,
    modifier : Modifier = Modifier,
    enabled: Boolean = true,
    // By allowing RowScope, you can pass text, icons, or both
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,

        modifier = modifier,
        enabled = enabled,
        // Global shape design choice (e.g., modern rounded corners)
        shape = RoundedCornerShape(8.dp),
        // Global color scheme enforcement
        colors = ButtonDefaults.buttonColors(
            backgroundColor = TrajectoryColors.Purple,
            contentColor = TrajectoryColors.Background,
            disabledBackgroundColor = TrajectoryColors.TextMuted.copy(alpha = 0.5f),
            disabledContentColor = TrajectoryColors.Background.copy(alpha = 0.5f)
        ),
        // Global uniform padding settings
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        content = content
    )
}