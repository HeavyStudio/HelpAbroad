package com.heavystudio.helpabroad.ui.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.heavystudio.helpabroad.data.model.EmergencyContact
import com.heavystudio.helpabroad.ui.viewmodel.EmergencyNumbersViewModel
import com.heavystudio.helpabroad.utils.FlagUtils

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
            TopAppBar(title = { Text("Emergency Numbers") })
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

            // Display country
            if (!uiState.isLoading &&
                uiState.permissionRequiredMessage == null &&
                uiState.detectedCountry != null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Region: ${uiState.detectedCountry}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val flagEmoji = getFlagEmojiForCountryCode(uiState.detectedCountryIso)
                    if (flagEmoji.isNotEmpty()) {
                        Text(
                            text = flagEmoji,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 18.dp))
            }

            // Main content
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }

                uiState.errorMessage != null -> {
                    ErrorMessageDisplay(message = uiState.errorMessage!!)
                }

                !uiState.isLoading &&
                uiState.emergencyContacts.isEmpty() &&
                uiState.permissionRequiredMessage == null -> {
                    EmptyStateMessage(message = "No emergency numbers found for your region.")
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

@Composable
fun PermissionRequestSection(permissionMessage: String, onGrantPermissionClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = permissionMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(onClick = onGrantPermissionClick) {
            Text("Grant Permission")
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessageDisplay(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, textAlign = TextAlign.Center)
    }
}

@Composable
fun EmergencyContactsList(
    contacts: List<EmergencyContact>,
    onCall: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(contacts, key = { it.number + it.type }) { contact ->
            EmergencyContactItem(contact = contact, onCall = onCall)
        }
    }
}

@Composable
fun EmergencyContactItem(
    contact: EmergencyContact,
    onCall: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onCall(contact.number) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = getIconForType(contact.type),
                    contentDescription = contact.type,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = contact.type,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = contact.number,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                }
            }
            IconButton(onClick = { onCall(contact.number) }) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Call ${contact.number}",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

fun getIconForType(type: String): ImageVector {
    return when {
        type.contains("Police", ignoreCase = true) -> Icons.Default.LocalPolice
        type.contains("Ambulance", ignoreCase = true) -> Icons.Default.LocalHospital
        type.contains("Fire", ignoreCase = true) -> Icons.Default.LocalFireDepartment
        else -> Icons.Default.Warning
    }
}

fun getFlagEmojiForCountryCode(countryCode: String?): String {
    if (countryCode.isNullOrBlank() || countryCode.length != 2) {
        return ""
    }

    val firstLetter =
        Character.codePointAt(countryCode.uppercase(), 0) - 'A'.code + 0x1F1E6
    val secondLetter =
        Character.codePointAt(countryCode.uppercase(), 1) - 'A'.code + 0x1F1E6

    val firstChar = String(Character.toChars(firstLetter))
    val secondChar = String(Character.toChars(secondLetter))

    return firstChar + secondChar
}