package com.heavystudio.helpabroad.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import com.heavystudio.helpabroad.ui.common.isoCodeToFlagEmoji
import com.heavystudio.helpabroad.ui.navigation.Screen

/**
 * A Composable function that represents the main screen of the application.
 *
 * This screen features a search bar that allows users to search for countries.
 * Search results are displayed in a dropdown list. If no search is active,
 * an empty state prompt is shown. Clicking on a search result navigates
 * to the details screen for the selected country.
 *
 * @param navController The NavController used for navigating to other screens.
 * @param viewModel The [HomeViewModel] that provides state and handles business logic for this screen.
 *
 * @author Heavy Studio.
 * @since 0.2.1 Added search history.
 * @since 0.2.0 Moved results to CountryDetailsScreen.
 * @since 0.1.0 Creation of the HomeScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(
            query = uiState.searchQuery,
            onQueryChanged = viewModel::onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isSearchResultsVisible) {
            SearchResultsDropdown(
                results = uiState.searchResults,
                onCountryClick = { countryId ->
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    navController.navigate(Screen.Details.route + "/$countryId")
                    viewModel.onNavigationToDetailsHandled()
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            HomeScreenContent(
                recentlyViewed = uiState.recentlyViewed,
                onCountryClick = { countryId ->
                    navController.navigate(Screen.Details.route + "/$countryId")
                }
            )
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
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(text = stringResource(R.string.search_country_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_icon_desc)
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(results, key = { it.countryId }) { country ->
                Text(
                    text = country.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCountryClick(country.countryId) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                if (results.lastOrNull() != country) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun EmptyStatePrompt(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.home_prompt_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HomeScreenContent(
    recentlyViewed: List<CountryListItem>,
    onCountryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between cols
        verticalArrangement = Arrangement.spacedBy(8.dp) // Space between lines
    ) {
        // --- Welcome section ---
        // span allows an item to occupy multiple columns
        item(span = { GridItemSpan(maxLineSpan)}) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.home_welcome_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        // --- History section title ---
        if (recentlyViewed.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.home_recently_viewed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }

            // --- History section (grid) ---
            items(recentlyViewed, key = { "recent-${it.countryId}" }) { country ->
                RecentCountryItem(
                    country = country,
                    onClick = { onCountryClick(country.countryId) }
                )
            }
        }
    }
}

@Composable
private fun RecentCountryItem(
    country: CountryListItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = isoCodeToFlagEmoji(country.isoCode))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = country.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}