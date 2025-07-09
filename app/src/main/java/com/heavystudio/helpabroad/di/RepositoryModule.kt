package com.heavystudio.helpabroad.di

import com.heavystudio.helpabroad.data.repository.EmergencyRepository
import com.heavystudio.helpabroad.data.repository.EmergencyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmergencyRepository(
        emergencyRepositoryImpl: EmergencyRepositoryImpl
    ): EmergencyRepository
}