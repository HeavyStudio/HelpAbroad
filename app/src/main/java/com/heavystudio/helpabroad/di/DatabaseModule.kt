package com.heavystudio.helpabroad.di

import android.content.Context
import androidx.room.Room
import com.heavystudio.helpabroad.data.local.AppDatabase
import com.heavystudio.helpabroad.data.local.dao.CountryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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
            .createFromAsset("database/db_seeds.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideCountryDao(appDatabase: AppDatabase): CountryDao {
        return appDatabase.countryDao()
    }
}