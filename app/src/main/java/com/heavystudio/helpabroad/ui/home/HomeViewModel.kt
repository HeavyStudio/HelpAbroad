package com.heavystudio.helpabroad.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.core.AppConfig
import com.heavystudio.helpabroad.data.settings.SettingsRepository
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 *
 * This ViewModel manages the UI state for the home screen, primarily handling the logic
 * for searching countries. It exposes a [StateFlow] of [HomeUiState] which the UI can
 * observe to react to changes in the search query and results.
 *
 * @param countryRepository The repository for fetching country data.
 *
 * @author Heavy Studio.
 * @since 0.2.0 Divided the MainViewModel into specific VM for each screen.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val countryRepository: CountryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // --- State for the search query, controlled by the UI ---
    private val _searchQuery = MutableStateFlow("")

    // --- Language determination ---
    private val effectiveLangCode: String = run {
        val deviceLang = Locale.getDefault().language
        if (deviceLang in AppConfig.supportedLanguages) deviceLang else "en"
    }

    // --- Flow for search results based on the query ---
    private val searchResultsFlow = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            Log.d("SEARCH_DEBUG", "[VM] flatMapLatest executing. Query: '$query'")
            if (query.length < 2) {
                flowOf(emptyList())
            } else {
                Log.d("SEARCH_DEBUG", "Calling repository.searchCountries...")
                countryRepository.searchCountries(query, effectiveLangCode)
            }
        }

    private val recentlySearchedCountriesFlow = settingsRepository.recentlySearchedCountriesFlow
        .flatMapLatest { ids ->
            if (ids.isEmpty()) {
                flowOf(emptyList())
            } else {
                // Fetch country details for the saved IDs.
                countryRepository.getCountriesByIds(ids, effectiveLangCode)
            }
        }
        // This ensures the order is preserved from DataStore (most recent first)
        .combine(settingsRepository.recentlySearchedCountriesFlow) { countries, ids ->
            val countryMap = countries.associateBy { it.countryId }
            ids.mapNotNull { id -> countryMap[id] }
        }

    // --- The final UI state, combining the query and results ---
    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery,
        searchResultsFlow,
        recentlySearchedCountriesFlow
    ) { query, results, recent ->
        Log.d("SEARCH_DEBUG", "Combine running. Query: '$query', Results count: ${results.size}")
        HomeUiState(
            searchQuery = query,
            searchResults = results,
            isSearchResultsVisible = query.isNotBlank() && results.isNotEmpty(),
            recentlyViewed = recent,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    /**
     * Updates the search query.
     *
     * This function is called from the UI whenever the user types in the search bar.
     * It updates the internal `_searchQuery` state flow, which in turn triggers
     * the search logic to execute.
     *
     * @param query The new search string entered by the user.
     */
    fun onSearchQueryChanged(query: String) {
        Log.d("SEARCH_DEBUG", "onSearchQueryChanged called with: '$query'")
        _searchQuery.value = query
    }

    /**
     * Resets the search state after a navigation event has been handled.
     *
     * This function should be called after the user has navigated away from the home screen
     * to a details screen (e.g., after tapping on a search result). Its primary purpose is
     * to clear the search query, which in turn hides the search results and resets the UI
     * to its initial state, ready for the user's return to the home screen.
     */
    fun onNavigationToDetailsHandled() {
        _searchQuery.value = ""
    }
}