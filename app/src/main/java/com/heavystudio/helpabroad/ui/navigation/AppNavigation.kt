package com.heavystudio.helpabroad.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heavystudio.helpabroad.ui.welcome.WelcomeScreen
import com.heavystudio.helpabroad.ui.MainViewModel
import com.heavystudio.helpabroad.ui.home.HomeScreen
import com.heavystudio.helpabroad.ui.permissions.PermissionsScreen
import com.heavystudio.helpabroad.ui.permissions.PermissionsViewModel
import com.heavystudio.helpabroad.ui.welcome.WelcomeViewModel

@Composable
fun AppNavigation() {
    Log.d("AppNav", "AppNavigation Composable Entered")
    val mainViewModel: MainViewModel = hiltViewModel()
    val startDestinationState by mainViewModel.startDestination.collectAsState()
    Log.d("AppNav", "Initial startDestinationState: $startDestinationState")

    startDestinationState?.let { startDestination ->
        Log.d("AppNav", "StartDestination is: $startDestination. Remembering NavController")
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            Log.d("AppNav", "NavHost Composed. Current Start Dest: $startDestination")

            composable(Routes.WELCOME) {
                Log.d("AppNav", "Composing ${Routes.WELCOME}")
                val welcomeViewModel: WelcomeViewModel = hiltViewModel()
                WelcomeScreen(
                    viewModel = welcomeViewModel,
                    onContinueClick = {
                        navController.navigate(Routes.PERMISSIONS) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.PERMISSIONS) {
                Log.d("AppNav", "Composing ${Routes.PERMISSIONS}")
                val permissionsViewModel: PermissionsViewModel = hiltViewModel()
                PermissionsScreen(
                    onNavigateHome = {
                        Log.d("AppNav", "PERMISSIONS: onNavigateHome triggered. " +
                                "Navigating to HOME.")
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.PERMISSIONS) { inclusive = true }
                        }
                    },
                    onNavigateCountrySelection = {
                        Log.d("AppNav", "PERMISSIONS: onNavigateCountrySelection triggered.")
                        navController.navigate(Routes.COUNTRY_SELECTION) {
                            popUpTo(Routes.PERMISSIONS) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                Log.d("AppNav", "Composing ${Routes.HOME} - This is the target screen.")
                // TODO: HomeViewModel
                HomeScreen(
                    navController = navController
                )
                Log.d("AppNav", "Finished composing ${Routes.HOME}")
            }

            composable(Routes.COUNTRY_SELECTION) {
                // TODO: CountrySelectionScreen(...) + CountrySelectionViewModel
                Log.d("AppNav", "Composing ${Routes.COUNTRY_SELECTION}")
                Text("CountrySelectionScreen Placeholder")
            }

            composable(Routes.SETTINGS) {
                // TODO: SettingsScreen(...) + SettingsViewModel
                Log.d("AppNav", "Composing ${Routes.SETTINGS}")
                Text("SettingsScreen Placeholder")
            }
        }
    } ?: run {
        Log.d("AppNav", "StartDestination is NULL, showing CircularProgressIndicator.")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}