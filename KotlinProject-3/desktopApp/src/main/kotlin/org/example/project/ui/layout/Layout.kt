package org.example.project.ui.layout

import TopBarMenu
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import org.example.project.ui.Navigation.NavigationController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.WindowState
import org.example.project.ui.Navigation.Screens
import org.example.project.ui.them.AppColors
import kotlin.system.exitProcess

@Composable
fun RootLayout(
    Colors : AppColors,
    onClose:  () -> Unit,
    onMinimize: () -> Unit ,
    nav : NavigationController,
    windowState: WindowState
) {
    Column (
        modifier = Modifier.fillMaxSize(),

    ){
            TopBarMenu(
                colors = Colors,
                onClose = onClose,
                onMinimize = onMinimize,
            )
        Row (
            modifier = Modifier.fillMaxWidth()
            .background(Color.Red)
            ){
            Button(
                onClick = onMinimize,
            ){
                Text("Minimize")
            }

        }


    }
}