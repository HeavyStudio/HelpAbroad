package com.heavystudio.helpabroad.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.heavystudio.helpabroad.data.local.AppDatabase
import com.heavystudio.helpabroad.data.local.dao.CountryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides database-related dependencies for the application.
 *
 * This module is installed in the [SingletonComponent], meaning the provided dependencies
 * will have a singleton scope and live as long as the application.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the object.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of the [AppDatabase].
     *
     * This function is responsible for building the Room database instance for the application.
     * It uses a pre-packaged database file from the assets (`database/db_seeds.db`) to
     * initialize the data. If a schema migration is required but not provided, it will
     * destructively recreate the database, discarding all existing data.
     *
     * @param context The application context, provided by Hilt.
     * @return A singleton instance of [AppDatabase].
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "help_abroad.db"
        )
            .createFromAsset("database/db_seeds_v2.db")
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3
            )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    /**
     * Provides a [CountryDao] instance.
     *
     * This function is a Dagger Hilt provider that retrieves the [CountryDao]
     * from the provided [AppDatabase] instance. This allows other parts of the
     * application to inject [CountryDao] to interact with the country data table.
     *
     * @param appDatabase The singleton instance of [AppDatabase] provided by Hilt.
     * @return An instance of [CountryDao].
     */
    @Provides
    fun provideCountryDao(appDatabase: AppDatabase): CountryDao {
        return appDatabase.countryDao()
    }
}