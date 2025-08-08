package com.heavystudio.helpabroad.ui.screen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.ui.components.HaTopAppBar
import com.heavystudio.helpabroad.ui.viewmodel.HomeViewModel
import com.heavystudio.helpabroad.ui.viewmodel.PermissionsViewModel
//import com.heavystudio.helpabroad.ui.viewmodel.OnboardingViewModel
import com.heavystudio.helpabroad.ui.viewmodel.state.LocationActionRequired

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    permissionsViewModel: PermissionsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNavigateToWelcome: () -> Unit
) {
    val context = LocalContext.current
    val locationState by homeViewModel.locationUiState.collectAsState()
    var showCoucouDialog by remember { mutableStateOf(false) }

    // Handle LocationActionRequired
    LaunchedEffect(locationState.requiresAction) {
        when (locationState.requiresAction) {
            LocationActionRequired.SETTINGS_NOT_OPTIMAL -> {
                Toast.makeText(
                    context,
                    "Please enable high accuracy location settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
            LocationActionRequired.NO_PERMISSION -> TODO()
            null -> { /* No action required */ }
        }
    }

    // Display general error messages
    LaunchedEffect(locationState.errorMessage) {
        locationState.errorMessage?.let {
            if (locationState.requiresAction != LocationActionRequired.SETTINGS_NOT_OPTIMAL ||
                !it.contains("settings are not optimal", ignoreCase = true)) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            HaTopAppBar(
                appName = stringResource(R.string.app_name),
                gradientColors = listOf(Color.Cyan, Color.Magenta),
                actionContent = {
                    when {
                        locationState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        locationState.countryFlagEmoji != null -> {
                            locationState.countryFlagEmoji?.let { emoji ->
                                Text(
                                    text = emoji,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                        locationState.errorMessage != null && locationState.countryFlagEmoji == null -> {
                            IconButton(onClick = { showCoucouDialog = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Map,
                                    contentDescription = "Map"
                                )
                            }
                        }
                        else -> {
                            IconButton(onClick = { showCoucouDialog = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Map,
                                    contentDescription = "Map"
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showCoucouDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCoucouDialog = false
                },
                title = {
                    Text(text = "Menu Action")
                },
                text = {
                    Text(text = "Coucou")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCoucouDialog = false
                        }
                    ) { Text("OK") }
                }
            )
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (locationState.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (locationState.latitude != null && locationState.longitude != null) {
                val welcomeTo = "Welcome to"
                val flagAndCountry = StringBuilder().apply {
                    locationState.countryFlagEmoji?.let {
                        append(it)
                    }
                    append(" ")
                    locationState.countryName?.let {
                        append(it)
                    }
                }
                Text(
                    text = "$welcomeTo $flagAndCountry",
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()
                Text(
                    text = "${locationState.fullAddress}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Latitude: ${"%.4f".format(locationState.latitude)}")
                Text(text = "Longitude: ${"%.4f".format(locationState.longitude)}")
                locationState.countryCode?.let {
                    Text("Country Code: $it")
                }
                locationState.countryName?.let {
                    Text("Country Name: $it")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(onClick = { homeViewModel.fetchCurrentLocation() }) {
                Text(
                    if (locationState.latitude == null && !locationState.isLoading) {
                        "Get Current Location"
                    } else {
                        "Refresh Location"
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            GoToAppSettingsButton(permissionsViewModel)

            Button(
                onClick = {
                    onNavigateToWelcome()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Go back to Welcome Screen")}
        }
    }
}

@Composable
fun GoToAppSettingsButton(
    permissionsViewModel: PermissionsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    Button(onClick = {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
        permissionsViewModel.resetOnboardingStatus()
    }) { Text("Go to App Permission Settings and Reset Onboarding") }
}

