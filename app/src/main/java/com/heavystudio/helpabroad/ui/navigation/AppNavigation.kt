package com.heavystudio.helpabroad.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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
import com.heavystudio.helpabroad.ui.permissions.PermissionsScreen
import com.heavystudio.helpabroad.ui.permissions.PermissionsViewModel
import com.heavystudio.helpabroad.ui.welcome.WelcomeViewModel

@Composable
fun AppNavigation() {
    val mainViewModel: MainViewModel = hiltViewModel()
    val startDestinationState by mainViewModel.startDestination.collectAsState()

    startDestinationState?.let { startDestination ->
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            composable(Routes.WELCOME) {
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
                val permissionsViewModel: PermissionsViewModel = hiltViewModel()
                PermissionsScreen(
                    onNavigateHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.PERMISSIONS) { inclusive = true }
                        }
                    },
                    onNavigateCountrySelection = {
                        navController.navigate(Routes.COUNTRY_SELECTION) {
                            popUpTo(Routes.PERMISSIONS) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                // TODO: HomeScreen(...) + HomeViewModel
            }

            composable(Routes.COUNTRY_SELECTION) {
                // TODO: CountrySelectionScreen(...) + CountrySelectionViewModel
            }

            composable(Routes.SETTINGS) {
                // TODO: SettingsScreen(...) + SettingsViewModel
            }
        }
    } ?: run {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}