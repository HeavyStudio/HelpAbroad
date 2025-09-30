package com.heavystudio.helpabroad.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heavystudio.helpabroad.ui.common.AppBottomBar
import com.heavystudio.helpabroad.ui.countries.CountriesScreen
import com.heavystudio.helpabroad.ui.home.HomeScreen
import com.heavystudio.helpabroad.ui.main.MainViewModel
import com.heavystudio.helpabroad.ui.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) { navBackStackEntry ->
                val viewModel: MainViewModel = hiltViewModel()
                val selectedId = navBackStackEntry.savedStateHandle.get<Int>("selected_country_id")

                LaunchedEffect(selectedId) {
                    if (selectedId != null) {
                        viewModel.onCountrySelected(selectedId)
                        navBackStackEntry.savedStateHandle.remove<Int>("selected_country_id")
                    }
                }

                HomeScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(Screen.Countries.route) {
                CountriesScreen(navController = navController)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }

            composable(Screen.About.route) {
                AboutScreen(navController = navController)
            }

            composable(Screen.Disclaimer.route) {
                DisclaimerScreen(navController = navController)
            }
        }
    }
}