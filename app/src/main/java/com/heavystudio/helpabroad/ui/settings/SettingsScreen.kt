package com.heavystudio.helpabroad.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Vertices
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.settings.AppTheme
import com.heavystudio.helpabroad.ui.common.AppTopBar
import com.heavystudio.helpabroad.ui.navigation.Screen

/**
 * A Composable that displays the settings screen of the application.
 *
 * This screen allows users to configure various application settings, grouped into sections:
 * - **Appearance**: Users can select the app's theme (Light, Dark, or System default).
 * - **Features**:
 *     - Toggle "Direct Call": Allows making calls directly from the app. This requires the `CALL_PHONE` permission.
 *     - Toggle "Confirm Before Call": Shows a confirmation dialog before initiating a call (only enabled if "Direct Call" is on).
 * - **About**: Provides navigation links to the "About" and "Disclaimer" screens.
 *
 * The screen's state is managed by [SettingsViewModel] and is collected as [SettingsUiState].
 * It uses a `LazyColumn` to efficiently display the list of settings.
 *
 * @param navController The [NavController] used for navigating to other screens (e.g., About, Disclaimer).
 * @param viewModel The [SettingsViewModel] instance that provides the UI state and handles user actions.
 *                  It is provided by Hilt's `hiltViewModel()`.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the screen.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val callPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.CALL_PHONE,
        onPermissionResult = { permissionWasGranted ->
            if (permissionWasGranted) {
                viewModel.onDirectCallToggled(true)
            } else {
                viewModel.onDirectCallToggled(false)
            }
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // --- Section Apparence ---
        item {
            SettingsHeader(stringResource(R.string.settings_header_appearance))
            Spacer(modifier = Modifier.height(8.dp))
            ThemeSelector(
                currentTheme = uiState.theme,
                onThemeSelected = viewModel::onThemeChanged
            )
        }

        // --- Section Fonctionnalités ---
        item {
            SettingsHeader(stringResource(R.string.settings_header_features))
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                SwitchSettingItem(
                    title = stringResource(R.string.settings_direct_call_title),
                    description = stringResource(R.string.settings_direct_call_desc),
                    checked = uiState.isDirectCallEnabled,
                    onCheckedChange = { isEnabled ->
                        if (isEnabled) {
                            callPermissionState.launchPermissionRequest()
                        } else {
                            viewModel.onDirectCallToggled(false)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                SwitchSettingItem(
                    title = stringResource(R.string.settings_confirm_call_title),
                    description = stringResource(R.string.settings_confirm_call_desc),
                    checked = uiState.isConfirmBeforeCallEnabled,
                    onCheckedChange = viewModel::onConfirmBeforeCallToggled,
                    enabled = uiState.isDirectCallEnabled
                )
            }
        }

        item {
            // --- Section A propos ---
            SettingsHeader(stringResource(R.string.settings_header_about))
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column {
                    SettingItem(
                        title = stringResource(R.string.settings_about_title),
                        onClick = { navController.navigate(Screen.About.route) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                    SettingItem(
                        title = stringResource(R.string.settings_disclaimer_title),
                        onClick = { navController.navigate(Screen.Disclaimer.route) }
                    )
                }
            }
        }
    }

}

@Composable
private fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp)
    )
}

@Composable
private fun SettingItem(
    title: String,
    description: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SwitchSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = { onCheckedChange(!checked) })
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    val themeOptions = AppTheme.entries
    val cornerRadius = 8.dp

    Surface(
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clip(RoundedCornerShape(cornerRadius)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            themeOptions.forEachIndexed { index, theme ->
                val isSelected = currentTheme == theme
                val backgroundColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
                val textColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(backgroundColor)
                        .clickable { onThemeSelected(theme) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = theme.toStringRes()),
                        color = textColor,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                if (index < themeOptions.size - 1) {
                    VerticalDivider(
                        modifier = Modifier.height(30.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTheme.toStringRes(): Int {
    return when (this) {
        AppTheme.LIGHT -> R.string.theme_light
        AppTheme.DARK -> R.string.theme_dark
        AppTheme.SYSTEM -> R.string.theme_system
    }
}