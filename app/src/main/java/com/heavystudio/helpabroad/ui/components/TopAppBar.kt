package com.heavystudio.helpabroad.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    appName: String,
    gradientColors: List<Color>
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = appName.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = Brush.linearGradient(
                        colors = gradientColors
                    )
                )
            )
        },
    )
}