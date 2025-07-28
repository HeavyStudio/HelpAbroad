package com.heavystudio.helpabroad.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heavystudio.helpabroad.data.database.AppDatabase
import com.heavystudio.helpabroad.data.database.LocalEmergencyNumberEntity
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
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class LocalEmergencyNumberDaoTest {
    private lateinit var localEmergencyNumberDao: LocalEmergencyNumberDao
    private lateinit var db: AppDatabase

    private val sampleNumber1 = LocalEmergencyNumberEntity(1, "FR", "17", 1)
    private val sampleNumber2 = LocalEmergencyNumberEntity(2, "FR", "15", 2)
    private val sampleNumber3 = LocalEmergencyNumberEntity(3, "FR", "18", 3)
    private val sampleNumber4 = LocalEmergencyNumberEntity(4, "FR", "911", 1)
    private val sampleNumber5 = LocalEmergencyNumberEntity(5, "US", "911", 1)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        localEmergencyNumberDao = db.localEmergencyNumberDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertLocalEmergencyNumber_single_insertsCorrectly() = runBlocking {
        val insertedId = localEmergencyNumberDao.insertLocalEmergencyNumber(sampleNumber1)
        val retrieved = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 1).first()
        assertTrue("Retrieved list should not be empty", retrieved.isNotEmpty())
        assertEquals("Inserted number", sampleNumber1, retrieved.find { it.id == sampleNumber1.id })
    }

    @Test
    @Throws(Exception::class)
    fun insertLocalEmergencyNumbers_multiple_insertsCorrectly() = runBlocking {
        val numbersToInsert = listOf(sampleNumber1, sampleNumber2)
        localEmergencyNumberDao.insertLocalEmergencyNumbers(numbersToInsert)

        val retrievedFor17 = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 1).first()
        val retrievedFor15 = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 2).first()

        assertEquals("Count for 17 should be 1", 1, retrievedFor17.size)
        assertTrue("17 numbers should contain sampleNumber1", retrievedFor17.contains(sampleNumber1))

        assertEquals("Count for 15 should be 1", 1, retrievedFor15.size)
        assertTrue("15 numbers should contain sampleNumber2", retrievedFor15.contains(sampleNumber2))
    }

    @Test
    @Throws(Exception::class)
    fun updateLocalEmergencyNumber_updatesExistingNumber() = runBlocking {
        localEmergencyNumberDao.insertLocalEmergencyNumber(sampleNumber1)
        val updatedNumber = sampleNumber1.copy(countryIsoCode = "US", number = "911")
        localEmergencyNumberDao.updateLocalEmergencyNumber(updatedNumber)

        val retrieved = localEmergencyNumberDao.getNumbersForCountryAndService("US", 1).first()
        val found = retrieved.find { it.id == sampleNumber1.id }
        assertNotNull("Updated number not found", found)
        assertEquals("Updated country code", "US", found!!.countryIsoCode)
        assertEquals("Updated number", "911", found.number)
        assertEquals("Service ID not updated", 1, found.serviceId)
    }

    @Test
    @Throws(Exception::class)
    fun updateLocalEmergencyNumber_nonExistent_doesNothing() = runBlocking {
        val nonExistent = sampleNumber3
        localEmergencyNumberDao.updateLocalEmergencyNumber(nonExistent)

        val allNumbers = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 3).first()
        assertTrue("Database should remain empty", allNumbers.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun getNumbersForCountryAndService_returnsCorrectOrderedList() = runBlocking {
        val numbers = listOf(sampleNumber1, sampleNumber4)
        localEmergencyNumberDao.insertLocalEmergencyNumbers(numbers)

        val result = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 1).first()
        assertEquals("Should retrieve 2 numbers for FR service 1", 2, result.size)
        assertEquals("First number in order (17)", sampleNumber1, result[0])
        assertEquals("Second number in order (911)", sampleNumber4, result[1])
    }

    @Test
    @Throws(Exception::class)
    fun getNumbersForCountryAndService_nonExistent_returnsEmptyList() = runBlocking {
        val result = localEmergencyNumberDao.getNumbersForCountryAndService("XX", 404).first()
        assertTrue("Should return empty list for non-existent country/service", result.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun getNumbersForCountry_returnsCorrectList() = runBlocking {
        val numbers = listOf(sampleNumber1, sampleNumber2, sampleNumber3)
        localEmergencyNumberDao.insertLocalEmergencyNumbers(numbers)

        val resultFR = localEmergencyNumberDao.getNumbersForCountry("FR").first()
        assertEquals("Should retrieve 3 numbers for FR", 3, resultFR.size)
        assertTrue(resultFR.containsAll(numbers))

        val resultUS = localEmergencyNumberDao.getNumbersForCountry("US").first()
        assertEquals("Should retrieve 1 number for US", 1, resultUS.size)
        assertTrue(resultUS.contains(sampleNumber5))
    }

    @Test
    @Throws(Exception::class)
    fun getNumbersForCountry_nonExistent_returnsEmptyList() = runBlocking {
        val result = localEmergencyNumberDao.getNumbersForCountry("ES").first()
        assertTrue("Should return empty list for non-existent country", result.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun deleteNumbersForCountry_deletesAllForSpecificCountry() = runBlocking {
        val numbers = listOf(sampleNumber1, sampleNumber2, sampleNumber3, sampleNumber5)
        localEmergencyNumberDao.insertLocalEmergencyNumbers(numbers)
        localEmergencyNumberDao.deleteNumbersForCountry("US")

        val usNumbers = localEmergencyNumberDao.getNumbersForCountry("US").first()
        assertTrue("US numbers should be deleted", usNumbers.isEmpty())

        val frNumbers = localEmergencyNumberDao.getNumbersForCountry("FR").first()
        assertEquals("FR numbers should still exist", 3, frNumbers.size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteNumbersForService_deletesAllForSpecificService() = runBlocking {
        val numbers = listOf(sampleNumber1, sampleNumber2, sampleNumber3, sampleNumber4)
        localEmergencyNumberDao.deleteNumbersForService(1)

        val service1Numbers = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 1).first()
        assertTrue("Service 1 numbers should be deleted", service1Numbers.isEmpty())

        val service2Numbers = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 2).first()
        assertEquals("Service 2 numbers should still exist", 1, service2Numbers.size)

        val service3Numbers = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 3).first()
        assertEquals("Service 3 numbers should still exist", 1, service3Numbers.size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllLocalEmergencyNumbers_clearsTable() = runBlocking {
        val numbers = listOf(sampleNumber1, sampleNumber2, sampleNumber3)
        localEmergencyNumberDao.insertLocalEmergencyNumbers(numbers)
        localEmergencyNumberDao.deleteAllLocalEmergencyNumbers()

        val allNumbers = localEmergencyNumberDao.getNumbersForCountryAndService("FR", 1).first()
        assertTrue("Table should be empty", allNumbers.isEmpty())
    }
}