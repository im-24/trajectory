
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.project.App
import java.awt.Toolkit



fun main() = application {

    val screenSize = Toolkit.getDefaultToolkit().screenSize

    val screenWidth = screenSize.width
    val screenHeight = screenSize.height


    val stateApp = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.TopStart),
        placement =
            WindowPlacement.Maximized ,
    )


    Window(
        onCloseRequest = ::exitApplication,
        title = "Trajectory",
        state = stateApp,
        resizable = true,
        alwaysOnTop = false,
        undecorated = false,
        transparent = false,
        ) {

        App(stateApp)
    }
}