package com.heavystudio.helpabroad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.heavystudio.helpabroad.ui.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.CountrySelection,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .shadow(elevation = 6.dp, spotColor = Color.Black)
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val circleColor = if (selected) MaterialTheme.colorScheme.background else Color.Transparent
                val iconColor = MaterialTheme.colorScheme.onSurface
//                    if (selected) MaterialTheme.colorScheme.onSurface else Color.White

                Box(
                    modifier = Modifier
                        .size(45.dp)
//                        .shadow(
//                            elevation = 4.dp,
//                            shape = CircleShape,
//                            clip = false,
//                            spotColor = Color.Black
//                        )
                        .clip(CircleShape)
                        .background(circleColor)
                        .clickable {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelResId),
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}