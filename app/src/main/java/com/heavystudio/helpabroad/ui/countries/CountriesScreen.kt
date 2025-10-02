package com.heavystudio.helpabroad.ui.countries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.heavystudio.helpabroad.ui.common.isoCodeToFlagEmoji
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

    LazyColumn() {
        item { Spacer(modifier = Modifier.height(24.dp)) }
        items(filteredCountries, key = { it.countryId }) { country ->
            ListItem(
                leadingContent = {
                    Text(
                        text = isoCodeToFlagEmoji(country.isoCode),
                        fontSize = 28.sp
                    )
                },
                headlineContent = { Text(country.name) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                },
                modifier = Modifier.clickable {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_country_id", country.countryId)

                    navController.popBackStack()
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )
            HorizontalDivider()
        }
    }
}