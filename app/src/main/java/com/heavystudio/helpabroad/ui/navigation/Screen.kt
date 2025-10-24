package com.heavystudio.helpabroad.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.heavystudio.helpabroad.R

/**
 * Represents the different screens in the application for navigation purposes.
 *
 * This sealed class defines each screen with its associated navigation route, a string resource for
 * its label, and an icon. This provides a type-safe way to handle navigation throughout the app.
 *
 * @property route The unique string identifier for the navigation route.
 * @property labelResId The string resource ID for the screen's title or label.
 * @property icon The [ImageVector] to be displayed for the screen, e.g., in a navigation bar.
 *
 * @author Heavy Studio.
 * @since 0.2.0 Added Details screen.
 * @since 0.1.0 Creation of the sealed class.
 */
sealed class Screen(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector
) {
    object Home : Screen("home", R.string.nav_home, Icons.Filled.Home)
    object Countries : Screen("countries", R.string.nav_countries, Icons.Filled.Language)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings)
    object Details : Screen("details", R.string.screen_title_details, Icons.Default.Info)

    object About : Screen("about", R.string.screen_title_about, Icons.Filled.Info)
    object Disclaimer : Screen("disclaimer", R.string.screen_title_disclaimer, Icons.Filled.Warning)
}