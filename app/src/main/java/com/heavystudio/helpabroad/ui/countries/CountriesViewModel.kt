package com.heavystudio.helpabroad.ui.countries

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.core.AppConfig
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Countries screen.
 *
 * This ViewModel is responsible for fetching the list of countries from the repository,
 * handling the UI state, and persisting the scroll position of the country list. It determines
 * the appropriate language for the country data based on the device's locale, defaulting to
 * English if the device's language is not supported.
 *
 * @param repository The repository for fetching country data.
 *
 * @author Heavy Studio.
 * @since 0.2.0 Divided MainViewModel into individual ViewModels for each screen.
 */
@HiltViewModel
class CountriesViewModel @Inject constructor(
    repository: CountryRepository
) : ViewModel() {

    // Save the scroll state here so that it persists
    val listState = LazyListState()

    private val _effectiveLangCode: String

    init {
        val deviceLang = Locale.getDefault().language
        _effectiveLangCode = if (deviceLang in AppConfig.supportedLanguages) deviceLang else "en"
    }

    val uiState: StateFlow<CountriesUiState> = repository.getAllCountries(langCode = _effectiveLangCode)
        .map { countries ->
            CountriesUiState(allCountries = countries, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CountriesUiState()
        )
}