package com.heavystudio.helpabroad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.heavystudio.helpabroad.ui.navigation.AppNavigation
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HelpAbroadTheme {
                AppNavigation()
            }
        }
    }
}