package com.heavystudio.helpabroad.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.ui.about.AboutScreen
import com.heavystudio.helpabroad.ui.common.AppBottomBar
import com.heavystudio.helpabroad.ui.common.AppTopBar
import com.heavystudio.helpabroad.ui.countries.CountriesScreen
import com.heavystudio.helpabroad.ui.countrydetails.CountryDetailsScreen
import com.heavystudio.helpabroad.ui.disclaimer.DisclaimerScreen
import com.heavystudio.helpabroad.ui.home.HomeScreen
import com.heavystudio.helpabroad.ui.settings.SettingsScreen

/**
 * Main composable that sets up the app's navigation structure.
 *
 * This function is responsible for:
 * - Creating and remembering a [NavController].
 * - Setting up a [Scaffold] with a dynamic [AppTopBar] and a persistent [AppBottomBar].
 * - Defining the navigation graph using a [NavHost], which maps routes to their corresponding
 *   screen composable.
 * - Managing the title of the [AppTopBar] based on the current route.
 * - Handling the visibility and action of the 'up' navigation button in the [AppTopBar].
 *
 * The navigation graph includes the following screens:
 * - [HomeScreen]: The start destination of the app.
 * - [CountriesScreen]: Displays a list of countries.
 * - [SettingsScreen]: For user-configurable settings.
 * - [AboutScreen]: Provides information about the app.
 * - [DisclaimerScreen]: Shows the app's disclaimer.
 * - [CountryDetailsScreen]: Shows details for a specific country, taking a `countryId` as an argument.
 *
 * @author Heavy Studio
 * @since 0.2.0 Added details screen
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarTitle = when {
        currentRoute == Screen.Home.route -> stringResource(id = R.string.app_name)
        currentRoute == Screen.Countries.route -> stringResource(id = R.string.screen_title_countries)
        currentRoute == Screen.Settings.route -> stringResource(id = R.string.screen_title_settings)
        currentRoute == Screen.About.route -> stringResource(id = R.string.screen_title_about)
        currentRoute == Screen.Disclaimer.route -> stringResource(id = R.string.screen_title_disclaimer)
        // Check if the route starts with "details" for the details screen
        currentRoute?.startsWith(Screen.Details.route) == true -> stringResource(id = R.string.screen_title_details)
        else -> ""
    }
    val canNavigateBack = navController.previousBackStackEntry != null && currentRoute != Screen.Home.route


    Scaffold(
        topBar = {
            AppTopBar(
                title = topBarTitle,
                canNavigateBack = canNavigateBack,
                onNavigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            AppBottomBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = hiltViewModel()
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

            composable(
                route = Screen.Details.route + "/{countryId}",
                arguments = listOf(navArgument("countryId") { type = NavType.IntType })
            ) { backStackEntry ->
                val countryId = backStackEntry.arguments?.getInt("countryId")
                if (countryId != null) {
                    CountryDetailsScreen(
                        navController = navController,
                        countryId = countryId
                    )
                }
            }
        }
    }
}