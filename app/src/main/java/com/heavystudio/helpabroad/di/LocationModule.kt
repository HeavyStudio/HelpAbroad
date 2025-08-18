package com.heavystudio.helpabroad.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.heavystudio.helpabroad.data.location.AndroidLocationProvider
import com.heavystudio.helpabroad.data.location.LocationManager
import com.heavystudio.helpabroad.data.location.LocationRepository
import com.heavystudio.helpabroad.data.location.LocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideLocationRepository(
        fusedClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): LocationRepository = LocationRepositoryImpl(fusedClient, context)
}