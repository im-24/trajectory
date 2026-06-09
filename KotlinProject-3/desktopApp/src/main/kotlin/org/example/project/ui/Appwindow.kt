import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sun.rowset.internal.Row
import java.lang.module.ModuleDescriptor
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.ui.them.AppColors
import org.example.project.ui.them.ThemeController
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment


@Composable
fun TopBarMenu(
    colors: AppColors,
    onClose : () -> Unit,
    onMinimize : () -> Unit,
){
    Row (
        modifier = Modifier.fillMaxWidth()
            .background(colors.background)
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Text("Trajectory")
        Row (
            modifier = Modifier.wrapContentWidth(),

        ){
            Button(
                onClick = {
                    onMinimize()

                },
                modifier = Modifier.width(56.dp)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colors.background
                )
                ,shape = RoundedCornerShape(0.dp)


            )
            {
                Text("_")
            }
            Button(
                onClick = {
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colors.background,
                ),
                modifier = Modifier.width(56.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(0.dp)

            ){

            }
        }
    }
}