package com.heavystudio.helpabroad.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heavystudio.helpabroad.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    appName: String,
    appLogoIcon: ImageVector = Icons.Filled.Public,
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = appLogoIcon,
                    contentDescription = stringResource(R.string.desc_ic_logo),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        navigationIcon = {},
        actions = {
            if (actions != null) {
                actions()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

// --- Preview ---
@Preview(name = "AppTopBar - Basic", showBackground = true)
@Composable
fun AppTopBarPreviewBasic() {
    AppTopBar(appName = "Help Abroad")
}

@Preview(name = "AppTopBar - With Action", showBackground = true)
@Composable
fun AppTopBarPreviewWithAction() {
    AppTopBar(
        appName = "Help Abroad",
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "Refresh Location"
                )
            }
        }
    )
}

@Preview(name = "AppTopBar - Custom Logo & Multiple Actions", showBackground = true)
@Composable
fun AppTopBarPreviewCustomLogoAndActions() {
    AppTopBar(
        appName = "Help Abroad",
        appLogoIcon = Icons.Filled.Workspaces,
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "Refresh"
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}