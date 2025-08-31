package com.heavystudio.helpabroad.data.dao

import androidx.room.Query
import androidx.room.Transaction
import com.heavystudio.helpabroad.data.model.result.CountryWithLocalizedName
import kotlinx.coroutines.flow.Flow

interface CountryDao {

    @Transaction
    @Query("""
        SELECT 
            c.*, 
            ct.name AS localizedName 
        FROM countries AS c 
        INNER JOIN country_translations AS ct 
        ON c.iso_code = ct.iso_code 
        WHERE ct.language_code = :languageCode 
        ORDER BY ct.name ASC    
    """)
    fun getCountriesByLanguage(languageCode: String): Flow<List<CountryWithLocalizedName>>

    @Transaction
    @Query("""
        SELECT 
            c.*, 
            ct.name AS localizedName 
        FROM countries AS c 
        INNER JOIN country_translations AS ct
        ON c.iso_code = ct.iso_code 
        INNER JOIN country_translations_fts AS ct_fts 
        ON ct.id = ct_fts.rowid 
        WHERE ct_fts.name MATCH :query 
        AND ct.language_code = :languageCode 
        GROUP BY c.iso_code 
        ORDER BY ct.name ASC
    """)
    fun searchCountries(query: String, languageCode: String): Flow<List<CountryWithLocalizedName>>
}