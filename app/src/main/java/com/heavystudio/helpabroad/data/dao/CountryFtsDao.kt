package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.CountryEntity
import com.heavystudio.helpabroad.data.database.CountryFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryFtsDao {

    // --- Convenience Methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountryName(name: CountryFtsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountryNames(vararg names: CountryFtsEntity)

    @Update
    suspend fun updateCountryName(name: CountryFtsEntity): Int

    @Delete
    suspend fun deleteCountryName(name: CountryFtsEntity): Int

    // --- Queries ---
    @Query("""
        SELECT DISTINCT C.* 
        FROM countries C 
        JOIN countries_fts C_FTS 
        ON C.iso_code = C_FTS.country_iso_code 
        WHERE C_FTS.searchable_name MATCH :query AND C_FTS.language_id = :currentLang 
        ORDER BY C.name ASC
    """)
    fun searchCountries(query: String, currentLang: String): Flow<List<CountryEntity>>

    @Query("DELETE FROM countries_fts WHERE country_iso_code = :isoCode")
    suspend fun deleteCountryNameByIsoCode(isoCode: String): Int

    @Query("DELETE FROM countries_fts")
    suspend fun deleteAllCountryNames(): Int
}