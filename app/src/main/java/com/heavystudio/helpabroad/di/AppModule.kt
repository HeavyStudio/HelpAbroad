package com.heavystudio.helpabroad.di

import android.content.Context
import androidx.room.Room
import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.dao.ServiceDao
import com.heavystudio.helpabroad.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "help_abroad_database.db"
        )
            .createFromAsset("database/help_abroad_db_seeds.db")
            .fallbackToDestructiveMigration(true) // Dev only TODO: Remove for prod
            .build()
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categoryDao()

    @Provides
    fun provideCountryDao(appDatabase: AppDatabase): CountryDao = appDatabase.countryDao()

    @Provides
    fun provideEmergencyNumberDao(appDatabase: AppDatabase): EmergencyNumberDao = appDatabase.emergencyNumberDao()

    @Provides
    fun provideServiceDao(appDatabase: AppDatabase): ServiceDao = appDatabase.serviceDao()
}