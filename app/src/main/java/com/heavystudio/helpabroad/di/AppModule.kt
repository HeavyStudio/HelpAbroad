package com.heavystudio.helpabroad.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.dao.ServiceDao
import com.heavystudio.helpabroad.data.dao.UserContactNumberDao
import com.heavystudio.helpabroad.data.database.AppDatabase
import com.heavystudio.helpabroad.data.repository.OnboardingInterface
import com.heavystudio.helpabroad.data.repository.OnboardingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "help_abroad_user_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

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
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideOnboardingRepository(dataStore: DataStore<Preferences>): OnboardingInterface {
        return OnboardingRepository(dataStore)
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categoryDao()

    @Provides
    fun provideCountryDao(appDatabase: AppDatabase): CountryDao = appDatabase.countryDao()

    @Provides
    fun provideEmergencyNumberDao(appDatabase: AppDatabase): EmergencyNumberDao = appDatabase.emergencyNumberDao()

    @Provides
    fun provideServiceDao(appDatabase: AppDatabase): ServiceDao = appDatabase.serviceDao()

    @Provides
    fun provideUserContactNumberDao(appDatabase: AppDatabase): UserContactNumberDao = appDatabase.userContactNumberDao()
}