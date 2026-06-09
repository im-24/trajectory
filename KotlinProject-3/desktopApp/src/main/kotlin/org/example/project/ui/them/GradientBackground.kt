import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun MeshGradientBackground(content: @Composable () -> Unit) {
    val gradient = Brush.sweepGradient(
        colors = listOf(
            Color(0xFFD4C8F0),  // soft lavender
            Color(0xFFC8E6D4),  // soft sage green
            Color(0xFFF5C8D8),  // soft pink
            Color(0xFFF0E6C8),  // soft cream
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