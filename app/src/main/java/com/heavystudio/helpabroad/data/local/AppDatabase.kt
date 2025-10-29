package com.heavystudio.helpabroad.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao

    companion object {
        // Migration from v1 to v2.
        // Add SMS support and "114" number for France.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    ALTER TABLE emergency_numbers
                    ADD COLUMN number_type TEXT NOT NULL DEFAULT 'CALL'
                """.trimIndent())
            }
        }

        // Migration from v2 to v3.
        // Fix data inconsistency.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    UPDATE emergency_numbers 
                    SET service_type_id = 6 
                    WHERE country_id = 16 AND phone_number = '114'
                """.trimIndent())
            }
        }
    }
}