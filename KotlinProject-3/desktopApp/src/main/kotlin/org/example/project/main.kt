
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TrajectoryApp",
    ) {
        App()
    }
}