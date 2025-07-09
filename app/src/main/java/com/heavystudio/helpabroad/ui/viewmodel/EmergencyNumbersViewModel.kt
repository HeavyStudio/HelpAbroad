package com.heavystudio.helpabroad.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heavystudio.helpabroad.data.repository.EmergencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmergencyNumbersUiState(
    val isLoading: Boolean = true,
    val emergencyNumbers: List<String> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class EmergencyNumbersViewModel @Inject constructor(
    private val emergencyRepository: EmergencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyNumbersUiState())
    val uiState: StateFlow<EmergencyNumbersUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized. Fetching emergency numbers.")
        fetchEmergencyNumbers()
    }

    // Optional: Allow the UI to trigger a refresh
    fun refreshEmergencyNumbers() {
        Log.d(TAG, "Refresh triggered.")
        fetchEmergencyNumbers()
    }

    private fun fetchEmergencyNumbers() {
        viewModelScope.launch {
            // Set loading state to true before starting the fetch
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            emergencyRepository.getSystemEmergencyNumbers()
                .catch { exception ->
                    Log.e(TAG, "Error collecting emergency numbers flow", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load emergency numbers: ${exception.localizedMessage}"
                    )
                }
                .collect { numbers ->
                    Log.d(TAG, "Received emergency numbers: $numbers")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emergencyNumbers = numbers,
                        errorMessage = null
                    )
                }
        }
    }

    companion object {
        private const val TAG = "EmergencyNumbersVM"
    }

}
