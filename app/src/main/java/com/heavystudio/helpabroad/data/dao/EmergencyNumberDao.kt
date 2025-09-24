package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.heavystudio.helpabroad.data.model.result.EmergencyNumberWithService
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyNumberDao {

    /**
     * Retrieves a list of emergency numbers and their associated service details for a specific
     * country, identified by its ISO 3166-1 alpha-2 code.
     *
     * This function performs a JOIN operation between the `emergency_numbers` table and the
     * `services` table to combine the phone number with the service's name, notes, and icon resource
     * keys. The results are wrapped in a [Flow] to allow for reactive updates if the underlying
     * data changes.
     *
     * @param isoCode The ISO 3166-1 alpha-2 country code (e.g., "US", "DE") for which to fetch
     *                emergency numbers.
     * @return A [Flow] emitting a list of [EmergencyNumberWithService] objects for the specified
     *         country.
     */
    @Transaction
    @Query("""
        SELECT 
            en.number, 
            s.name_res_key AS serviceNameResKey, 
            s.notes_res_key AS serviceNotesResKey,
            s.icon_res_key AS iconResKey 
        FROM emergency_numbers AS en 
        INNER JOIN services AS s 
            ON en.service_id = s.id 
        WHERE en.iso_code = :isoCode    
    """)
    fun getEmergencyNumbersForCountry(isoCode: String): Flow<List<EmergencyNumberWithService>>
}