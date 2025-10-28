package com.heavystudio.helpabroad.di

import com.heavystudio.helpabroad.data.repository.CountryRepositoryImpl
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing repository dependencies.
 * This module is responsible for binding repository interfaces to their concrete implementations.
 * It is installed in the [SingletonComponent], meaning the provided repositories will have a singleton scope
 * and live as long as the application.
 *
 * @author Heavy Studio.
 * @since 0.1.0 Creation of the module.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCountryRepository(
        countryRepositoryImpl: CountryRepositoryImpl
    ): CountryRepository
}