import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import com.sun.rowset.internal.Row
import java.lang.module.ModuleDescriptor
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun GlobalWindow(
    onClose : () -> Unit,
    onMinimize : () -> Unit,
){
    draggableWindow()
}

@Composable
fun draggableWindow(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Red),
    ) {

    }
}