package org.example.project.ui.them
import org.example.project.ui.them.Appthemes

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue

import androidx.compose.runtime.setValue


class ThemeController {

    var currentTheme by mutableStateOf(Appthemes.Light)

    fun toggleTheme() {

        currentTheme =
            if (currentTheme == Appthemes.Dark)
                Appthemes.Light
            else
                Appthemes.Dark
    }
}