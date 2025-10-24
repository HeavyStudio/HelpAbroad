package com.heavystudio.helpabroad.ui.countrydetails

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.ui.common.isoCodeToFlagEmoji
import com.heavystudio.helpabroad.ui.model.UiCountryDetails
import com.heavystudio.helpabroad.ui.model.UiEmergencyService
import com.heavystudio.helpabroad.ui.navigation.Screen
import com.heavystudio.helpabroad.ui.theme.AmbulanceGreen
import com.heavystudio.helpabroad.ui.theme.DefaultServiceGray
import com.heavystudio.helpabroad.ui.theme.DispatchOrange
import com.heavystudio.helpabroad.ui.theme.FireRed
import com.heavystudio.helpabroad.ui.theme.PoliceBlue

/**
 * A Composable screen that displays detailed information about a specific country,
 * including its emergency service numbers.
 *
 * This screen observes a [CountryDetailsViewModel] to get the country's details. It handles
 * loading, error, and success states. It also manages the `CALL_PHONE` permission
 * and displays a confirmation dialog before making a call if the user has enabled that setting.
 *
 * @param navController The NavController used for navigating to other screens, such as the disclaimer.
 * @param countryId The unique identifier for the country whose details are to be displayed.
 *                  This is not directly used by the Composable but is required by the ViewModel
 *                  which is instantiated by Hilt's navigation-compose integration.
 * @param viewModel The ViewModel instance that provides the UI state and handles business logic for this screen.
 *                  It is provided by Hilt.
 *
 * @author Heavy Studio.
 * @since 0.2.0 Creation of the screen.
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailsScreen(
    navController: NavController,
    countryId: Int,
    viewModel: CountryDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val callPermissionState = rememberPermissionState(permission = Manifest.permission.CALL_PHONE)
    val context = LocalContext.current

    // Handle call confirmation dialog
    if (uiState.numberToConfirmBeforeCall != null) {
        CallConfirmationDialog(
            numberToCall = uiState.numberToConfirmBeforeCall!!,
            onConfirm = {
                val number = uiState.numberToConfirmBeforeCall!!
                val intent = Intent(Intent.ACTION_CALL, "tel:$number".toUri())
                context.startActivity(intent)
                viewModel.onCallConfirmationDismissed()
            },
            onDismiss = viewModel::onCallConfirmationDismissed
        )
    }

    // Main content area
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Divider below the TopAppBar
        HorizontalDivider()

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.countryDetails != null) {
            // Display the details using the LazyColumn
            CountryDetailsContent(
                navController = navController,
                details = uiState.countryDetails!!,
                uiState = uiState,
                callPermissionState = callPermissionState,
                onEmergencyNumberClick = viewModel::onEmergencyNumberClicked
            )
        } else {
            // Show an error message if details couldn't be loaded
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Could not load country details.")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CountryDetailsContent(
    navController: NavController,
    details: UiCountryDetails,
    uiState: CountryDetailsUiState,
    callPermissionState: PermissionState,
    onEmergencyNumberClick: (String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        // Header with Flag and Name
        item {
            CountryHeader(
                isoCode = details.countryIsoCode,
                countryName = details.countryName
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // List of Emergency Service Cards
        items(details.services) { service ->
            EmergencyServiceCard(
                service = service,
                onClick = { phoneNumber ->
                    if (uiState.isDirectCallEnabled) {
                        if (callPermissionState.status.isGranted) {
                            if (uiState.isConfirmBeforeCallEnabled) {
                                onEmergencyNumberClick(phoneNumber)
                            } else {
                                // Direct call without confirmation
                                val intent = Intent(Intent.ACTION_CALL, "tel:$phoneNumber".toUri())
                                context.startActivity(intent)
                            }
                        } else {
                            callPermissionState.launchPermissionRequest()
                        }
                    } else {
                        // Open Dialer
                        val intent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
                        context.startActivity(intent)
                    }
                }
            )
        }

        // Short Disclaimer link
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.home_disclaimer_short),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        navController.navigate(Screen.Disclaimer.route)
                    }
            )
        }
    }
}

@Composable
private fun CountryHeader(isoCode: String, countryName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = isoCodeToFlagEmoji(isoCode), fontSize = 30.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = countryName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmergencyServiceCard(
    service: UiEmergencyService,
    onClick: (String) -> Unit
) {
    val (serviceIcon, serviceColor) = when (service.code) {
        "POLICE" -> Pair(Icons.Default.LocalPolice, PoliceBlue)
        "AMBULANCE" -> Pair(Icons.Default.LocalHospital, AmbulanceGreen)
        "SAMU" -> Pair(Icons.Default.LocalHospital, AmbulanceGreen)
        "FIRE" -> Pair(Icons.Default.LocalFireDepartment, FireRed)
        "DISPATCH" -> Pair(Icons.Default.Sos, DispatchOrange)
        else -> Pair(Icons.AutoMirrored.Filled.HelpOutline, DefaultServiceGray)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(service.number) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = serviceIcon,
                contentDescription = service.name,
                modifier = Modifier.size(40.dp),
                tint = serviceColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = service.number,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = stringResource(R.string.call_action),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CallConfirmationDialog(
    numberToCall: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.call_confirmation_title)) },
        text = { Text(stringResource(R.string.call_confirmation_message)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.call_action))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_action))
            }
        }
    )
}