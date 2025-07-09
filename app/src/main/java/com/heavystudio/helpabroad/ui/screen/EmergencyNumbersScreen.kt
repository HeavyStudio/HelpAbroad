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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        if (isGranted) {
            Log.d("PermissionScreen", "READ_PHONE_STATE permission GRANTED")
            viewModel.refreshEmergencyNumbers()
        } else {
            Log.w("PermissionScreen", "READ_PHONE_STATE permission DENIED")
        }
    }

    // Effect to check and request permission when the screen is composed
    // or when you decide it's appropriate to ask
    LaunchedEffect(Unit) {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            PackageManager.PERMISSION_GRANTED -> {
                Log.d("PermissionScreen", "Permission already granted, loading numbers.")
                viewModel.refreshEmergencyNumbers()
            }
            else -> {
                Log.d("PermissionScreen", "Permission not granted, requesting.")
                requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.errorMessage != null) {
            Text("Error: ${uiState.errorMessage}")
        } else if (uiState.emergencyNumbers.isEmpty()) {
            Text("No emergency numbers found.")
        } else {
            // Display numbers in a simple column for now
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Emergency Numbers:")
                uiState.emergencyNumbers.forEach { number ->
                    Text(
                        text = number,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .clickable {
                                // Create an Intent to dial the number
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:$number".toUri()
                                }
                                // Check if there's an app to handle this Intent
                                if (dialIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(dialIntent)
                                } else {
                                    Log.w("Dialer", "No application can handle ACTION_DIAL")
                                }
                            }
                            .padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to explicitly refresh/retry
                Button(onClick = {
                    when (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                        PackageManager.PERMISSION_GRANTED -> viewModel.refreshEmergencyNumbers()
                        else -> requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                    }
                }) {
                    Text("Refresh Numbers")
                }
            }
        }
    }
}