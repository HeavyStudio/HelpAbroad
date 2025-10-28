package com.heavystudio.helpabroad.ui.countries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.ui.common.isoCodeToFlagEmoji
import com.heavystudio.helpabroad.ui.navigation.Screen

/**
 * A Composable screen that displays a searchable list of countries.
 *
 * This screen allows the user to search for a country by its name or ISO code.
 * When a country is selected from the list, the screen navigates back to the
 * previous screen and passes the selected country's ID via the NavController's
 * `savedStateHandle`. This is a common pattern for a "picker" screen.
 *
 * @param navController The NavController used for navigation actions, specifically
 *                      to pop the back stack and return the result to the previous screen.
 * @param viewModel The [CountriesViewModel] instance, which is responsible for
 *                  providing the list of countries and managing the screen's state.
 *                  It is provided by Hilt's `hiltViewModel()`.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesScreen(
    navController: NavController,
    viewModel: CountriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    val filteredCountries = if (searchQuery.isBlank()) {
        uiState.allCountries
    } else {
        uiState.allCountries.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.isoCode.contains(searchQuery, ignoreCase = true)
        }
    }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            placeholder = { Text(text = stringResource(R.string.search_country_placeholder)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )

        )

        LazyColumn(state = viewModel.listState) {
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
                            ?.set("countryId", country.countryId)

                        navController.navigate(Screen.Details.route + "/${country.countryId}")
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                )
                HorizontalDivider()
            }
        }
    }
}