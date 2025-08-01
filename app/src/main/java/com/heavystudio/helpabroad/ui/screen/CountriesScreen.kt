package com.heavystudio.helpabroad.ui.screen // Your screen's package

// import androidx.compose.ui.platform.LocalContext // Not strictly needed in this version
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heavystudio.helpabroad.R
import com.heavystudio.helpabroad.data.database.CountryEntity
import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity
import com.heavystudio.helpabroad.ui.viewmodel.CountriesViewModel
import com.heavystudio.helpabroad.utils.EmergencyServiceIds
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesScreen(
    countriesViewModel: CountriesViewModel = viewModel()
) {
    val allCountries by countriesViewModel.allCountries.collectAsState()
    val selectedCountry by countriesViewModel.selectedCountry.collectAsState()
    val currentEmergencyNumbers by countriesViewModel.currentEmergencyNumbers.collectAsState()

    val isLoadingCountries by countriesViewModel.isLoadingCountries.collectAsState()
    val isLoadingNumbers by countriesViewModel.isLoadingNumbers.collectAsState()
    val errorMessage by countriesViewModel.errorMessage.collectAsState()

    val languageConfig = LocalConfiguration.current
    val displayLocale = languageConfig.locales[0]
    val snackbarHostState = remember { SnackbarHostState() }

    val topAppBarTitle = selectedCountry?.let { country ->
        val flag = country.flagEmoji ?: ""
        val countryName = Locale.Builder().setRegion(country.isoCode).build().getDisplayCountry(displayLocale)

        if (flag.isNotEmpty()) {
            "$flag $countryName"
        } else {
            countryName
        }
    } ?: stringResource(id = R.string.app_name) // Default to app name

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it, // ViewModel errors should be localized or generic
                duration = SnackbarDuration.Short
            )
            countriesViewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(topAppBarTitle) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            // Section for loading or displaying country selector
            if (isLoadingCountries) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.loading_countries), modifier = Modifier.padding(start = 8.dp))
                }
            } else if (allCountries.isEmpty() && errorMessage == null) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_countries_available))
                }
            } else if (allCountries.isNotEmpty()) {
                CountrySearchScreen(
                    countries = allCountries,
                    selectedCountry = selectedCountry,
                    onCountrySelected = { country ->
                        countriesViewModel.selectCountry(country)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    displayLocale = displayLocale
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section for displaying selected country's details
            val currentSelectedCountry = selectedCountry
            if (currentSelectedCountry == null && allCountries.isNotEmpty() && !isLoadingCountries) {
                // Prompt to select a country if list is loaded but nothing is chosen yet
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        // This uses your specific key "select_country".
                        // Note: "Select Country" as a prompt might be brief.
                        // A more complete sentence like "Please select a country..." might be better UX,
                        // for which you might use a different string resource.
                        stringResource(R.string.select_country),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium // Made it a bit larger
                    )
                }
            } else if (currentSelectedCountry != null) {
                // Title for the numbers section
                Text(
                    // Assuming you have a string like: <string name="emergency_numbers_for_country">Numéros d'urgence pour %1$s</string>
                    stringResource(
                        R.string.emergency_numbers_for_country,
                        Locale.Builder().setRegion(currentSelectedCountry.isoCode).build().getDisplayCountry(displayLocale)
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (isLoadingNumbers) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        // Assuming you have a string like: <string name="loading_numbers">Chargement des numéros...</string>
                        Text(stringResource(R.string.loading_numbers), modifier = Modifier.padding(start = 8.dp))
                    }
                } else {
                    CountryNumbersView(
                        country = currentSelectedCountry,
                        emergencyNumbersList = currentEmergencyNumbers,
                        displayLocale = displayLocale
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySearchScreen(
    countries: List<CountryEntity>,
    selectedCountry: CountryEntity?,
    onCountrySelected: (CountryEntity) -> Unit,
    modifier: Modifier = Modifier,
    displayLocale: Locale
) {

    // TESTING KEYBOARD
    var testText by remember { mutableStateOf("") }
    val testFocusRequester = remember { FocusRequester() }
    val localKeyboardController = LocalSoftwareKeyboardController.current
    // END TESTING KEYBOARD
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Filter countries based on search query
    val filteredCountries = remember(searchQuery, countries, displayLocale) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val query = searchQuery.trim()
            val uppercaseQuery = query.uppercase()

            val commonAbbreviations = mapOf(
                "UK" to "GB",
                "SCOTLAND" to "GB",
                "England".uppercase() to "GB",
                "Wales".uppercase() to "GB",
                "Northern Ireland".uppercase() to "GB"
            )

            var partialAbbreviationTargetIso: String? = null
            if (uppercaseQuery.isNotEmpty()) {
                for ((abbrKey, isoValue) in commonAbbreviations) {
                    if (abbrKey.startsWith(uppercaseQuery)) {
                        partialAbbreviationTargetIso = isoValue
                        if (abbrKey == uppercaseQuery) {
                            break
                        }
                    }
                }
            }

            countries
                .mapNotNull { country ->
                    val countryName = Locale.Builder().setRegion(country.isoCode).build().getDisplayCountry(displayLocale)
                    val isoCode = country.isoCode.trim()

                    val startsWithName = countryName.startsWith(query, ignoreCase = true)
                    val containsName = !startsWithName && countryName.contains(query, ignoreCase = true)
                    val startsWithIso = isoCode.startsWith(query, ignoreCase = true)
                    val containsIso = !startsWithIso && isoCode.contains(query, ignoreCase = true)
                    val isAbbreviationMatch = partialAbbreviationTargetIso != null && isoCode.equals(partialAbbreviationTargetIso, ignoreCase = true)

                    val priority = when {
                        isAbbreviationMatch -> 0
                        startsWithName -> 1
                        startsWithIso -> 2
                        containsName -> 3
                        containsIso -> 4
                        else -> null
                    }

                    priority?.let { Pair(country, it) }
                }
                .sortedBy { it.second }
                .map { it.first }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.select_country),
            style = MaterialTheme.typography.headlineSmall,
            modifier = modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isSearchActive = focusState.isFocused
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                },
            label = { Text(stringResource(R.string.search_country_label)) },
            placeholder = { Text(stringResource(R.string.search_country_placeholder)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_icon_description)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear_search_icon_description))
                    }
                }
            },
            singleLine = true
        )

        // Results List or Prompts
        if (isSearchActive) {
            if (searchQuery.isBlank()) {
                Text(
                    stringResource(R.string.search_country_label),
                    modifier = modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            } else if (filteredCountries.isEmpty()) {
                Text(
                    stringResource(R.string.no_countries_found),
                    modifier = modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxWidth().weight(1f).padding(8.dp)
                ) {
                    items(filteredCountries, key = { it.isoCode}) { country ->
                        ListItem(
                            headlineContent = { Text(Locale.Builder().setRegion(country.isoCode).build().getDisplayCountry(displayLocale)) },
                            modifier = modifier.clickable {
                                onCountrySelected(country)
                                searchQuery = ""
                                isSearchActive = false
                                keyboardController?.hide()
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    Button(onClick = {
        testFocusRequester.requestFocus()
        localKeyboardController?.show()
        Log.d("KeyboardDebug", "Button clicked, attempting to show keyboard")
    }) {
        Text("Test Keyboard For Simple Field")
    }
    OutlinedTextField(
        value = testText,
        onValueChange = { testText = it },
        label = { Text("Simple Test Field") },
        modifier = Modifier.focusRequester(testFocusRequester)
    )
}

@Composable
fun CountryNumbersView(
    country: CountryEntity, // country is now used for flags, not passed to EmergencyListItem
    emergencyNumbersList: List<EmergencyNumberEntity>,
    displayLocale: Locale // Keep for potential future use if numbers need localization (unlikely for digits)
) {
    val context = LocalContext.current

    @Composable
    fun EmergencyListItem(
        label: String,
        numberEntity: EmergencyNumberEntity?,
        icon: ImageVector,
        itemActionDescription: String // For accessibility or future use
    ) {
        numberEntity?.let { entity ->
            ListItem(
                headlineContent = { Text(label, style = MaterialTheme.typography.titleMedium) },
                trailingContent = {
                    Text(
                        entity.emergencyNumber,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                leadingContent = {
                    Icon(
                        icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent =
                            Intent(Intent.ACTION_DIAL, "tel:${entity.emergencyNumber}.".toUri())
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e(
                                "CountryNumbersView",
                                "Could not open dialer for ${entity.emergencyNumber}",
                                e
                            )
                            // Consider showing a Snackbar or Toast via ViewModel for errors like this
                        }
                    },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent) // Blend with potential Card parent
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }

    // This Column now directly contains the ListItems.
    // If you want a Card around this whole numbers view, add it here.
    // For now, it's designed to be flexible if placed inside a LazyColumn's item.
    Column {
        if (emergencyNumbersList.isEmpty()) {
            Text(
                stringResource(R.string.no_emergency_numbers_found),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            val policeEntity = emergencyNumbersList.firstOrNull { it.serviceId == EmergencyServiceIds.POLICE }
            val ambulanceEntity = emergencyNumbersList.firstOrNull { it.serviceId == EmergencyServiceIds.AMBULANCE }
            val samuEntity = emergencyNumbersList.firstOrNull { it.serviceId == EmergencyServiceIds.SAMU }
            val fireEntity = emergencyNumbersList.firstOrNull { it.serviceId == EmergencyServiceIds.FIRE }
            val general112Entity = emergencyNumbersList.firstOrNull { it.serviceId == EmergencyServiceIds.GENERAL_112 }
            val general911Entity = emergencyNumbersList.firstOrNull { it.serviceId == EmergencyServiceIds.GENERAL_911 }
            val generalDispatchEntity = emergencyNumbersList.firstOrNull { it.serviceId == EmergencyServiceIds.GENERAL_DISPATCH }

            EmergencyListItem(stringResource(R.string.police_label), policeEntity, Icons.Filled.LocalPolice, "Police")
            EmergencyListItem(stringResource(R.string.ambulance_label), ambulanceEntity, Icons.Filled.MedicalServices, "Ambulance")
            if (samuEntity != null && (ambulanceEntity == null || samuEntity.emergencyNumber != ambulanceEntity.emergencyNumber)) {
                EmergencyListItem(stringResource(R.string.samu_label), samuEntity, Icons.Filled.MedicalServices, "SAMU")
            }
            EmergencyListItem(stringResource(R.string.fire_label), fireEntity, Icons.Filled.FireTruck, "Fire Department")

            general112Entity?.let {
                EmergencyListItem(stringResource(R.string.member_112_label), it, Icons.Filled.Public, "112 General Emergency")
            }
            general911Entity?.let {
                EmergencyListItem(stringResource(R.string.member_911_label), it, Icons.Filled.Public, "911 General Emergency")
            }
            generalDispatchEntity?.let {
                // You might need a more specific string resource for "Dispatch" if it's a primary contact
                EmergencyListItem(stringResource(R.string.general_emergency_label), it, Icons.Filled.SupportAgent, "General Dispatch")
            }

            // Fallback for other numbers
            val handledServiceIds = mutableSetOf<Int>()
            listOfNotNull(policeEntity, ambulanceEntity, samuEntity, fireEntity, general112Entity, general911Entity, generalDispatchEntity)
                .forEach { it.serviceId?.let { element -> handledServiceIds.add(element) } }
            // Also explicitly mark base IDs as handled to prevent them from appearing in "Other numbers"
            // if they were specifically sought but no entity was found.
            handledServiceIds.addAll(listOf(
                EmergencyServiceIds.POLICE, EmergencyServiceIds.AMBULANCE, EmergencyServiceIds.SAMU,
                EmergencyServiceIds.FIRE, EmergencyServiceIds.GENERAL_112,
                EmergencyServiceIds.GENERAL_911, EmergencyServiceIds.GENERAL_DISPATCH
            ))

            val otherNumbers = emergencyNumbersList.filterNot { handledServiceIds.contains(it.serviceId) }

            if (otherNumbers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    stringResource(R.string.other_numbers_label),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 0.dp, bottom = 4.dp, top = 8.dp) // No extra indent for this title
                )
                otherNumbers.forEach { numberEntity ->
                    ListItem( // Using a standard ListItem for "other" numbers
                        headlineContent = {
                            Text(
                                numberEntity.notesResKey.takeIf { !it.isNullOrBlank() } // Assuming EmergencyNumberEntity has 'description'
                                    ?: stringResource(R.string.additional_emergency_service_label)
                            )
                        },
                        trailingContent = { Text(numberEntity.emergencyNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge) },
                        leadingContent = { Icon(Icons.Filled.Dialpad, contentDescription = stringResource(R.string.additional_emergency_service_label)) },
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_DIAL, "tel:${numberEntity.emergencyNumber}".toUri())
                            try { context.startActivity(intent) } catch (e: Exception) { Log.e("CountryNumbersView", "Dialer error", e) }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

// Separate Composable for Country Flags (member112, member911)
@Composable
fun CountryFlagsView(country: CountryEntity?) {
    country?.let {
        val showFlags = it.member112 || it.member911
        if (showFlags) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                // stringResource(R.string.country_specific_info_label), // Add this string: "Country Information"
                "Country Information", // Placeholder if string not available
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (it.member112) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(Icons.Filled.CheckCircleOutline, contentDescription = "112 Member", tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.member_112_label), style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (it.member911) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(Icons.Filled.CheckCircleOutline, contentDescription = "911 Member", tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.member_911_label), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
