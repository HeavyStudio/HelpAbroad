package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.UserContactNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserContactNumberDao {

    // --- Convenience Methods ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserContactNumber(userContactNumber: UserContactNumberEntity): Long

    @Update
    suspend fun updateUserContactNumber(userContactNumber: UserContactNumberEntity): Int

    @Delete
    suspend fun deleteUserContactNumber(userContactNumber: UserContactNumberEntity): Int

    // --- Queries ---
    @Query("SELECT * FROM user_contact_numbers ORDER BY contact_name ASC")
    fun getAllUserContactNumbers(): Flow<List<UserContactNumberEntity>>

    @Query("SELECT * FROM user_contact_numbers WHERE id = :id")
    fun getUserContactNumberById(id: Int): Flow<UserContactNumberEntity?>

    @Query(
        "SELECT * FROM user_contact_numbers " +
                "WHERE country_iso_code = :isoCode " +
                "ORDER BY contact_name ASC"
    )
    fun getUserContactNumbersByCountry(isoCode: String): Flow<List<UserContactNumberEntity>>

    @Query(
        "SELECT * FROM user_contact_numbers " +
                "WHERE service_id = :serviceId " +
                "ORDER BY contact_name ASC"
    )
    fun getUserContactNumbersByService(serviceId: Int): Flow<List<UserContactNumberEntity>>

    @Query(
        "SELECT * FROM user_contact_numbers " +
                "WHERE country_iso_code = :isoCode AND service_id = :serviceId " +
                "ORDER BY contact_name ASC"
    )
    fun getUserContactNumbersByCountryAndService(isoCode: String, serviceId: Int):
            Flow<List<UserContactNumberEntity>>

    @Query("DELETE FROM user_contact_numbers WHERE id = :id")
    suspend fun deleteUserContactNumberById(id: Int): Int

    @Query("DELETE FROM user_contact_numbers WHERE country_iso_code = :isoCode")
    suspend fun deleteUserContactNumbersByCountry(isoCode: String): Int

    @Query("DELETE FROM user_contact_numbers WHERE service_id = :serviceId")
    suspend fun deleteUserContactNumbersByService(serviceId: Int): Int

    @Query("DELETE FROM user_contact_numbers")
    suspend fun deleteAllUserContactNumbers(): Int

}