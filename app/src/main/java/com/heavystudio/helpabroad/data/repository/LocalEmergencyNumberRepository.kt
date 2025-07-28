package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.database.LocalEmergencyNumberEntity
import kotlinx.coroutines.flow.Flow

interface LocalEmergencyNumberRepository {

    /**
     * Inserts a new local emergency number into the data source.
     *
     * @param localNumber The [LocalEmergencyNumberEntity] to be inserted.
     * @return The row ID of the newly inserted local emergency number.
     */
    suspend fun insertLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity): Long

    /**
     * Inserts a list of local emergency numbers into the local data source.
     *
     * @param localNumbers A list of [LocalEmergencyNumberEntity] objects to be inserted.
     */
    suspend fun insertLocalEmergencyNumbers(localNumbers: List<LocalEmergencyNumberEntity>)

    /**
     * Retrieves a list of local emergency numbers for a specific country and service.
     *
     * @param countryIsoCode The ISO code of the country.
     * @param serviceId The ID of the emergency service.
     * @return A Flow emitting a list of [LocalEmergencyNumberEntity] objects.
     */
    fun getNumbersForCountryAndService(countryIsoCode: String, serviceId: Int): Flow<List<LocalEmergencyNumberEntity>>

    /**
     * Retrieves a list of local emergency numbers for a specific country.
     *
     * @param countryIsoCode The ISO code of the country.
     * @return A Flow emitting a list of [LocalEmergencyNumberEntity] objects.
     */
    fun getNumbersForCountry(countryIsoCode: String): Flow<List<LocalEmergencyNumberEntity>>

    /**
     * Updates an existing local emergency number in the data source.
     *
     * @param localNumber The [LocalEmergencyNumberEntity] to be updated.
     */
    suspend fun updateLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity)

    /**
     * Deletes a specific local emergency number from the data source.
     *
     * @param localNumber The [LocalEmergencyNumberEntity] to be deleted.
     */
    suspend fun deleteLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity)

    /**
     * Deletes all local emergency numbers associated with a specific country from the data source.
     *
     * @param countryIsoCode The ISO code of the country for which to delete emergency numbers.
     */
    suspend fun deleteNumbersForCountry(countryIsoCode: String)

    /**
     * Deletes all local emergency numbers associated with a specific service from the data source.
     *
     * @param serviceId The ID of the service for which to delete emergency numbers.
     */
    suspend fun deleteNumbersForService(serviceId: Int)

    /**
     * Deletes all local emergency numbers associated with a specific country and service.
     *
     * @param countryIsoCode The ISO code of the country.
     * @param serviceId The ID of the emergency service.
     */
    suspend fun deleteNumbersForCountryAndService(countryIsoCode: String, serviceId: Int)

    /**
     * Deletes all local emergency numbers from the data source.
     */
    suspend fun deleteAllLocalEmergencyNumbers()

}