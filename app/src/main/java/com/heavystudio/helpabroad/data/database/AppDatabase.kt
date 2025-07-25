package com.heavystudio.helpabroad.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.dao.LocalEmergencyNumberDao
import com.heavystudio.helpabroad.data.dao.PersonalEmergencyNumberDao
import com.heavystudio.helpabroad.data.dao.ServiceDao

@Database(
    entities = [
        CategoryEntity::class,
        CountryEntity::class,
        LocalEmergencyNumberEntity::class,
        PersonalEmergencyNumberEntity::class,
        ServiceEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun countryDao(): CountryDao
    abstract fun localEmergencyNumberDao(): LocalEmergencyNumberDao
    abstract fun personalEmergencyNumberDao(): PersonalEmergencyNumberDao
    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "help_abroad_database_seeds"
                )
                    .createFromAsset("database/help_abroad_database_seeds.db")
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}