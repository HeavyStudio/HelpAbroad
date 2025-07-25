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
    private val sampleNumber2 = LocalEmergencyNumberEntity(2, "FR", "18", 1)
    private val sampleNumber3 = LocalEmergencyNumberEntity(3, "FR", "19", 1)

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
}