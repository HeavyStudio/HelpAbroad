package com.heavystudio.helpabroad.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heavystudio.helpabroad.data.local.dao.CountryDao
import com.heavystudio.helpabroad.data.local.dao.SetupDao
import com.heavystudio.helpabroad.data.local.model.CountryEntity
import com.heavystudio.helpabroad.data.local.model.CountryNameEntity
import com.heavystudio.helpabroad.data.local.model.CountryNameFtsEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyNumberEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyServiceTypeEntity
import com.heavystudio.helpabroad.data.local.model.ServiceTypeNameEntity

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
    abstract fun setupDao(): SetupDao
}