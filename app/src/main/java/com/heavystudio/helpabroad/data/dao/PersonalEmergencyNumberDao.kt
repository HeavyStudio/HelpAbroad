package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.PersonalEmergencyNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalEmergencyNumberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalNumber(personalNumber: PersonalEmergencyNumberEntity): Long

    @Update
    suspend fun updatePersonalNumber(personalNumber: PersonalEmergencyNumberEntity)

    @Delete
    suspend fun deletePersonalNumber(personalNumber: PersonalEmergencyNumberEntity)

    @Query("SELECT * FROM pen_personal_emergency_numbers WHERE pen_id = :penId")
    suspend fun getPersonalNumberById(penId: Int): PersonalEmergencyNumberEntity?

    @Query("SELECT * FROM pen_personal_emergency_numbers ORDER BY pen_is_favorite DESC, pen_contact_name ASC")
    fun getAllPersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>>

    @Query("SELECT * FROM pen_personal_emergency_numbers WHERE pen_country_iso_code = :countryIsoCode")
    fun getPersonalNumbersByCountry(countryIsoCode: String): Flow<List<PersonalEmergencyNumberEntity>>

    @Query("SELECT * FROM pen_personal_emergency_numbers WHERE pen_category_id = :categoryId ORDER BY pen_is_favorite DESC, pen_contact_name ASC")
    fun getPersonalNumbersByCategory(categoryId: Int): Flow<List<PersonalEmergencyNumberEntity>>

    @Query("SELECT * FROM pen_personal_emergency_numbers WHERE pen_category_id IS NULL ORDER BY pen_contact_name ASC")
    fun getUncategorizedPersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>>

    @Query("SELECT * FROM pen_personal_emergency_numbers WHERE pen_is_favorite = 1 ORDER BY pen_contact_name ASC")
    fun getFavoritePersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>>

    @Query("SELECT COUNT(pen_id) FROM pen_personal_emergency_numbers")
    suspend fun getPersonalNumbersCount(): Int

    @Query("SELECT COUNT(pen_id) FROM pen_personal_emergency_numbers WHERE pen_is_favorite = 1")
    suspend fun getFavoritePersonalNumbersCount(): Int

    @Query("UPDATE pen_personal_emergency_numbers SET pen_is_favorite = :isFavorite WHERE pen_id = :penId")
    suspend fun setFavoriteStatus(penId: Int, isFavorite: Boolean)

    @Query("DELETE FROM pen_personal_emergency_numbers WHERE pen_id = :penId")
    suspend fun deletePersonalNumberById(penId: Int)

    @Query("DELETE FROM pen_personal_emergency_numbers")
    suspend fun deleteAllPersonalNumbers()
}