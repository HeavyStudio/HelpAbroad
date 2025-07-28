package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.database.CountryEntity
import kotlinx.coroutines.flow.Flow

interface CountryRepository {

    /**
     * Inserts a country into the data source.
     *
     * @param country The country to be inserted.
     * @return The row ID of the newly inserted country.
     */
    suspend fun insertCountry(country: CountryEntity): Long

    /**
     * Retrieves a country by its ISO code.
     *
     * @param countryIsoCode The ISO code of the country to retrieve.
     * @return The [CountryEntity] if found, or null otherwise.
     */
    suspend fun getCountryByIsoCode(countryIsoCode: String): CountryEntity?
    /**
     * Retrieves all countries from the data source.
     *
     * @return A [Flow] emitting a list of [CountryEntity] objects.
     */
    fun getAllCountries(): Flow<List<CountryEntity>>

    /**
     * Updates an existing country in the data source.
     *
     * @param country The [CountryEntity] object with updated information.
     */
    suspend fun updateCountry(country: CountryEntity)

    /**
     * Deletes a country from the database by its ISO code.
     *
     * @param countryIsoCode The ISO code of the country to delete.
     */
    suspend fun deleteCountryByIsoCode(countryIsoCode: String)
    /**
     * Deletes all countries from the local data source.
     */
    suspend fun deleteAllCountries()

}