package com.heavystudio.helpabroad.ui.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
            Row(
                modifier = Modifier
                    .drawBehind {
                        val borderSize = 2.dp.toPx()
                        val start = Offset(0f, size.height)
                        val end = Offset(size.width, size.height)
                        drawLine(
                            color = Color(0xFFA27B5B),
                            start = start,
                            end = end,
                            strokeWidth = borderSize
                        )
                    }
            ) {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.title_permissions_needed))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .shadow(4.dp, ambientColor = MaterialTheme.colorScheme.primary)
                )
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
//                    contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    enabled = canContinue,
                    onClick = { viewModel.onContinue() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                ) {
                    Text(stringResource(R.string.btn_continue))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            PermissionRow(
                titleRes = R.string.title_location,
                descriptionRes = R.string.desc_location,
                rationaleRes = R.string.rationale_location,
                status = items[0].status,
                onRequest = {
                    locRequested = true
                    locPermission.launchPermissionRequest()
                },
                onOpenSettings = { openAppSettings(context) }
            )

            Spacer(Modifier.height(16.dp))

            PermissionRow(
                titleRes = R.string.title_call_phone,
                descriptionRes = R.string.desc_call_phone,
                rationaleRes = R.string.rationale_call_phone,
                status = items[1].status,
                onRequest = {
                    locRequested = true
                    callPermission.launchPermissionRequest()
                },
                onOpenSettings = { openAppSettings(context) }
            )

            Spacer(Modifier.height(16.dp))

            PermissionRow(
                titleRes = R.string.title_send_sms,
                descriptionRes = R.string.desc_send_sms,
                rationaleRes = R.string.rationale_send_sms,
                status = items[2].status,
                onRequest = {
                    locRequested = true
                    smsPermission.launchPermissionRequest()
                },
                onOpenSettings = { openAppSettings(context) }
            )
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
                    text = stringResource(id = descriptionRes),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.permission_allowed),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.desc_ic_allowed),
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            PermissionStatus.DENIED -> {
                Text(
                    text = stringResource(rationaleRes),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.permission_denied),
                        modifier = Modifier.weight(1f).padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = onRequest,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
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
                    Button(onClick = onOpenSettings, shape = RoundedCornerShape(6.dp)) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onRequest,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(text = stringResource(R.string.btn_allow))
                    }
                }
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
