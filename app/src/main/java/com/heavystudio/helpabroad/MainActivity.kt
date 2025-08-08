package com.heavystudio.helpabroad

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.heavystudio.helpabroad.ui.screen.PermissionsScreen
import com.heavystudio.helpabroad.ui.screen.WelcomeScreen
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme
import com.heavystudio.helpabroad.ui.viewmodel.PermissionsViewModel
import dagger.hilt.android.AndroidEntryPoint


// TODO: Move to a util file
fun areAllRequiredPermissionsGranted(context: Context): Boolean {
    val fineLocationGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseLocationGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val callPhoneGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.CALL_PHONE
    ) == PackageManager.PERMISSION_GRANTED
    val sendSmsGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.SEND_SMS
    ) == PackageManager.PERMISSION_GRANTED

    return (fineLocationGranted || coarseLocationGranted) && callPhoneGranted && sendSmsGranted
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
    val permissionsViewModel: PermissionsViewModel = hiltViewModel()

    var startDestination by remember { mutableStateOf<String?>(null)}
    var isLoadingStates by remember { mutableStateOf(true) }

    Log.d("MAIN_ACTIVITY_NAV", "HelpAbroadAppNavigation Composable recomposed/initialized.")
    Log.d("MAIN_ACTIVITY_NAV", "PermissionsViewModel instance: $permissionsViewModel")

    LaunchedEffect(key1 = permissionsViewModel.isOnboardingComplete) {
        Log.e("MAIN_ACTIVITY_NAV", "--> LAUNCHED_EFFECT_STARTED_VERY_FIRST_LINE")
//        val onboardingComplete = permissionsViewModel.isOnboardingComplete.value
//        val allSufficientPermissionsGrantedAtStart = areAllRequiredPermissionsGranted(context)
//
//        startDestination = if (!allSufficientPermissionsGrantedAtStart) {
//            AppDestinations.WELCOME_ROUTE
//        } else {
//            if (onboardingComplete) {
//                AppDestinations.MAIN_APP_ROUTE
//            } else {
//                AppDestinations.PERMISSIONS_ROUTE
//            }
//        }
//        isLoadingStates = false

        permissionsViewModel.isOnboardingComplete.collect { onboardingCompleteValue ->
            Log.d("PERMISSIONS_VM_MAINACTIVITY", "  L_EFFECT: Collected isOnboardingComplete.value: $onboardingCompleteValue")
            val allSufficientPermissionsGrantedAtStart = areAllRequiredPermissionsGranted(context)
            Log.d("PERMISSIONS_VM_MAINACTIVITY", "  L_EFFECT: All Permissions Granted at Start: $allSufficientPermissionsGrantedAtStart")

            val newDestination = if (!allSufficientPermissionsGrantedAtStart) {
                AppDestinations.WELCOME_ROUTE
            } else {
                if (onboardingCompleteValue) {
                    AppDestinations.MAIN_APP_ROUTE
                } else {
                    AppDestinations.PERMISSIONS_ROUTE
                }
            }

            if (startDestination != newDestination) {
                startDestination = newDestination
                Log.e("PERMISSIONS_VM_MAINACTIVITY", "  L_EFFECT: ---> Determined and SET startDestination to: $startDestination")
            }

            if (isLoadingStates) {
                isLoadingStates = false
                Log.d("PERMISSIONS_VM_MAINACTIVITY", "  L_EFFECT: isLoadingStates set to false.")
            }
        }

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

                composable(AppDestinations.PERMISSIONS_ROUTE) {
                    PermissionsScreen(
                        onNavigateToMain = {
                            navController.navigate(AppDestinations.MAIN_APP_ROUTE) {
                                popUpTo(AppDestinations.WELCOME_ROUTE) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onNavigateBack = {
                            navController.popBackStack(AppDestinations.WELCOME_ROUTE, inclusive = false)
                        }
                    )
                }

                composable(AppDestinations.MAIN_APP_ROUTE) {
                    MainScreen(
                        onNavigateToWelcome = {
                            navController.navigate(AppDestinations.WELCOME_ROUTE) {
                                popUpTo(AppDestinations.WELCOME_ROUTE) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
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