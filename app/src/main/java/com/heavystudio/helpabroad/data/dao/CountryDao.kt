package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

    // --- Convenience Methods ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(vararg countries: CountryEntity)

    @Update
    suspend fun updateCountry(country: CountryEntity): Int

    @Delete
    suspend fun deleteCountry(country: CountryEntity): Int

    // --- Queries ---
    @Query("SELECT * FROM countries WHERE iso_code = :isoCode")
    fun getCountryByIsoCode(isoCode: String): Flow<CountryEntity?>

    @Query("SELECT * FROM countries WHERE member_112 = 1 ORDER BY iso_code ASC")
    fun get112Countries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE member_911 = 1 ORDER BY iso_code ASC")
    fun get911Countries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries ORDER BY iso_code ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Query("DELETE FROM countries WHERE iso_code = :isoCode")
    suspend fun deleteCountryByIsoCode(isoCode: String): Int
}