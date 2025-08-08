package com.heavystudio.helpabroad.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaTopAppBar(
    appName: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    actionContent: (@Composable () -> Unit)? = null
) {
    TopAppBar(
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
        actions = {
            if (actionContent != null) {
                actionContent()
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HaTopAppBarWithFlagPreview() {
    MaterialTheme {
        HaTopAppBar(
            appName = "Help Abroad",
            gradientColors = listOf(Color.Blue, Color.White, Color.Red),
            actionContent = {
                Text(
                    text = "\uD83C\uDDEB\uD83C\uDDF7",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )
    }
}