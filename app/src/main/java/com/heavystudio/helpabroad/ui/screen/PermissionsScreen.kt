package com.heavystudio.helpabroad.ui.screen

import android.Manifest
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.logging.Logger
import com.heavystudio.helpabroad.ui.viewmodel.PermissionsViewModel
import com.heavystudio.helpabroad.ui.viewmodel.state.PermissionsUiState
import com.heavystudio.helpabroad.ui.viewmodel.state.PermissionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onNavigateToMain: () -> Unit,
    onNavigateBack: () -> Unit,
    permissionsViewModel: PermissionsViewModel = hiltViewModel()
) {
    // TODO: Replace with actual ViewModel instance passed from NavHost
    val viewModel: PermissionsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val canProceed = remember(uiState) {
        uiState.locationPermissionStatus != PermissionStatus.NotRequested &&
        uiState.callPermissionStatus != PermissionStatus.NotRequested &&
        uiState.smsPermissionStatus != PermissionStatus.NotRequested
    }

    // Permission Launchers
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsResult ->
            val isFineGranted = permissionsResult[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val isCoarseGranted = permissionsResult[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (isFineGranted) {
                viewModel.updateLocationPermissionStatus(PermissionStatus.FineGranted)
            } else if (isCoarseGranted) {
                viewModel.updateLocationPermissionStatus(PermissionStatus.CoarseGranted)
            } else {
                viewModel.updateLocationPermissionStatus(PermissionStatus.Denied)
            }
            checkIfAllPermissionsGrantedAndNavigate(viewModel, onNavigateToMain)
        }
    )

    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            val status = if (isGranted) PermissionStatus.Granted else PermissionStatus.Denied
            viewModel.updateCallPermissionStatus(status)
            checkIfAllPermissionsGrantedAndNavigate(viewModel, onNavigateToMain)
        }
    )

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            val status = if (isGranted) PermissionStatus.Granted else PermissionStatus.Denied
            viewModel.updateSmsPermissionStatus(status)
            checkIfAllPermissionsGrantedAndNavigate(viewModel, onNavigateToMain)
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Permissions") }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        permissionsViewModel.onOnboardingAttemptProceed()
                        Log.d("PermissionsScreen", "Value of isComplete: ${permissionsViewModel.isOnboardingComplete.value}")
                        onNavigateToMain()
                    },
                    enabled = canProceed,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) { Text("Continue") }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PermissionItem(
                title = "Location",
                description = "To find emergency numbers based on your location, we need access " +
                        "to your location.",
                buttonText = "Allow Location",
                permissionStatus = uiState.locationPermissionStatus,
                onRequestPermission = {
                    locationPermissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }
            )

            PermissionItem(
                title = "Call Phone",
                description = "To call emergency numbers directly from the app, we need permission " +
                        "to make calls.",
                buttonText = "Allow Call Phone",
                permissionStatus = uiState.callPermissionStatus,
                onRequestPermission = {
                    callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                }
            )

            PermissionItem(
                title = "Send SMS",
                description = "To send SMS messages to your emergency contacts, we need " +
                        "permission to send SMS messages.",
                buttonText = "Allow Send SMS",
                permissionStatus = uiState.smsPermissionStatus,
                onRequestPermission = {
                    smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                }
            )
        }
    }
}

@Composable
fun PermissionItem(
    title: String,
    description: String,
    buttonText: String,
    permissionStatus: PermissionStatus,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = description, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (permissionStatus) {
                PermissionStatus.NotRequested, PermissionStatus.Denied -> {
                    Button(
                        onClick = onRequestPermission,
                        shape = RoundedCornerShape(6.dp)
                    ) { Text(buttonText) }
                }
                PermissionStatus.Granted,
                PermissionStatus.FineGranted,
                PermissionStatus.CoarseGranted -> {
                    Text("Granted", color = MaterialTheme.colorScheme.primary)
                }
                else -> Button(
                    onClick = onRequestPermission,
                    shape = RoundedCornerShape(6.dp)
                ) { Text(buttonText) }
            }
        }
    }
}

// Helper function to check if all permissions are granted based on UI state
private fun areAllRequiredPermissionsGranted(uiState: PermissionsUiState): Boolean {
    return uiState.locationPermissionStatus == PermissionStatus.Granted &&
            uiState.callPermissionStatus == PermissionStatus.Granted &&
            uiState.smsPermissionStatus == PermissionStatus.Granted
}

// Helper function to check and navigate
private fun checkIfAllPermissionsGrantedAndNavigate(
    viewModel: PermissionsViewModel,
    onNavigateToMain: () -> Unit
) {
    if (areAllRequiredPermissionsGranted(viewModel.uiState.value)) {
        viewModel.onOnboardingAttemptProceed()
        onNavigateToMain()
    }
}