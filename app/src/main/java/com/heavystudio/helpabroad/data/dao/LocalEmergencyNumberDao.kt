package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.LocalEmergencyNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalEmergencyNumberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalEmergencyNumbers(localNumbers: List<LocalEmergencyNumberEntity>)

    @Update
    suspend fun updateLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity)

    @Delete
    suspend fun deleteLocalEmergencyNumber(localNumber: LocalEmergencyNumberEntity)

    /**
     * Retrieves a list of local emergency numbers for a specific country and service.
     *
     * @param countryIsoCode The ISO code of the country.
     * @param serviceId The ID of the emergency service.
     * @return A Flow emitting a list of [LocalEmergencyNumberEntity] objects matching the criteria,
     *         ordered by the emergency number in ascending order.
     */
    @Query("""
        SELECT * FROM len_local_emergency_numbers 
        WHERE len_country_iso_code = :countryIsoCode 
        AND len_service_id = :serviceId 
        ORDER BY len_number ASC
    """)
    fun getNumbersForCountryAndService(countryIsoCode: String, serviceId: Int): Flow<List<LocalEmergencyNumberEntity>>

    /**
     * Retrieves a list of local emergency numbers for a specific country.
     *
     * @param countryIsoCode The ISO code of the country for which to retrieve emergency numbers.
     * @return A Flow emitting a list of [LocalEmergencyNumberEntity] objects.
     */
    @Query("SELECT * FROM len_local_emergency_numbers WHERE len_country_iso_code = :countryIsoCode")
    fun getNumbersForCountry(countryIsoCode: String): Flow<List<LocalEmergencyNumberEntity>>

    /**
     * Deletes all local emergency numbers associated with a specific country.
     *
     * @param countryIsoCode The ISO code of the country for which to delete the emergency numbers.
     */
    @Query("DELETE FROM len_local_emergency_numbers WHERE len_country_iso_code = :countryIsoCode")
    suspend fun deleteNumbersForCountry(countryIsoCode: String)

    /**
     * Deletes all local emergency numbers associated with a specific service ID.
     *
     * @param serviceId The ID of the service for which to delete emergency numbers.
     */
    @Query("DELETE FROM len_local_emergency_numbers WHERE len_service_id = :serviceId")
    suspend fun deleteNumbersForService(serviceId: Int)

    /**
     * Deletes all local emergency numbers from the database.
     */
    @Query("DELETE FROM len_local_emergency_numbers")
    suspend fun deleteAllLocalEmergencyNumbers()
}