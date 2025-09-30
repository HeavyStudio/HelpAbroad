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

sealed class Screen(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector
) {
    object Home : Screen("home", R.string.nav_home, Icons.Filled.Home)
    object Countries : Screen("countries", R.string.nav_countries, Icons.Filled.Language)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings)

    object About : Screen("about", R.string.screen_title_about, Icons.Filled.Info)
    object Disclaimer : Screen("disclaimer", R.string.screen_title_disclaimer, Icons.Filled.Warning)
}