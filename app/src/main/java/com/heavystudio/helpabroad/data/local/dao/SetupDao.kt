package com.heavystudio.helpabroad.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.heavystudio.helpabroad.data.local.model.CountryEntity
import com.heavystudio.helpabroad.data.local.model.CountryNameEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyNumberEntity
import com.heavystudio.helpabroad.data.local.model.EmergencyServiceTypeEntity
import com.heavystudio.helpabroad.data.local.model.ServiceTypeNameEntity

@Dao
interface SetupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(items: List<CountryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountryNames(items: List<CountryNameEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceTypes(items: List<EmergencyServiceTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceTypeNames(items: List<ServiceTypeNameEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyNumbers(items: List<EmergencyNumberEntity>)
}