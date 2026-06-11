package org.example.project.ui.them

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale


@Composable
fun MeshGradientBackground(content: @Composable () -> Unit) {
   val backgroundImg = "backgrounimg.jpg"

            Image(
                painter = painterResource(backgroundImg), // Placed in src/desktopMain/resources/
                contentDescription = null, // Hidden from accessibility screen readers
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Crops and fills the window boundaries
            )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Black.copy(0.75f) , TrajectoryColors.PurpleLight.copy(0.15f) )
                    )
                )
        )

        content()
    }
