package com.heavystudio.helpabroad.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.model.entity.CountryEntity
import com.heavystudio.helpabroad.data.model.entity.CountryTranslationEntity
import com.heavystudio.helpabroad.data.model.entity.CountryTranslationFtsEntity
import com.heavystudio.helpabroad.data.model.entity.EmergencyNumberEntity
import com.heavystudio.helpabroad.data.model.entity.RegionEntity
import com.heavystudio.helpabroad.data.model.entity.ServiceEntity

@Database(
    entities = [
        CountryEntity::class,
        CountryTranslationEntity::class,
        CountryTranslationFtsEntity::class,
        EmergencyNumberEntity::class,
        ServiceEntity::class,
        RegionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun countryDao(): CountryDao
    abstract fun emergencyNumberDao(): EmergencyNumberDao
}