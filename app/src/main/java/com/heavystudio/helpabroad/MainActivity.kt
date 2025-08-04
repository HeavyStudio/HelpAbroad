package com.heavystudio.helpabroad

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heavystudio.helpabroad.ui.screen.OnboardingScreen
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme
import com.heavystudio.helpabroad.ui.viewmodel.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

object AppDestinations {
    const val ONBOARDING_ROUTE = "onboarding"
    const val MAIN_APP_ROUTE = "main_app"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelpAbroadTheme {
                HelpAbroadAppNavigation()
            }
        }
    }
}

@Composable
fun HelpAbroadAppNavigation() {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val isOnboardingComplete by onboardingViewModel.isOnboardingComplete.collectAsState()

    var isInitialStateReady by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isOnboardingComplete) {
        if (!isInitialStateReady) {
            isInitialStateReady = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isInitialStateReady) {
            NavHost(
                navController = navController,
                startDestination = if (isOnboardingComplete) {
                    AppDestinations.MAIN_APP_ROUTE
                } else {
                    AppDestinations.ONBOARDING_ROUTE
                }
            ) {
                composable(AppDestinations.ONBOARDING_ROUTE) {
                    OnboardingScreen(
                        onNavigateNext = {
                            navController.navigate(AppDestinations.MAIN_APP_ROUTE) {
                                popUpTo(AppDestinations.ONBOARDING_ROUTE) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(AppDestinations.MAIN_APP_ROUTE) {
                    val mainScreenOnboardingViewModel: OnboardingViewModel = hiltViewModel()
                    val context = LocalContext.current

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Welcome to the Main App Screen")
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = {
                                mainScreenOnboardingViewModel.resetOnboardingStatus()
                                Toast.makeText(
                                    context,
                                    "Onboarding reset. Please restart the app.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }) {
                                Text("DEV - Reset Onboarding")
                            }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

}