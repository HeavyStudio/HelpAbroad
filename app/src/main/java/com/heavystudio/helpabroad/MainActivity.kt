package com.heavystudio.helpabroad

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heavystudio.helpabroad.ui.screen.MainScreen
import com.heavystudio.helpabroad.ui.screen.OnboardingScreen
import com.heavystudio.helpabroad.ui.screen.WelcomeScreen
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme
import com.heavystudio.helpabroad.ui.viewmodel.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint


// TODO: Move to a util file
fun areallRequiredPermissionsGranted(context: Context): Boolean {
    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS
    )
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}
object AppDestinations {
    const val WELCOME_ROUTE = "welcome"
    const val PERMISSIONS_ROUTE = "permissions"
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
    val context = LocalContext.current
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()

    var startDestination by remember { mutableStateOf<String?>(null)}
    var isLoadingStates by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = onboardingViewModel.isOnboardingComplete) {
        val onboardingComplete = onboardingViewModel.isOnboardingComplete.value
        val allPermissionsGranted = areallRequiredPermissionsGranted(context)

        startDestination = if (!allPermissionsGranted) {
            AppDestinations.WELCOME_ROUTE
        } else {
            if (onboardingComplete) {
                AppDestinations.MAIN_APP_ROUTE
            } else {
                AppDestinations.ONBOARDING_ROUTE
            }
        }
        isLoadingStates = false
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoadingStates || startDestination == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = startDestination!!
            ) {
                composable(AppDestinations.WELCOME_ROUTE) {
                    WelcomeScreen(
                        onNavigateNext = {
                            navController.navigate(AppDestinations.PERMISSIONS_ROUTE) {
                                popUpTo(AppDestinations.WELCOME_ROUTE) { inclusive = true }
                            }
                        }
                    )
                }

                // TODO: Replace ONBOARDING_ROUTE with PERMISSIONS_ROUTE
                composable(AppDestinations.ONBOARDING_ROUTE) {
                    OnboardingScreen(
                        TODO(),
                        onNavigateNext = TODO()
                    )
                }

                composable(AppDestinations.MAIN_APP_ROUTE) {
                    MainScreen()
                }
            }
        }
    }

//    val isOnboardingComplete by onboardingViewModel.isOnboardingComplete.collectAsState()
//    var isInitialStateReady by remember { mutableStateOf(false) }

//    LaunchedEffect(key1 = isOnboardingComplete) {
//        if (!isInitialStateReady) {
//            isInitialStateReady = true
//        }
//    }

//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        if (isInitialStateReady) {
//            NavHost(
//                navController = navController,
//                startDestination = if (isOnboardingComplete) {
//                    AppDestinations.MAIN_APP_ROUTE
//                } else {
//                    AppDestinations.ONBOARDING_ROUTE
//                }
//            ) {
//                composable(AppDestinations.ONBOARDING_ROUTE) {
//                    OnboardingScreen(
//                        onNavigateNext = {
//                            navController.navigate(AppDestinations.MAIN_APP_ROUTE) {
//                                popUpTo(AppDestinations.ONBOARDING_ROUTE) {
//                                    inclusive = true
//                                }
//                                launchSingleTop = true
//                            }
//                        }
//                    )
//                }
//                composable(AppDestinations.MAIN_APP_ROUTE) {
//                    MainScreen()
//                }
//            }
//        } else {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        }
//    }

}