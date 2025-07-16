package com.heavystudio.helpabroad.ui.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.ui.screen.composables.DynamicAppBarTitle
import com.heavystudio.helpabroad.ui.screen.composables.EmergencyContactsList
import com.heavystudio.helpabroad.ui.screen.composables.EmptyStateMessage
import com.heavystudio.helpabroad.ui.screen.composables.ErrorMessageDisplay
import com.heavystudio.helpabroad.ui.screen.composables.LoadingIndicator
import com.heavystudio.helpabroad.ui.screen.composables.PermissionRequestSection
import com.heavystudio.helpabroad.ui.viewmodel.EmergencyNumbersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(viewModel: EmergencyNumbersViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Remember the permission state
    var hasPhoneStatePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPhoneStatePermission = isGranted
            viewModel.initialLoadOrRefresh(isGranted)
        }
    )

    // Initial load or refresh when permission state is first determined or changes
    LaunchedEffect(key1 = hasPhoneStatePermission) {
        if (uiState.permissionRequiredMessage != null && !hasPhoneStatePermission) {
            // TODO
        } else {
            viewModel.initialLoadOrRefresh(hasPhoneStatePermission)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    DynamicAppBarTitle(
                        isLoading = uiState.isLoading,
                        permissionRequired = uiState.permissionRequiredMessage != null,
                        countryName = uiState.detectedCountry,
                        countryIso = uiState.detectedCountryIso
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (!hasPhoneStatePermission && uiState.permissionRequiredMessage != null) {
                PermissionRequestSection(
                    permissionMessage = uiState.permissionRequiredMessage!!,
                    onGrantPermissionClick = {
                        permissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                    }
                )
            }

            // Main content
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }

                uiState.errorKey != null -> {
                    ErrorMessageDisplay(message = uiState.errorKey!!)
                }

                !uiState.isLoading &&
                uiState.emergencyContacts.isEmpty() &&
                uiState.permissionRequiredMessage == null -> {
                    EmptyStateMessage(message = stringResource(R.string.error_no_contacts_for_country))
                }

                uiState.emergencyContacts.isNotEmpty() -> {
                    EmergencyContactsList(
                        contacts = uiState.emergencyContacts,
                        onCall = { phoneNumber ->
                            try {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:$phoneNumber".toUri()
                                }
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Log.e("EmergencyScreen", "Could not initiate call: $e")
                            }
                        }
                    )
                }
            }
        }
    }
}