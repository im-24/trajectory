import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun MeshGradientBackground(content: @Composable () -> Unit) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFD4C8F0),  // soft lavender
             // soft cream
            Color(0xFFD4C8F0),  // back to lavender (close the sweep)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
    ) {
        content()
    }
}