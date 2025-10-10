package com.heavystudio.helpabroad.di

import com.heavystudio.helpabroad.data.settings.SettingsRepository
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetDataEntryPoint {
    fun countryRepository(): CountryRepository
    fun settingsRepository(): SettingsRepository
}