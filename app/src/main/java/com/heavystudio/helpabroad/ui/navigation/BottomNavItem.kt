package com.heavystudio.helpabroad.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.heavystudio.helpabroad.R

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelResId: Int
) {
    object Home : BottomNavItem(
        "home",
        Icons.Filled.Home,
        R.string.label_home
    )

    object CountrySelection : BottomNavItem(
        "country_selection",
        Icons.Filled.Language,
        R.string.label_country_selection
    )

    object Settings : BottomNavItem(
        "settings",
        Icons.Filled.Settings,
        R.string.label_settings
    )
}