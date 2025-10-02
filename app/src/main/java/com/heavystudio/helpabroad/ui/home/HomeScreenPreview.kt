package com.heavystudio.helpabroad.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heavystudio.helpabroad.ui.main.UiCountryDetails
import com.heavystudio.helpabroad.ui.main.UiEmergencyService
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme

// CORRECTION 2: On définit des couleurs spécifiques pour la preview
private val previewBlue = Color(0xFF007AFF)
private val previewLightGrayBg = Color(0xFFF0F0F7)
private val previewCardBg = Color.White
private val previewInactiveGray = Color(0xFF8A8A8E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContentPreview() {
    val fakeCountryDetails = UiCountryDetails(
        countryName = "France",
        countryIsoCode = "fr",
        services = listOf(
            UiEmergencyService(code = "POLICE", name = "Police", number = "17"),
            UiEmergencyService(code = "FIRE", name = "Fire", number = "18"),
            UiEmergencyService(code = "AMBULANCE", name = "Ambulance", number = "15"),
        )
    )

    Scaffold(
        containerColor = previewLightGrayBg, // Fond gris très clair
        topBar = {
            TopAppBar(
                // CORRECTION 1: Le nom de l'app est restauré
                title = { Text("Help Abroad") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomBarPreview()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Search for a country") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    unfocusedContainerColor = previewCardBg
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            CountryDetailsContentPreview(details = fakeCountryDetails)
        }
    }
}

@Composable
private fun CountryDetailsContentPreview(details: UiCountryDetails) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = isoCodeToFlagEmoji("US"), fontSize = 30.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = details.countryName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            details.services.forEach { service ->
                EmergencyServiceCardPreview(service = service)
            }
        }
    }
}

@Composable
private fun EmergencyServiceCardPreview(service: UiEmergencyService) {
    val serviceIcon = when (service.code) {
        "POLICE" -> Icons.Default.LocalPolice
        "AMBULANCE" -> Icons.Default.LocalHospital
        "FIRE" -> Icons.Default.LocalFireDepartment
        else -> Icons.Default.HelpOutline
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E5EA)),
        colors = CardDefaults.cardColors(containerColor = previewCardBg)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .clickable { },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = serviceIcon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = previewInactiveGray
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.number,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = previewInactiveGray
                )
            }
        }
    }
}

@Composable
private fun BottomBarPreview() {
    NavigationBar(
        // CORRECTION 3: Fond blanc
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* */ },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                // CORRECTION 3: Icône active bleue, inactives noires
                selectedIconColor = previewBlue,
                unselectedIconColor = Color.Black,
                selectedTextColor = previewBlue,
                unselectedTextColor = Color.Black,
                // On retire l'indicateur en pilule
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* */ },
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Countries") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = previewBlue,
                unselectedIconColor = Color.Black,
                selectedTextColor = previewBlue,
                unselectedTextColor = Color.Black,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* */ },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = previewBlue,
                unselectedIconColor = Color.Black,
                selectedTextColor = previewBlue,
                unselectedTextColor = Color.Black,
                indicatorColor = Color.Transparent
            )
        )
    }
}

private fun isoCodeToFlagEmoji(isoCode: String): String {
    return isoCode
        .uppercase()
        .map { char -> Character.toString(char.code + 0x1F1A5) }
        .joinToString("")
}

@Preview(name = "Final Light Mode Preview", showBackground = true, backgroundColor = 0xFFF0F0F7)
@Composable
fun HomeScreenFinalPreview() {
    HelpAbroadTheme(darkTheme = false) {
        HomeScreenContentPreview()
    }
}