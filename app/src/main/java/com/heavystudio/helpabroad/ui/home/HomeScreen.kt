package com.heavystudio.helpabroad.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import com.heavystudio.helpabroad.ui.main.MainViewModel
import com.heavystudio.helpabroad.ui.main.UiCountryDetails
import com.heavystudio.helpabroad.ui.main.UiEmergencyService
import com.heavystudio.helpabroad.ui.theme.HelpAbroadTheme
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.heavystudio.helpabroad.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.app_name)) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            TextButton(
                onClick = { navController.navigate(Screen.Countries.route)},
                modifier = Modifier.fillMaxWidth()
            ) { 
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text("All Countries Button")
            }

            // Le Box est crucial ici, il permet de superposer la liste des résultats
            // par-dessus le contenu principal de l'écran.
            Box {
                // Colonne pour le contenu principal (barre de recherche et détails/prompt)
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChanged = viewModel::onSearchQueryChanged,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Contenu principal qui change en fonction de l'état
                    when {
                        uiState.isLoading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        uiState.selectedCountryDetails != null -> {
                            CountryDetailsCard(details = uiState.selectedCountryDetails!!)
                        }
                        else -> {
                            EmptyStatePrompt()
                        }
                    }
                }

                // La liste des résultats, qui s'affiche "par-dessus" si elle est visible
                if (uiState.isSearchResultsVisible) {
                    SearchResultsDropdown(
                        results = uiState.searchResults,
                        onCountryClick = { countryId -> 
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            viewModel.onCountrySelected(countryId)
                        },
                        // Le padding permet de la positionner juste sous la barre de recherche
                        modifier = Modifier.padding(top = 80.dp)
                    )
                }
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
        singleLine = true
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

@Composable
private fun CountryDetailsCard(details: UiCountryDetails) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = details.countryName,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            LazyColumn {
                items(details.services) { service ->
                    EmergencyServiceItem(
                        service = service,
                        onItemClick = { phoneNumber ->
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:$phoneNumber".toUri()
                            }
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun EmergencyServiceItem(
    service: UiEmergencyService,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(service.number) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = service.name, style = MaterialTheme.typography.bodyLarge)
        Text(text = service.number, style = MaterialTheme.typography.titleMedium)
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

@Preview(showBackground = true, widthDp = 360)
@Composable
fun HomeScreenPreview() {
    HelpAbroadTheme {
        // Vous pouvez prévisualiser les différents états ici
        val fakeDetails = UiCountryDetails(
            countryName = "France",
            services = listOf(
                UiEmergencyService("Police", "17"),
                UiEmergencyService("Pompiers", "18"),
                UiEmergencyService("SAMU", "15")
            )
        )
        // Remplacez le composant ci-dessous pour voir les différents états
        // EmptyStatePrompt()
        CountryDetailsCard(details = fakeDetails)
    }
}