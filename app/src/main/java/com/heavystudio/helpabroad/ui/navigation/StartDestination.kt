package com.heavystudio.helpabroad.ui.navigation

sealed class StartDestination(val route: String) {
    object Welcome : StartDestination("welcome_route")
    object Permissions : StartDestination("permissions_route")
    object Home : StartDestination("home_route")
    object CountrySelection : StartDestination("country_selection_route")
    object Settings : StartDestination("settings_route")
}