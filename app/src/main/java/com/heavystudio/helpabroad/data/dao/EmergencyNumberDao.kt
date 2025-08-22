package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity
import com.heavystudio.helpabroad.ui.home.DisplayableEmergencyNumber
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyNumberDao {

    // --- Convenience Methods ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEmergencyNumber(emergencyNumber: EmergencyNumberEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEmergencyNumbers(vararg emergencyNumbers: EmergencyNumberEntity)

    @Update
    suspend fun updateEmergencyNumber(emergencyNumber: EmergencyNumberEntity): Int

    @Delete
    suspend fun deleteEmergencyNumber(emergencyNumber: EmergencyNumberEntity): Int

    // --- Queries ---
    @Query(
        "SELECT * FROM emergency_numbers " +
                "WHERE country_iso_code = :isoCode " +
                "ORDER BY emergency_number ASC"
    )
    fun getEmergencyNumbersByCountry(isoCode: String): Flow<List<EmergencyNumberEntity>>

    @Query(
        "SELECT * FROM emergency_numbers " +
                "WHERE service_id = :serviceId " +
                "ORDER BY emergency_number ASC"
    )
    fun getEmergencyNumbersByService(serviceId: Int): Flow<List<EmergencyNumberEntity>>

    @Query(
        "SELECT * FROM emergency_numbers " +
                "WHERE country_iso_code = :isoCode AND service_id = :serviceId " +
                "ORDER BY emergency_number ASC"
    )
    fun getEmergencyNumbersByCountryAndService(isoCode: String, serviceId: Int):
            Flow<List<EmergencyNumberEntity>>

    @Query(
        "SELECT * FROM emergency_numbers " +
                "WHERE country_iso_code = :isoCode AND supports_sms = 1 " +
                "ORDER BY emergency_number ASC"
    )
    fun getSmsSupportedEmergencyNumbersByCountry(isoCode: String): Flow<List<EmergencyNumberEntity>>

    @Query("""
        SELECT 
            s.name_res_key AS serviceNameResKey, 
            en.emergency_number AS number
        FROM emergency_numbers en 
        INNER JOIN services s ON en.service_id = s.id 
        WHERE en.country_iso_code = :countryIsoCode 
        ORDER BY s.name_res_key ASC
    """)
    fun getDisplayableEmergencyNumbersForCountry(countryIsoCode: String): Flow<List<DisplayableEmergencyNumber>>
}