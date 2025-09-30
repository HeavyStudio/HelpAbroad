package com.heavystudio.helpabroad.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.heavystudio.helpabroad.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showThemeDialog by remember { mutableStateOf(false) }

    // On prépare le gestionnaire de la permission d'appel
    val callPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.CALL_PHONE
    )

    if (showThemeDialog) {
        ThemeChooserDialog(
            currentTheme = uiState.theme,
            onThemeSelected = { viewModel.onThemeChanged(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // --- Section Apparence ---
            SettingsHeader(stringResource(R.string.settings_header_appearance))
            SettingItem(
                title = stringResource(R.string.settings_theme_title),
                description = stringResource(id = uiState.theme.toStringRes()),
                onClick = { showThemeDialog = true }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            // --- Section Fonctionnalités ---
            SettingsHeader(stringResource(R.string.settings_header_features))
            SwitchSettingItem(
                title = stringResource(R.string.settings_direct_call_title),
                description = stringResource(R.string.settings_direct_call_desc),
                checked = uiState.isDirectCallEnabled,
                onCheckedChange = { isEnabled ->
                    if (isEnabled) {
                        if (!callPermissionState.status.isGranted) {
                            callPermissionState.launchPermissionRequest()
                        }
                        viewModel.onDirectCallToggled(callPermissionState.status.isGranted)
                    } else {
                        viewModel.onDirectCallToggled(false)
                    }
                }
            )

            SwitchSettingItem(
                title = stringResource(R.string.settings_confirm_call_title),
                description = stringResource(R.string.settings_confirm_call_desc),
                checked = uiState.isConfirmBeforeCallEnabled,
                onCheckedChange = viewModel::onConfirmBeforeCallToggled,
                enabled = uiState.isDirectCallEnabled
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            // --- Section A propos ---
            SettingsHeader(stringResource(R.string.settings_header_about))
            SettingItem(
                title = stringResource(R.string.settings_about_title),
                onClick = { navController.navigate(Screen.About.route) }
            )
            SettingItem(
                title = stringResource(R.string.settings_disclaimer_title),
                onClick = { navController.navigate(Screen.Disclaimer.route) }
            )
        }
    }

}

@Composable
private fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
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
            .padding(vertical = 12.dp)
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
            .padding(vertical = 8.dp),
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

@Composable
private fun ThemeChooserDialog(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_theme_title)) },
        text = {
            Column {
                AppTheme.entries.forEach { theme ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (theme == currentTheme),
                                onClick = { onThemeSelected(theme) }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (theme == currentTheme),
                            onClick = { onThemeSelected(theme) }
                        )
                        Text(
                            text = stringResource(id = theme.toStringRes()),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
private fun AppTheme.toStringRes(): Int {
    return when (this) {
        AppTheme.LIGHT -> R.string.theme_light
        AppTheme.DARK -> R.string.theme_dark
        AppTheme.SYSTEM -> R.string.theme_system
    }
}