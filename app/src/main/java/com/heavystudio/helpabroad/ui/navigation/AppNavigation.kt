package com.heavystudio.helpabroad.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heavystudio.helpabroad.ui.screen.WelcomeScreen
import com.heavystudio.helpabroad.ui.viewmodel.MainViewModel
import com.heavystudio.helpabroad.ui.viewmodel.WelcomeViewModel

@Composable
fun AppNavigation() {
    val mainViewModel: MainViewModel = hiltViewModel()
    val startDestinationState by mainViewModel.startDestination.collectAsState()

    startDestinationState?.let { startDestination ->
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startDestination.route
        ) {

            composable(StartDestination.Welcome.route) {
                val welcomeViewModel: WelcomeViewModel = hiltViewModel()
                WelcomeScreen(
                    viewModel = welcomeViewModel,
                    onContinueClick = {
                        navController.navigate(StartDestination.Permissions.route) {
                            popUpTo(StartDestination.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(StartDestination.Permissions.route) {
                // TODO: PermissionsScreen(...) + PermissionsViewModel
            }

            composable(StartDestination.Home.route) {
                // TODO: HomeScreen(...) + HomeViewModel
            }

            composable(StartDestination.CountrySelection.route) {
                // TODO: CountrySelectionScreen(...) + CountrySelectionViewModel
            }

            composable(StartDestination.Settings.route) {
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