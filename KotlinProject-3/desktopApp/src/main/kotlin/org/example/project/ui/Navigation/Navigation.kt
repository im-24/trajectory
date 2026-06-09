package org.example.project.ui.Navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class Screens{
        Start ,
        ProjectileCaracteristics,
        Simulation2D,
        Simulation3D,
        MathExpretion,
        Tabs,
        ViewData,
        Export,
        Home,
        Import,
        Settings
    }

class NavigationController {
    var currentScreen by mutableStateOf(Screens.Start)
    fun NavigateToScreen(to : Screens){
        currentScreen = to
    }
}