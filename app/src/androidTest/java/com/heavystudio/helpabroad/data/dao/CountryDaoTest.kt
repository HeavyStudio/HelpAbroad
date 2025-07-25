package com.heavystudio.helpabroad.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heavystudio.helpabroad.data.database.AppDatabase
import com.heavystudio.helpabroad.data.database.CountryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CountryDaoTest {
    private lateinit var countryDao: CountryDao
    private lateinit var db: AppDatabase

    private val country1 = CountryEntity("AA", "Country 1", "EMO", "+1", "112")
    private val country2 = CountryEntity("BB", "Country 2", "EMO", "+2", "112")
    private val country3 = CountryEntity("CC", "Country 3", "EMO", "+3", "112")

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        countryDao = db.countryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertCountryAndGetByIsoCode() = runBlocking {
        countryDao.insertCountry(country1)

        val retrievedCountry = countryDao.getCountryByIsoCode(country1.isoCode)
        assertNotNull("Retrieved country should not be null", retrievedCountry)
        assertEquals("ID", country1.isoCode, retrievedCountry!!.isoCode)
        assertEquals("nameResKey", country1.name, retrievedCountry.name)
        assertEquals("customName", country1.dialCode, retrievedCountry.dialCode)
        assertEquals("isPredefined", country1.regionalDispatch, retrievedCountry.regionalDispatch)
    }

    @Test
    @Throws(Exception::class)
    fun getCountry_nonExistentIsoCode_returnsNull() = runBlocking {
        val retrievedCountry = countryDao.getCountryByIsoCode("YU")
        assertNull("Country with non-existent ISO code should be null", retrievedCountry)
    }

    @Test
    @Throws(Exception::class)
    fun getAllCountries_multipleCountries_returnsAll() = runBlocking {
        countryDao.insertCountry(country1)
        countryDao.insertCountry(country2)
        countryDao.insertCountry(country3)

        val allCountries = countryDao.getAllCountries().first()
        assertEquals("Should be 3 categories", 3, allCountries.size)
        assertEquals("Country 1", country1, allCountries[0])
        assertEquals("Country 2", country2, allCountries[1])
        assertEquals("Country 3", country3, allCountries[2])
    }

    @Test
    @Throws(Exception::class)
    fun getAllCountries_emptyDatabase_returnsEmptyList() = runBlocking {
        val allCountries = countryDao.getAllCountries().first()
        assertTrue("Country list should be empty", allCountries.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun updateCategory_changesExistingCategory() = runBlocking {
        countryDao.insertCountry(country1)
        val updatedCountry = CountryEntity("XX", "Updated Country", "TeR", "41", "911")
        countryDao.updateCountry(updatedCountry)

        val retrievedCountry = countryDao.getCountryByIsoCode("XX")
        assertNotNull(retrievedCountry)
        assertEquals("name should be updated", "Updated Country", retrievedCountry!!.name)
        assertEquals("flagEmoji should be updated", "TeR", retrievedCountry.flagEmoji)
        assertEquals("dialCode should be updated", "41", retrievedCountry.dialCode)
        assertEquals("regionalDispatch should be updated", "911", retrievedCountry.regionalDispatch)
    }

    @Test
    @Throws(Exception::class)
    fun updateCategory_nonExistent_doesNothingOrError() = runBlocking {
        countryDao.updateCountry(country1)

        val retrievedCountry = countryDao.getCountryByIsoCode("AA")
        assertNull("Country should still be null as update shouldn't insert", retrievedCountry)

        val allCountries = countryDao.getAllCountries().first()
        assertTrue("Database should remain empty or unchanged in count", allCountries.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun deleteCountry_removesCategoryFromDatabase() = runBlocking {
        countryDao.insertCountry(country1)
        countryDao.deleteCountry(country1)

        val retrievedCategory = countryDao.getCountryByIsoCode("AA")
        assertNull("Deleted category should be null", retrievedCategory)

        val allCountries = countryDao.getAllCountries().first()
        assertTrue("Database should be empty or have one less item", allCountries.none { it.isoCode == "XX" })
    }

    @Test
    @Throws(Exception::class)
    fun deleteCountry_nonExistent_doesNothing() = runBlocking {
        countryDao.insertCountry(country1)
        countryDao.deleteCountry(country2)

        val allCategories = countryDao.getAllCountries().first()
        assertEquals("Count should remain 1", 1, allCategories.size)
        assertNotNull("Other category should still exist", countryDao.getCountryByIsoCode(country1.isoCode))
    }
}