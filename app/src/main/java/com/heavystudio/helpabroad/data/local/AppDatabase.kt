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
                // Ensure the "DEAF" service type exists.
                db.execSQL("""
                    INSERT OR IGNORE INTO emergency_service_types (id, service_code, default_icon_ref) 
                    VALUES (6, 'DEAF', 'ic_deaf')
                """.trimIndent())

                // Update or insert the names for the "DEAF" service in all supported languages.
                db.execSQL("""
                    INSERT OR REPLACE INTO service_type_names (id, service_type_id, language_code, name)
                    VALUES 
                        ((SELECT id FROM service_type_names WHERE service_type_id = 6 AND language_code = 'en'), 6, 'en', 'Deaf & Hard of Hearing'),
                        ((SELECT id FROM service_type_names WHERE service_type_id = 6 AND language_code = 'fr'), 6, 'fr', 'Sourds et malentendants'),
                        ((SELECT id FROM service_type_names WHERE service_type_id = 6 AND language_code = 'de'), 6, 'de', 'Gehörlose & Schwerhörige'),
                        ((SELECT id FROM service_type_names WHERE service_type_id = 6 AND language_code = 'it'), 6, 'it', 'Sordi e con problemi di udito'),
                        ((SELECT id FROM service_type_names WHERE service_type_id = 6 AND language_code = 'es'), 6, 'es', 'Sordos y con discapacidad auditiva'),
                        ((SELECT id FROM service_type_names WHERE service_type_id = 6 AND language_code = 'pt'), 6, 'pt', 'Surdos e com deficiência auditiva')
                """.trimIndent())

                // Delete any previous incorrect "114" entry and insert the correct one.
                db.execSQL("""
                    DELETE FROM emergency_numbers 
                    WHERE country_id = 16 
                    AND phone_number = '114'
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO emergency_numbers (country_id, service_type_id, phone_number, number_type)
                    VALUES (16, 6, '114', 'SMS')
                """.trimIndent())
            }
        }
    }
}