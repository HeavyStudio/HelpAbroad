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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Update
    suspend fun updateCountry(country: CountryEntity)

    @Delete
    suspend fun deleteCountry(country: CountryEntity)

    /**
     * Retrieves a country from the database by its ISO code.
     *
     * @param countryIsoCode The ISO code of the country to retrieve.
     * @return The [CountryEntity] if found, or null otherwise.
     */
    @Query("SELECT * FROM cnt_countries WHERE cnt_iso_code = :countryIsoCode")
    suspend fun getCountryByIsoCode(countryIsoCode: String): CountryEntity?

    /**
     * Retrieves all countries from the database, ordered alphabetically by name.
     *
     * @return A Flow emitting a list of [CountryEntity] objects.
     */
    @Query("SELECT * FROM cnt_countries ORDER BY cnt_name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    /**
     * Deletes a country from the database by its ISO code.
     *
     * @param countryIsoCode The ISO code of the country to delete.
     */
    @Query("DELETE FROM cnt_countries WHERE cnt_iso_code = :countryIsoCode")
    suspend fun deleteCountryByIsoCode(countryIsoCode: String)

    /**
     * Deletes all countries from the cnt_countries table.
     */
    @Query("DELETE FROM cnt_countries")
    suspend fun deleteAllCountries()
}