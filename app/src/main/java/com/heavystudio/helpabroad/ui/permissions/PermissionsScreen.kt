package com.heavystudio.helpabroad.ui.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Surface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.heavystudio.helpabroad.R

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onNavigateHome: () -> Unit,
    onNavigateCountrySelection: () -> Unit,
    viewModel: PermissionsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val items by viewModel.items.collectAsState()
    val canContinue by viewModel.canContinue.collectAsState()

    // Individual permission states (to show rationale per item)
    val locPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val callPermission = rememberPermissionState(Manifest.permission.CALL_PHONE)
    val smsPermission = rememberPermissionState(Manifest.permission.SEND_SMS)

    // Track whether user has attempted requests
    var locRequested by remember { mutableStateOf(false) }
    val callRequested by remember { mutableStateOf(false) }
    val smsRequested by remember { mutableStateOf(false) }

    // Push status updates to VM wheneve they change
    LaunchedEffect(locPermission.status, locRequested) {
        viewModel.onPermissionStatusChanged(
            index = 0,
            status = toPermissionStatus(
                locPermission.status.isGranted,
                locPermission.status.shouldShowRationale,
                locRequested
            )
        )
    }

    LaunchedEffect(callPermission.status, callRequested) {
        viewModel.onPermissionStatusChanged(
            index = 1,
            status = toPermissionStatus(
                callPermission.status.isGranted,
                callPermission.status.shouldShowRationale,
                callRequested
            )
        )
    }

    LaunchedEffect(smsPermission.status, smsRequested) {
        viewModel.onPermissionStatusChanged(
            index = 2,
            status = toPermissionStatus(
                smsPermission.status.isGranted,
                smsPermission.status.shouldShowRationale,
                smsRequested
            )
        )
    }

    // Collect navigation events
    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is PermissionsNavEvent.GoHome -> onNavigateHome()
                is PermissionsNavEvent.GoCountrySelection -> onNavigateCountrySelection()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_permissions_needed)) })
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    enabled = canContinue,
                    onClick = { viewModel.onContinue() }
                ) {
                    Text(stringResource(R.string.btn_continue))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // TODO PermissionRow
        }
    }
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Composable
private fun PermissionRow(
    titleRes: Int,
    descriptionRes: Int,
    rationaleRes: Int,
    status: PermissionStatus,
    onRequest: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column {
        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.titleMedium
        )

        when (status) {
            PermissionStatus.GRANTED -> {
                Text(
                    text = stringResource(R.string.permission_allowed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            PermissionStatus.DENIED -> {
                Text(
                    text = stringResource(rationaleRes),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.permission_denied),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onRequest) {
                        Text(text = stringResource(R.string.btn_allow))
                    }
                }
            }

            PermissionStatus.PERMANENTLY_DENIED -> {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.permission_permanently_denied),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onOpenSettings) {
                        Text(text = stringResource(R.string.btn_settings))
                    }
                }
            }

            PermissionStatus.UNKNOWN -> {
                Text(
                    text = stringResource(id = descriptionRes),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRequest) { Text(text = stringResource(R.string.btn_allow)) }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun toPermissionStatus(
    isGranted: Boolean,
    shouldShowRationale: Boolean,
    requestedOnce: Boolean
): PermissionStatus =
    when {
        isGranted -> PermissionStatus.GRANTED
        !isGranted && requestedOnce && !shouldShowRationale -> PermissionStatus.PERMANENTLY_DENIED
        !isGranted && shouldShowRationale -> PermissionStatus.DENIED
        else -> PermissionStatus.UNKNOWN
    }
