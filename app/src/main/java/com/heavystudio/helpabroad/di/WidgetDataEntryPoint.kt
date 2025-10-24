package com.heavystudio.helpabroad.di

import com.heavystudio.helpabroad.data.settings.SettingsRepository
import com.heavystudio.helpabroad.domain.repository.CountryRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt EntryPoint to provide dependencies for non-Hilt-aware classes, specifically for App Widgets.
 *
 * This interface allows access to the application's singleton-scoped dependencies, such as repositories,
 * from components like `GlanceAppWidgetReceiver` which are instantiated by the Android system
 * and cannot be directly injected by Hilt.
 *
 * It provides access to:
 * - [CountryRepository] for fetching country-related data.
 * - [SettingsRepository] for accessing user settings.
 *
 * @author Heavy Studio.
 * @since 0.X.X WIP, coming soon!
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetDataEntryPoint {
    fun countryRepository(): CountryRepository
    fun settingsRepository(): SettingsRepository
}