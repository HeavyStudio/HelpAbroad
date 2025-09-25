package com.heavystudio.helpabroad.ui.countries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.ui.main.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // On filtre la liste complète des pays en mémoire avec la requête de recherche
    val filteredCountries = if (uiState.searchQuery.isBlank()) {
        uiState.allCountries
    } else {
        uiState.allCountries.filter {
            it.name.contains(uiState.searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Countries List") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(filteredCountries, key = { it.countryId }) { country ->
                ListItem(
                    headlineContent = { Text(country.name) },
                    modifier = Modifier.clickable {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_country_id", country.countryId)

                        navController.popBackStack()
                    }
                )
                HorizontalDivider()
            }
        }
    }
}