package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.PersonalEmergencyNumberDao
import com.heavystudio.helpabroad.data.database.PersonalEmergencyNumberEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonalEmergencyNumberRepositoryImpl @Inject constructor(
    private val personalEmergencyNumberDao: PersonalEmergencyNumberDao
) : PersonalEmergencyNumberRepository {

    override suspend fun insertPersonalNumber(personalNumber: PersonalEmergencyNumberEntity): Long {
        return personalEmergencyNumberDao.insertPersonalNumber(personalNumber)
    }

    override suspend fun getPersonalNumberById(penId: Int): PersonalEmergencyNumberEntity? {
        return personalEmergencyNumberDao.getPersonalNumberById(penId)
    }

    override fun getPersonalNumbersByCountry(countryIsoCode: String): Flow<List<PersonalEmergencyNumberEntity>> {
        return personalEmergencyNumberDao.getPersonalNumbersByCountry(countryIsoCode)
    }

    override fun getPersonalNumbersByCategory(categoryId: Int): Flow<List<PersonalEmergencyNumberEntity>> {
        return personalEmergencyNumberDao.getPersonalNumbersByCategory(categoryId)
    }

    override fun getUncategorizedPersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>> {
        return personalEmergencyNumberDao.getUncategorizedPersonalNumbers()
    }

    override fun getFavoritePersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>> {
        return personalEmergencyNumberDao.getFavoritePersonalNumbers()
    }

    override fun getAllPersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>> {
        return personalEmergencyNumberDao.getAllPersonalNumbers()
    }

    override suspend fun getPersonalNumbersCount(): Int {
        return personalEmergencyNumberDao.getPersonalNumbersCount()
    }

    override suspend fun getFavoritePersonalNumbersCount(): Int {
        return personalEmergencyNumberDao.getFavoritePersonalNumbersCount()
    }

    override suspend fun updatePersonalNumber(personalNumber: PersonalEmergencyNumberEntity) {
        return personalEmergencyNumberDao.updatePersonalNumber(personalNumber)
    }

    override suspend fun setFavoriteStatus(penId: Int, isFavorite: Boolean) {
        return personalEmergencyNumberDao.setFavoriteStatus(penId, isFavorite)
    }

    override suspend fun deletePersonalNumber(personalNumber: PersonalEmergencyNumberEntity) {
        return personalEmergencyNumberDao.deletePersonalNumber(personalNumber)
    }

    override suspend fun deletePersonalNumberById(penId: Int) {
        return personalEmergencyNumberDao.deletePersonalNumberById(penId)
    }

    override suspend fun deleteAllPersonalNumbers() {
        return personalEmergencyNumberDao.deleteAllPersonalNumbers()
    }
}