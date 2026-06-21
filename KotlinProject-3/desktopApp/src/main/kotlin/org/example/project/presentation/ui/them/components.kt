import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.bouncycastle.oer.OERDefinition.placeholder
import org.example.project.presentation.ui.them.TrajectoryColors

@Composable
fun mainButtonLarge (
    onClick: () -> Unit = {

    },
    modifier : Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit = {}
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.width(180.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
           TrajectoryColors.LimeGreen
        ),
        shape = CircleShape
    ) {
        content()
    }
}


@Composable
fun SecondaryButtonLarge (
    onClick: () -> Unit = {

    },
    modifier : Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit = {}

){
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.width(180.dp)
            .height(48.dp), colors = ButtonDefaults.buttonColors(
           MaterialTheme.colorScheme.background.copy(0f),
        ),
        border = BorderStroke(1.dp, TrajectoryColors.Background),

        shape = CircleShape
    )
    {
        content ()
    }
}


@Composable
fun Dialogwind(
    transpearence: Float,
    width : Dp = 12.dp,
    height: Dp=12.dp,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit
){
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Foreground message card — less blurred, sits above the scrim

        Surface(

            modifier = modifier
                .width(width)
                .height(height),
            shape = MaterialTheme.shapes.medium,
            color = TrajectoryColors.PurpleDark.copy(alpha = transpearence),
            tonalElevation = 12.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TrajectoryColors.PurpleLight.copy(0.25f ), MaterialTheme.shapes.medium)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                TrajectoryColors.LimeGreen.copy(alpha = 0.15f),
                                Color(0x34355325),
                                TrajectoryColors.PurpleDark.copy(alpha = 0.35f)
                            )
                        )
                    ))
            {
            }

            content()
            }
        }}
