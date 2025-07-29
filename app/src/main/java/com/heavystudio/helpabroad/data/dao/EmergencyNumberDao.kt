package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity
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
        "DELETE FROM emergency_numbers " +
                "WHERE country_iso_code = :isoCode AND can_be_deleted = 1"
    )
    suspend fun deleteEmergencyNumbersByCountry(isoCode: String): Int

    @Query("DELETE FROM emergency_numbers WHERE service_id = :serviceId AND can_be_deleted = 1")
    suspend fun deleteEmergencyNumbersByService(serviceId: Int): Int

    @Query("DELETE FROM emergency_numbers WHERE can_be_deleted = 1")
    suspend fun deleteAllEmergencyNumbers(): Int
}