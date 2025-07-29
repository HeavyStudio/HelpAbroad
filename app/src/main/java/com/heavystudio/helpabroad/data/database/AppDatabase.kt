package com.heavystudio.helpabroad.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.dao.CountryFtsDao
import com.heavystudio.helpabroad.data.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.dao.ServiceDao

@Database(
    entities = [
        CategoryEntity::class,
        CountryEntity::class,
        CountryFtsEntity::class,
        EmergencyNumberEntity::class,
        ServiceEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun countryDao(): CountryDao
    abstract fun countryFtsDao(): CountryFtsDao
    abstract fun emergencyNumberDao(): EmergencyNumberDao
    abstract fun serviceDao(): ServiceDao
}