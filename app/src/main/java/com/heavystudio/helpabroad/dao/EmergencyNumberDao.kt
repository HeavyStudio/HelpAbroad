package com.heavystudio.helpabroad.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heavystudio.helpabroad.data.Country
import com.heavystudio.helpabroad.data.EmergencyNumber
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyNumberDao {

    // --- Country Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<Country>)

    @Query("SELECT * FROM countries ORDER BY countryName ASC")
    fun getAllCountries(): Flow<List<Country>>

    @Query("SELECT * FROM countries WHERE isDownloaded = 1 ORDER BY countryName ASC")
    fun getDownloadedCountries(): Flow<List<Country>>

    @Query("UPDATE countries SET isDownloaded = :isDownloaded WHERE countryCode = :countryCode")
    suspend fun updateCountryDownloadedStatus(countryCode: String, isDownloaded: Boolean)

    @Query("SELECT countryName FROM countries WHERE countryCode = :countryCode")
    suspend fun getCountryNameByCode(countryCode: String): String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCountry(country: Country)

    @Query("SELECT COUNT(*) FROM countries WHERE countryCode = :countryCode")
    suspend fun countCountry(countryCode: String): Int

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun countAllCountries(): Int

    // --- EmergencyNumber Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyNumbers(numbers: List<EmergencyNumber>)

    @Query("SELECT * FROM emergency_numbers WHERE countryCode = :countryCode ORDER BY serviceName ASC")
    fun getEmergencyNumbersByCountryCode(countryCode: String): Flow<List<EmergencyNumber>>

    @Query("DELETE FROM emergency_numbers WHERE countryCode = :countryCode")
    suspend fun deleteEmergencyNumbersByCountryCode(countryCode: String)

    @Query("SELECT COUNT(*) FROM emergency_numbers WHERE countryCode = :countryCode")
    suspend fun countEmergencyNumbersForCountry(countryCode: String): Int

    // --- Combined Operations ---
    // To manage downloading/deleting a country and its numbers

    @Transaction
    suspend fun downloadCountryData(country: Country, numbers: List<EmergencyNumber>) {
        insertEmergencyNumbers(numbers)
        updateCountryDownloadedStatus(country.countryCode, true)
    }

    @Transaction
    suspend fun deleteCountryDataAndNumbers(countryCode: String) {
        deleteEmergencyNumbersByCountryCode(countryCode)
        updateCountryDownloadedStatus(countryCode, false)
    }
}