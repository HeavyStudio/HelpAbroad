package com.heavystudio.helpabroad.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.heavystudio.helpabroad.data.local.dto.CountryDetails
import com.heavystudio.helpabroad.data.local.dto.CountryListItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the country-related tables in the Room database.
 *
 * This interface provides methods for accessing and querying country information,
 * such as searching for countries, retrieving a list of all countries, and fetching
 * detailed information for a specific country. All methods return a [Flow] to allow
 * for reactive data observation.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the interface.
 */
@Dao
interface CountryDao {

    /**
     * Searches for countries based on a query string and language code.
     *
     * This function performs a full-text search on the `country_names_fts` table
     * for country names matching the provided `query`. It then joins the results
     * with the `countries` and `country_names` tables to retrieve the country ID,
     * ISO code, and localized name.
     *
     * The search is language-specific, using the `langCode` to filter country names.
     * The results are ordered alphabetically by the localized country name.
     *
     * @param query The search query string. The function will append a '*' to perform a prefix search.
     * @param langCode The language code (e.g., "en", "es") to filter country names by.
     * @return A [Flow] emitting a list of [CountryListItem] objects that match the search criteria.
     */
    @Query("""
        SELECT DISTINCT c.id AS countryId, c.iso_code AS isoCode, cn_display.name AS name
        FROM country_names_fts AS fts
        JOIN country_names AS cn_match ON fts.rowid = cn_match.rowid
        JOIN countries AS c ON cn_match.country_id = c.id
        JOIN country_names AS cn_display ON c.id = cn_display.country_id
        WHERE fts.name MATCH :query || '*'
        AND cn_display.language_code = :langCode
        ORDER BY cn_display.name ASC
    """)
    fun searchCountries(query: String, langCode: String): Flow<List<CountryListItem>>

    /**
     * Retrieves a list of all countries with their names localized to the specified language code.
     *
     * This function queries the database to get the ID, ISO code, and localized name of each country.
     * It joins the `countries` table with the `country_names` table to fetch the names
     * based on the provided `langCode`. The results are ordered alphabetically by the country name.
     *
     * @param langCode The language code (e.g., "en", "es") to fetch the country names in.
     * @return A [Flow] emitting a list of [CountryListItem] objects, where each item
     *         represents a country with its ID, ISO code, and localized name.
     */
    @Query("""
        SELECT c.id AS countryId, c.iso_code AS isoCode, cn.name AS name 
        FROM countries AS c 
        INNER JOIN country_names AS cn ON c.id = cn.country_id 
        WHERE cn.language_code = :langCode 
        ORDER BY cn.name ASC
    """)
    fun getAllCountries(langCode: String): Flow<List<CountryListItem>>

    /**
     * Retrieves the details of a specific country by its ID.
     *
     * This function queries the `countries` table to fetch all columns for the
     * country that matches the given `countryId`.
     *
     * @param countryId The unique identifier of the country to retrieve details for.
     * @return A [Flow] emitting a [CountryDetails] object if a country with the specified ID is found,
     *         or `null` if no such country exists. The operation is performed within a transaction.
     */
    @Transaction
    @Query("SELECT * FROM countries WHERE id = :countryId")
    fun getCountryDetails(countryId: Int): Flow<CountryDetails?>

    /**
     * Retrieves a list of countries by their IDs, with names localized to the specified language.
     *
     * This function queries the database to get the ID, ISO code, and localized name for each
     * country ID provided in the `countryIds` list. It joins the `countries` table with the
     * `country_names` table to fetch the names based on the provided `langCode`. The results
     * are ordered alphabetically by the country name.
     *
     * This is useful for fetching a specific subset of countries, for example, the user's
     * favorite or recently visited countries.
     *
     * @param countryIds A list of unique identifiers for the countries to retrieve.
     * @param langCode The language code (e.g., "en", "es") for localizing the country names.
     * @return A [Flow] emitting a list of [CountryListItem] objects for the specified IDs.
     */
    @Query("""
        SELECT c.id AS countryId, c.iso_code AS isoCode, cn.name AS name 
        FROM countries AS c 
        INNER JOIN country_names AS cn ON c.id = cn.country_id 
        WHERE cn.language_code = :langCode AND c.id IN (:countryIds)
    """)
    fun getCountriesByIds(countryIds: List<Int>, langCode: String): Flow<List<CountryListItem>>
}