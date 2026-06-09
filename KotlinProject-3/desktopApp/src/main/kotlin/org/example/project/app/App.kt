package org.example.project


import TopBarMenu
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Colors
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import org.example.project.ui.Navigation.NavigationController
import org.example.project.ui.layout.RootLayout
import org.example.project.ui.them.ThemeController
import kotlin.system.exitProcess


@Composable
fun App( windowState: WindowState) {
    val themeController = remember {
        ThemeController()
    }

    val Colors = themeController.currentTheme

    val nav = remember {
        NavigationController()
    }



    Column (
        modifier = Modifier.fillMaxSize()
            .background(Colors.background),
    ){
        RootLayout(
            Colors = Colors,
            windowState = windowState,
            onMinimize ={windowState.isMinimized = true},
            onClose = {exitProcess(0)},
            nav = nav,
        )

    }



}