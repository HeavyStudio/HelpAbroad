package com.heavystudio.helpabroad.ui.home

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import com.heavystudio.helpabroad.ui.common.AppTopBar
import com.heavystudio.helpabroad.ui.common.isoCodeToFlagEmoji
import com.heavystudio.helpabroad.ui.main.MainUiState
import com.heavystudio.helpabroad.ui.main.MainViewModel
import com.heavystudio.helpabroad.ui.main.UiCountryDetails
import com.heavystudio.helpabroad.ui.main.UiEmergencyService
import com.heavystudio.helpabroad.ui.navigation.Screen
import com.heavystudio.helpabroad.ui.theme.AmbulanceRed
import com.heavystudio.helpabroad.ui.theme.DefaultServiceGray
import com.heavystudio.helpabroad.ui.theme.FireOrange
import com.heavystudio.helpabroad.ui.theme.PoliceBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val callPermissionState = rememberPermissionState(permission = Manifest.permission.CALL_PHONE)
    val context = LocalContext.current

    if (uiState.numberToCallForConfirmation != null) {
        CallConfirmationDialog(
            numberToCall = uiState.numberToCallForConfirmation!!,
            onConfirm = {
                val number = uiState.numberToCallForConfirmation!!
                val intent = Intent(Intent.ACTION_CALL, "tel:$number".toUri())
                context.startActivity(intent)
                viewModel.onCallConfirmationDismissed()
            },
            onDismiss = viewModel::onCallConfirmationDismissed
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Box {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChanged = viewModel::onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.selectedCountryDetails != null -> {
                        CountryDetailsContent(
                            navController = navController,
                            details = uiState.selectedCountryDetails!!,
                            uiState = uiState,
                            callPermissionState = callPermissionState,
                            onEmergencyNumberClick = { number ->
                                viewModel.onEmergencyNumberClicked(number)
                            }
                        )
                    }
                    else -> {
                        EmptyStatePrompt()
                    }
                }
            }

            if (uiState.isSearchResultsVisible) {
                SearchResultsDropdown(
                    results = uiState.searchResults,
                    onCountryClick = { countryId ->
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        viewModel.onCountrySelected(countryId)
                    },
                    modifier = Modifier.padding(top = 88.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        label = { Text(stringResource(R.string.search_country_placeholder)) },
        modifier = modifier,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon_desc)
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun SearchResultsDropdown(
    results: List<CountryListItem>,
    onCountryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(results, key = { it.countryId }) { country ->
                Text(
                    text = country.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCountryClick(country.countryId) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CountryDetailsContent(
    navController: NavController,
    details: UiCountryDetails,
    uiState: MainUiState,
    callPermissionState: PermissionState,
    onEmergencyNumberClick: (String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            CountryHeader(
                isoCode = details.countryIsoCode,
                countryName = details.countryName
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(details.services) { service ->
            EmergencyServiceCard(
                service = service,
                onClick = { phoneNumber ->
                    if (uiState.isDirectCallEnabled) {
                        if (callPermissionState.status.isGranted) {
                            if (uiState.isConfirmBeforeCallEnabled) {
                                onEmergencyNumberClick(phoneNumber)
                            } else {
                                val intent = Intent(Intent.ACTION_CALL, "tel:$phoneNumber".toUri())
                                context.startActivity(intent)
                            }
                        } else {
                            callPermissionState.launchPermissionRequest()
                        }
                    } else {
                        val intent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
                        context.startActivity(intent)
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.home_disclaimer_short),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
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
        Text(
            text = isoCodeToFlagEmoji(isoCode),
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
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
        "AMBULANCE" -> Pair(Icons.Default.LocalHospital, AmbulanceRed)
        "FIRE" -> Pair(Icons.Default.LocalFireDepartment, FireOrange)
        else -> Pair(Icons.AutoMirrored.Default.HelpOutline, DefaultServiceGray)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(service.number) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = serviceIcon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = serviceColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Text(
                        text = service.number,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = stringResource(R.string.call_action),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

        }
    }
}

@Composable
private fun EmptyStatePrompt() {
    Text(
        text = stringResource(R.string.home_prompt_message),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(32.dp)
    )
}

@Composable
private fun CallConfirmationDialog(
    numberToCall: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.call_confirmation_title)) },
        text = { Text(stringResource(R.string.call_confirmation_message, numberToCall)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.call_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_action))
            }
        }
    )
}