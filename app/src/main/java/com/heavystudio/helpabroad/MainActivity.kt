package com.heavystudio.helpabroad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heavystudio.helpabroad.data.settings.AppTheme
import com.heavystudio.helpabroad.data.settings.SettingsRepository
import com.heavystudio.helpabroad.ui.navigation.AppNavigation
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

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
            val currentTheme by settingsRepository.themeFlow.collectAsStateWithLifecycle(
                initialValue = AppTheme.SYSTEM
            )

            val userDarkTheme = when (currentTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }
            HelpAbroadTheme(darkTheme = userDarkTheme) {
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