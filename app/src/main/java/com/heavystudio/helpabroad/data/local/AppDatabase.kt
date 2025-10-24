package com.heavystudio.helpabroad.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heavystudio.helpabroad.data.local.dao.CountryDao
import com.heavystudio.helpabroad.data.local.model.CountryEntity
import com.heavystudio.helpabroad.data.local.model.CountryNameEntity
import com.heavystudio.helpabroad.data.local.model.CountryNameFtsEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyNumberEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyServiceTypeEntity
import com.heavystudio.helpabroad.data.local.model.ServiceTypeNameEntity

/**
 * The main Room database for the application.
 *
 * This class defines the database configuration and serves as the main access point
 * to the persisted data. It lists all the entities that are part of the database
 * and the DAOs used to interact with them.
 *
 * @see CountryDao for data access operations related to countries.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the class.
 */
@Database(
    entities = [
        CountryEntity::class,
        CountryNameEntity::class,
        CountryNameFtsEntity::class,
        EmergencyNumberEntity::class,
        EmergencyServiceTypeEntity::class,
        ServiceTypeNameEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
}