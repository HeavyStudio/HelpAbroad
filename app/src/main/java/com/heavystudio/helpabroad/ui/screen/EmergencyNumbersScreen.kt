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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heavystudio.helpabroad.data.model.EmergencyContact
import com.heavystudio.helpabroad.ui.viewmodel.EmergencyNumbersViewModel

@Composable
fun BasicEmergencyNumbersScreen(
    viewModel: EmergencyNumbersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d("PermissionScreen", "Permission result: isGranted = $isGranted")
        viewModel.initialLoadOrRefresh(hasPermission = isGranted)
    }

    // Effect to check initial permission status and load numbers or request permission
    LaunchedEffect(Unit) {
        val initialPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("PermissionScreen", "LaunchedEffect: Initial permission granted = " +
                "$initialPermissionGranted")
        viewModel.initialLoadOrRefresh(hasPermission = initialPermissionGranted)

        if (!initialPermissionGranted) {
            Log.d("PermissionScreen", "LaunchedEffect: Requesting permission.")
            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.permissionRequiredMessage != null -> {
                PermissionDeniedContent(
                    message = uiState.permissionRequiredMessage!!,
                    onRequestPermission = {
                        requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                    }
                )
            }
            uiState.errorMessage != null -> {
                ErrorContent(
                    message = uiState.errorMessage!!,
                    onRetry = {
                        val isGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) == PackageManager.PERMISSION_GRANTED
                        if (isGranted) {
                            viewModel.refreshEmergencyNumbers()
                            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                        }
                    }
                )
            }
            uiState.emergencyContacts.isEmpty() -> {
                NoDataContent(
                    onRefresh = {
                        val isGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) == PackageManager.PERMISSION_GRANTED
                        if (isGranted) {
                            viewModel.refreshEmergencyNumbers()
                        } else {
                            viewModel.initialLoadOrRefresh(hasPermission = false)
                            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                        }
                    }
                )
            }
            else -> {
                EmergencyNumbersList(
                    contacts = uiState.emergencyContacts,
                    onRefresh = {
                        val isGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) == PackageManager.PERMISSION_GRANTED
                        if (isGranted) {
                            viewModel.refreshEmergencyNumbers()
                        } else {
                            viewModel.initialLoadOrRefresh(hasPermission = false)
                            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EmergencyNumbersList(
    contacts: List<EmergencyContact>,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Numéros d'urgence :",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        contacts.forEach { contact ->
            EmergencyContactRow(contactInfo = contact, context = context)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRefresh) {
            Text("Actualiser")
        }
    }
}

@Composable
fun EmergencyContactRow(contactInfo: EmergencyContact, context: android.content.Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = "tel:${contactInfo.number}".toUri()
                }
                if (dialIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(dialIntent)
                } else {
                    Log.w("Dialer", "No application can handle ACTION_DIAL")
                }
            }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = contactInfo.type,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = contactInfo.number,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PermissionDeniedContent(message: String, onRequestPermission: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = message, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = message, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun NoDataContent(onRefresh: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "No emergency numbers found for your region.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRefresh) {
            Text("Refresh")
        }
    }
}