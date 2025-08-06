package com.heavystudio.helpabroad.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.ui.components.HaTopAppBar
import com.heavystudio.helpabroad.ui.viewmodel.OnboardingViewModel
import com.heavystudio.helpabroad.ui.viewmodel.state.PermissionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    onNavigateNext: () -> Unit
) {
    val uiState by onboardingViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
    val callPhone = Manifest.permission.CALL_PHONE
    val sendSms = Manifest.permission.SEND_SMS
    val permissionGranted = PackageManager.PERMISSION_GRANTED

    // --- Permission Launchers ---
    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[fineLocation] == true
        val coarseLocationGranted = permissions[coarseLocation] == true
        val granted = fineLocationGranted || coarseLocationGranted

        if (granted) {
            onboardingViewModel.updateLocationPermissionStatus(PermissionStatus.Granted)
        } else {
            val activity = context as? Activity
            val permanentlyDenied = activity?.let {
                !it.shouldShowRequestPermissionRationale(fineLocation) &&
                !it.shouldShowRequestPermissionRationale(coarseLocation) &&
                ContextCompat.checkSelfPermission(context, fineLocation) != permissionGranted &&
                ContextCompat.checkSelfPermission(context, coarseLocation) != permissionGranted
            } ?: false
            onboardingViewModel.updateLocationPermissionStatus(
                PermissionStatus.Denied(permanentlyDenied)
            )
        }
    }

    val requestCallPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onboardingViewModel.updateCallPermissionStatus(PermissionStatus.Granted)
        } else {
            val activity = context as? Activity
            val permanentlyDenied = activity?.let {
                !it.shouldShowRequestPermissionRationale(callPhone) &&
                ContextCompat.checkSelfPermission(context, callPhone) != permissionGranted
            } ?: false
            onboardingViewModel.updateCallPermissionStatus(
                PermissionStatus.Denied(permanentlyDenied)
            )
        }
    }

    val requestSmsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onboardingViewModel.updateSmsPermissionStatus(PermissionStatus.Granted)
        } else {
            val activity = context as? Activity
            val permanentlyDenied = activity?.let {
                !it.shouldShowRequestPermissionRationale(sendSms) &&
                        ContextCompat.checkSelfPermission(context, sendSms) != permissionGranted
            } ?: false
            onboardingViewModel.updateSmsPermissionStatus(
                PermissionStatus.Denied(permanentlyDenied)
            )
        }
    }

    Scaffold(
        topBar = {
            HaTopAppBar(
                appName = stringResource(R.string.app_name),
                gradientColors = listOf(Color.Cyan, Color.Magenta)
            )
        },
        bottomBar = {
            val allPermissionsInteractedWith = listOf(
                uiState.locationPermissionStatus,
                uiState.callPermissionStatus,
                uiState.smsPermissionStatus
            ).all { it is PermissionStatus.Granted || it is PermissionStatus.Denied }

            Button(
                onClick = {
                    onboardingViewModel.onOnboardingAttemptProceed()
                    onNavigateNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp),
                enabled = allPermissionsInteractedWith
            ) {
                Text(stringResource(R.string.onboarding_button_continue))
            }
        }
    ) {  paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_title_permissions),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                modifier = Modifier.padding(8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = stringResource(R.string.onboarding_permissions_intro),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Location Permission Section
            PermissionInfoCard(
                title = stringResource(R.string.permission_location_title),
                titleIcon = Icons.Filled.MyLocation,
                rationale = stringResource(R.string.permission_location_rationale),
                status = uiState.locationPermissionStatus,
                onGrantClick = {
                    onboardingViewModel.updateLocationPermissionStatus(PermissionStatus.Requested)
                    requestLocationPermissionLauncher.launch(
                        arrayOf(fineLocation, coarseLocation)
                    )
                },
                consequenceIfDenied = stringResource(R.string.permission_location_consequence),
                onOpenSettingsClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Call Permission Section
            PermissionInfoCard(
                title = stringResource(R.string.permission_call_title),
                titleIcon = Icons.Filled.Phone,
                rationale = stringResource(R.string.permission_call_rationale),
                status = uiState.callPermissionStatus,
                onGrantClick = {
                    onboardingViewModel.updateCallPermissionStatus(PermissionStatus.Requested)
                    requestCallPermissionLauncher.launch(callPhone)
                },
                consequenceIfDenied = stringResource(R.string.permission_call_consequence),
                onOpenSettingsClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            // SMS Permission Section
            PermissionInfoCard(
                title = stringResource(R.string.permission_sms_title),
                titleIcon = Icons.Filled.Sms,
                rationale = stringResource(R.string.permission_sms_rationale),
                status = uiState.smsPermissionStatus,
                onGrantClick = {
                    onboardingViewModel.updateSmsPermissionStatus(PermissionStatus.Requested)
                    requestSmsPermissionLauncher.launch(sendSms)
                },
                consequenceIfDenied = stringResource(R.string.permission_sms_consequence),
                onOpenSettingsClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PermissionInfoCard(
    title: String,
    titleIcon: ImageVector,
    rationale: String,
    status: PermissionStatus,
    onGrantClick: () -> Unit,
    consequenceIfDenied: String,
    onOpenSettingsClick: () -> Unit
) {
    val cardColor = when (status) {
        PermissionStatus.Granted -> Color.Green
        is PermissionStatus.Denied -> Color.Red
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = titleIcon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rationale,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 1.5.em
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (status) {
                    PermissionStatus.Granted -> {
                        Text(
                            text = stringResource(R.string.permission_status_granted),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    is PermissionStatus.Denied -> {
                        Text(
                            text = stringResource(R.string.permission_status_denied),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    PermissionStatus.Requested -> {
                        Text(
                            text = stringResource(R.string.permission_status_requesting),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    PermissionStatus.NotRequested -> {
                        Text(
                            text = stringResource(R.string.permission_status_not_requested),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Action Button (Grant/Retry) - only if not Granted and not Denied Permanently
                if (status !is PermissionStatus.Granted) {
                    if (status is PermissionStatus.Denied && status.permanently) {
                        // TODO: If permanently denied, the main action is "Open Settings"
                    } else {
                        Button(
                            onClick = onGrantClick,
                            modifier = Modifier.wrapContentSize(),
                            colors = if (status is PermissionStatus.Denied) {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            } else {
                                ButtonDefaults.buttonColors()
                            }
                        ) {
                            Text(
                                text = when (status) {
                                    is PermissionStatus.Denied -> stringResource(R.string.permission_button_retry_grant)
                                    PermissionStatus.Requested -> stringResource(R.string.permission_status_requesting)
                                    else -> stringResource(R.string.permission_button_grant)
                                },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            if (status is PermissionStatus.Denied) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = consequenceIfDenied,
                    style = MaterialTheme.typography.bodySmall
                )
                if (status.permanently) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onOpenSettingsClick, modifier = Modifier.align(Alignment.End)) {
                        Text(stringResource(R.string.onboarding_button_open_settings))
                    }
                }
            }
        }
    }
}

