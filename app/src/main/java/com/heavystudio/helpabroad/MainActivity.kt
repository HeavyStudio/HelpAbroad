package com.heavystudio.helpabroad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heavystudio.helpabroad.ui.home.HomeScreen
import com.heavystudio.helpabroad.ui.navigation.AppNavigation
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color(0x40FFFFFF).toArgb(),
                darkScrim = Color(0x66000000).toArgb()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color(0x40FFFFFF).toArgb(),
                darkScrim = Color(0x66000000).toArgb()
            )
        )

        setContent {
            HelpAbroadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}