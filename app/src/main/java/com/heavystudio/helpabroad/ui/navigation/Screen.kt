package com.heavystudio.helpabroad.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Countries : Screen("countries")
    object Settings : Screen("settings")
}