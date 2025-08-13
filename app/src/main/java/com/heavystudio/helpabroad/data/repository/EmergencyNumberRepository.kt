package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity
import com.heavystudio.helpabroad.utils.catchAndLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyNumberRepository @Inject constructor(
    private val emergencyNumberDao: EmergencyNumberDao
) {

    private val tag = "EmergencyNumberRepository"

    fun getEmergencyNumbersByCountry(isoCode: String): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByCountry(isoCode)
            .catchAndLog(
                tag = tag,
                errorMessage = "Error fetching emergency numbers by country: $isoCode",
                defaultValue = emptyList()
            )
    }

    fun getEmergencyNumbersByService(serviceId: Int): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByService(serviceId)
            .catchAndLog(
                tag = tag,
                errorMessage = "Error fetching emergency numbers by service: $serviceId",
                defaultValue = emptyList()
            )
    }

    fun getEmergencyNumbersByCountryAndService(
        isoCode: String, serviceId: Int
    ): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByCountryAndService(isoCode, serviceId)
            .catchAndLog(
                tag = tag,
                errorMessage = "Error fetching emergency numbers by country ($isoCode) and " +
                        "service ($serviceId)",
                defaultValue = emptyList()
            )
    }

    fun getSmsSupportedEmergencyNumbersByCountry(isoCode: String): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getSmsSupportedEmergencyNumbersByCountry(isoCode)
            .catchAndLog(
                tag = tag,
                errorMessage = "Error fetching SMS supported emergency numbers by country: $isoCode",
                defaultValue = emptyList()
            )
    }
}