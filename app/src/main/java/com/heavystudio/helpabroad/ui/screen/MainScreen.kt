package com.heavystudio.helpabroad.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.ui.components.HaTopAppBar
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme
import com.heavystudio.helpabroad.ui.viewmodel.HomeViewModel
import com.heavystudio.helpabroad.ui.viewmodel.OnboardingViewModel
import com.heavystudio.helpabroad.ui.viewmodel.state.LocationActionRequired

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val locationState by homeViewModel.locationUiState.collectAsState()

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
                gradientColors = listOf(Color.Cyan, Color.Magenta)
            )
        }
    ) { paddingValues ->
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
                    text = "Street: ${locationState.street}"
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

            Button(onClick = {
                onboardingViewModel.resetOnboardingStatus()
                Toast.makeText(
                    context,
                    "Onboarding reset. Please restart the app.",
                    Toast.LENGTH_LONG
                ).show()
            }) {
                Text("DEV - Reset Onboarding")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HelpAbroadTheme {
        MainScreen()
    }
}