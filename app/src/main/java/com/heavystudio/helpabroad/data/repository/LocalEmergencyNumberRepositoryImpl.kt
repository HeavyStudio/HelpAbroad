package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.LocalEmergencyNumberDao
import com.heavystudio.helpabroad.data.database.LocalEmergencyNumberEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalEmergencyNumberRepositoryImpl @Inject constructor(
    private val localEmergencyNumberDao: LocalEmergencyNumberDao
) : LocalEmergencyNumberRepository {

    override suspend fun insertLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity): Long {
        return localEmergencyNumberDao.insertLocalEmergencyNumber(localNumber)
    }

    override suspend fun insertLocalEmergencyNumbers(localNumbers: List<LocalEmergencyNumberEntity>) {
        return localEmergencyNumberDao.insertLocalEmergencyNumbers(localNumbers)
    }

    override fun getNumbersForCountryAndService(
        countryIsoCode: String,
        serviceId: Int
    ): Flow<List<LocalEmergencyNumberEntity>> {
        return localEmergencyNumberDao.getNumbersForCountryAndService(countryIsoCode, serviceId)
    }

    override fun getNumbersForCountry(countryIsoCode: String): Flow<List<LocalEmergencyNumberEntity>> {
        return localEmergencyNumberDao.getNumbersForCountry(countryIsoCode)
    }

    override suspend fun updateLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity) {
        return localEmergencyNumberDao.updateLocalEmergencyNumber(localNumber)
    }

    override suspend fun deleteLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity) {
        return localEmergencyNumberDao.deleteLocalEmergencyNumber(localNumber)
    }

    override suspend fun deleteNumbersForCountry(countryIsoCode: String) {
        return localEmergencyNumberDao.deleteNumbersForCountry(countryIsoCode)
    }

    override suspend fun deleteNumbersForService(serviceId: Int) {
        return localEmergencyNumberDao.deleteNumbersForService(serviceId)
    }

    override suspend fun deleteNumbersForCountryAndService(countryIsoCode: String, serviceId: Int) {
        return localEmergencyNumberDao.deleteNumbersForCountryAndService(countryIsoCode, serviceId)
    }

    override suspend fun deleteAllLocalEmergencyNumbers() {
        return localEmergencyNumberDao.deleteAllLocalEmergencyNumbers()
    }
}