package com.heavystudio.helpabroad.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.heavystudio.helpabroad.data.local.AppDatabase
import com.heavystudio.helpabroad.data.local.dao.CountryDao
import com.heavystudio.helpabroad.data.local.dao.SetupDao
import com.heavystudio.helpabroad.data.local.dto.InitialData
import com.heavystudio.helpabroad.data.local.model.CountryEntity
import com.heavystudio.helpabroad.data.local.model.CountryNameEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyNumberEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyServiceTypeEntity
import com.heavystudio.helpabroad.data.local.model.ServiceTypeNameEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        setupDaoProvider: Provider<SetupDao> // Hilt nous fournit un Provider pour éviter les dépendances cycliques
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "emergency_numbers.db"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // On lance la logique de pré-remplissage dans une coroutine
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(context, setupDaoProvider.get())
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideCountryDao(appDatabase: AppDatabase): CountryDao {
        return appDatabase.countryDao()
    }

    @Provides
    fun provideSetupDao(appDatabase: AppDatabase): SetupDao {
        return appDatabase.setupDao()
    }

    // Le reste du module (provideCountryDao, provideSetupDao) ne change pas...

    private suspend fun populateDatabase(context: Context, setupDao: SetupDao) {
        // 1. LIRE ET PARSER LE FICHIER JSON
        val jsonString = context.assets.open("initial_data.json")
            .bufferedReader()
            .use { it.readText() }
        val initialData = Json.decodeFromString<InitialData>(jsonString)

        // 2. TRANSFORMER LES DONNÉES JSON EN ENTITÉS ROOM

        // --- Services ---
        val serviceTypesToInsert = mutableListOf<EmergencyServiceTypeEntity>()
        val serviceTypeNamesToInsert = mutableListOf<ServiceTypeNameEntity>()
        initialData.serviceTypes.forEachIndexed { index, jsonServiceType ->
            val serviceTypeId = index + 1
            serviceTypesToInsert.add(EmergencyServiceTypeEntity(
                    id = serviceTypeId,
                    serviceCode = jsonServiceType.code,
                    defaultIconRef = jsonServiceType.icon
            ))
            jsonServiceType.names.forEach { name ->
                serviceTypeNamesToInsert.add(ServiceTypeNameEntity(
                    serviceTypeId = serviceTypeId,
                    languageCode = name.lang,
                    name = name.name
                ))
            }
        }

        // --- Pays ---
        val countriesToInsert = mutableListOf<CountryEntity>()
        val countryNamesToInsert = mutableListOf<CountryNameEntity>()
        val emergencyNumbersToInsert = mutableListOf<EmergencyNumberEntity>()

        // Créer une map pour facilement retrouver l'ID d'un service par son code
        val serviceCodeToIdMap = serviceTypesToInsert.associate { it.serviceCode to it.id }

        initialData.countries.forEachIndexed { index, jsonCountry ->
            val countryId = index + 1
            countriesToInsert.add(CountryEntity(id = countryId, isoCode = jsonCountry.isoCode))
            jsonCountry.names.forEach { name ->
                countryNamesToInsert.add(CountryNameEntity(
                    countryId = countryId,
                    languageCode = name.lang,
                    name = name.name
                ))
            }
            jsonCountry.services.forEach { service ->
                val serviceTypeId = serviceCodeToIdMap[service.type]
                if (serviceTypeId != null) {
                    emergencyNumbersToInsert.add(EmergencyNumberEntity(
                        countryId = countryId,
                        serviceTypeId = serviceTypeId,
                        phoneNumber = service.number
                    ))
                }
            }
        }

        // 3. INSÉRER TOUTES LES DONNÉES DANS LA BASE
        setupDao.insertServiceTypes(serviceTypesToInsert)
        setupDao.insertServiceTypeNames(serviceTypeNamesToInsert)
        setupDao.insertCountries(countriesToInsert)
        setupDao.insertCountryNames(countryNamesToInsert)
        setupDao.insertEmergencyNumbers(emergencyNumbersToInsert)
    }
}