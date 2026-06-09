
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.project.App
import java.awt.Toolkit



fun main() = application {
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized,
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Trajectory",
        state = windowState,
        undecorated = false
    ) {
        App()
    }
}