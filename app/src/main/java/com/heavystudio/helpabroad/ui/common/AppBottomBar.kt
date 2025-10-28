package com.heavystudio.helpabroad.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.heavystudio.helpabroad.ui.navigation.Screen

/**
 * A composable function that displays the application's bottom navigation bar.
 *
 * This bar contains navigation items for top-level destinations like Home, Countries, and Settings.
 * It observes the navigation back stack to highlight the currently selected screen and handles
 * navigation events to switch between screens, ensuring a consistent and state-preserving
 * user experience.
 *
 * @param navController The [NavController] used to handle navigation actions when an item is clicked.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the component.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomBar(navController: NavController) {
    val navItems = listOf(
        Screen.Home,
        Screen.Countries,
        Screen.Settings
    )

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        HorizontalDivider()
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            navItems.forEach { screen ->
                AppNavigationItem(
                    screen = screen,
                    isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        if (screen == Screen.Home) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        } else {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.AppNavigationItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .weight(1f)
            .height(80.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = stringResource(id = screen.labelResId),
            tint = contentColor
        )
        Text(
            text = stringResource(id = screen.labelResId),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}