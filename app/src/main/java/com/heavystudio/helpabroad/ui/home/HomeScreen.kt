package com.heavystudio.helpabroad.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity
import com.heavystudio.helpabroad.ui.components.AppBottomBar
import com.heavystudio.helpabroad.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // TODO: All the logic
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                appName = stringResource(R.string.app_name),
                appLogoIcon = Icons.Filled.Language,
                actions = {
                    IconButton(onClick = { viewModel.refreshAllData() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                navController = navController,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Welcome Title Section ---
            // Show this only if country name & flag are available and not in error state
            if (!uiState.isLoading && uiState.countryName != null && uiState.countryFlag != null &&
                uiState.errorMessage?.contains("country", ignoreCase = true) != true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Welcome to", // TODO: Replace with string resource
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    uiState.countryFlag?.let { flag ->
                        Text(
                            text = flag,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    uiState.countryName?.let { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
            // --- End Welcome Title Section ---

            // --- Loading / Error / Content Section ---
            when {
                uiState.isLoading && uiState.countryName == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading data...", // TODO: Replace with string resource
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                // Display specific error message if present
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshAllData() }) {
                            Text("Retry") // TODO: Replace with string resource
                        }
                    }
                }

                // Content display when data is available
                else -> {
                    // --- Address Section ---
                    Text(
                        text = "Current Location", // TODO: Replace with string resource
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.isAddressLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator()
                            Text(
                                text = " ${uiState.userAddress}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    } else {
                        Text(
                            text = uiState.userAddress,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // --- End Address Section ---

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Emergency Numbers Section ---
                    if (uiState.areEmergencyNumbersLoading) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator()
                            Text(
                                text = "Loading Emergency Numbers...", // TODO: Replace with string resource
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else if (uiState.emergencyNumbers.isNotEmpty()) {
                        Text(
                            text = "Emergency Numbers", // TODO: Replace with string resource
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        uiState.emergencyNumbers.forEach { numberEntity ->
                            TODO("Implement EmergencyNumberListItem")
                        }
                    }

                    Text(
                        text = "More content soon...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyNumberListItem(
    numberEntity: EmergencyNumberEntity,
    context: Context,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Call number", numberEntity.emergencyNumber) {
                val phoneNumber = numberEntity.emergencyNumber
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = "tel:$phoneNumber".toUri()
                }

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Could not initiate dial for $phoneNumber: ${e.message}")
                }
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = numberEntity.serviceId.toString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            val iconToShow = when (numberEntity.supportsSms) {
                true -> Icons.Filled.Sms
                else -> Icons.Filled.Call
            }
        }
    }
}