package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.model.result.EmergencyNumberWithService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmergencyNumberRepository @Inject constructor(
    private val emergencyNumberDao: EmergencyNumberDao
) {

    fun getEmergencyNumbersForCountry(isoCode: String): Flow<List<EmergencyNumberWithService>> {
        return emergencyNumberDao.getEmergencyNumbersForCountry(isoCode)
    }
}