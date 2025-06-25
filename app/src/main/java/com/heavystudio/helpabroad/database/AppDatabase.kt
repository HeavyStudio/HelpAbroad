package com.heavystudio.helpabroad.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.heavystudio.helpabroad.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.Country
import com.heavystudio.helpabroad.data.EmergencyNumber

@Database(
    entities = [Country::class, EmergencyNumber::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emergencyNumberDao(): EmergencyNumberDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "emergency_numbers_db"
                )
                // !!! WARNING: For a production database, you will need to handle migrations correctly
                // .addMigrations(MIGRATION_1_2) for example
                    .fallbackToDestructiveMigration() // For development, this is sufficient
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}