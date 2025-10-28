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
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao

    companion object {
        // Migration from v1 to v2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    ALTER TABLE emergency_numbers
                    ADD COLUMN number_type TEXT NOT NULL DEFAULT 'CALL'
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO emergency_service_types (service_code, default_icon_ref) 
                    VALUES ("DEAF", "ic_deaf")
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO service_type_names (service_type_id, language_code, name) 
                    VALUES
                        (6, "en", "SOS Deaf"),
                        (6, "fr", "SOS Sourds"),
                        (6, "de", "SOS Gehörlos"),
                        (6, "es", "SOS Sordos"),
                        (6, "pt", "SOS Surdos"),
                        (6, "it", "SOS Sordi")
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO emergency_numbers (country_id, service_type_id, phone_number, number_type) 
                    VALUES (16, 6, '114', 'SMS')
                """.trimIndent())
            }
        }
    }
}