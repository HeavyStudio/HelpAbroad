package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.heavystudio.helpabroad.data.model.result.CountryWithLocalizedName
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

    /**
     * Retrieves a list of all countries with their names localized to the specified language.
     * The results are ordered alphabetically by the localized country name.
     *
     * This function returns a [Flow] that will automatically emit a new list of countries
     * whenever the underlying data in the `countries` or `country_translations` tables changes.
     *
     * @param languageCode The ISO 639-1 code of the language for the country names (e.g., "en", "es").
     * @return A [Flow] emitting a list of [CountryWithLocalizedName] objects.
     */
    @Transaction
    @Query("""
        SELECT 
            c.iso_code, 
            c.flag, 
            c.last_updated,
            c.region_id,
            ct.name AS localizedName,
            r.name_res_key AS regionName 
        FROM countries AS c 
        INNER JOIN country_translations AS ct 
            ON c.iso_code = ct.iso_code 
        INNER JOIN regions AS r 
            ON c.region_id = r.id 
        WHERE ct.language_code = :languageCode 
        ORDER BY ct.name ASC    
    """)
    fun getCountriesByLanguage(languageCode: String): Flow<List<CountryWithLocalizedName>>

    /**
     * Searches for countries using a full-text search (FTS) query on their localized names.
     *
     * This function performs a search against the `country_translations_fts` table, which is an FTS5
     * virtual table for fast text searching. It joins the results with the `countries` and
     * `country_translations` tables to retrieve the country's ISO code, flag, and the matching
     * localized name for a specific language.
     *
     * The results are grouped by the country's ISO code to avoid duplicates and are ordered
     * alphabetically by the localized country name.
     *
     * The query should be a valid FTS5 match expression. For a simple prefix search,
     * append a `*` to the query string (e.g., "United*").
     *
     * @param query The FTS match query string to search for in country names.
     * @param languageCode The ISO 639-1 code of the language to search within (e.g., "en", "es").
     * @return A [Flow] emitting a list of matching [CountryWithLocalizedName] objects.
     */
    @Transaction
    @Query("""
        SELECT 
            c.iso_code,
            c.flag, 
            c.last_updated, 
            c.region_id, 
            ct.name AS localizedName,
            r.name_res_key as regionName  
        FROM countries AS c 
        INNER JOIN country_translations AS ct
        ON c.iso_code = ct.iso_code 
        INNER JOIN country_translations_fts AS ct_fts 
        ON ct.id = ct_fts.rowid 
        INNER JOIN regions AS r 
        ON c.region_id = r.id 
        WHERE ct_fts.name MATCH :query 
        AND ct.language_code = :languageCode 
        GROUP BY c.iso_code 
        ORDER BY ct.name ASC
    """)
    fun searchCountries(query: String, languageCode: String): Flow<List<CountryWithLocalizedName>>
}