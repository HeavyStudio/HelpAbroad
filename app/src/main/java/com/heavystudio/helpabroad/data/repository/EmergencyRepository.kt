package com.heavystudio.helpabroad.data.repository

import kotlinx.coroutines.flow.Flow

interface EmergencyRepository {
    fun getSystemEmergencyNumbers(): Flow<List<String>>
}