package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    // --- Convenience Methods ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertService(service: ServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertServices(vararg services: ServiceEntity)

    @Update
    suspend fun updateService(service: ServiceEntity): Int

    @Delete
    suspend fun deleteService(service: ServiceEntity): Int

    // --- Queries ---
    @Query("SELECT * FROM services WHERE id = :id")
    fun getServiceById(id: Int): Flow<ServiceEntity?>

    @Query("SELECT * FROM services ORDER BY name_res_key ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE category_id = :categoryId ORDER BY name_res_key ASC")
    fun getServicesByCategoryId(categoryId: Int): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE can_be_deleted = :canBeDeleted ORDER BY name_res_key ASC")
    fun getServicesByDeletableStatus(canBeDeleted: Boolean): Flow<List<ServiceEntity>>

    @Query("DELETE FROM services WHERE id = :id AND can_be_deleted = 1")
    suspend fun deleteServiceById(id: Int): Int

    @Query("DELETE FROM services WHERE category_id = :categoryId AND can_be_deleted = 1")
    suspend fun deleteServicesByCategory(categoryId: Int): Int

    @Query("DELETE FROM services WHERE can_be_deleted = 1")
    suspend fun deleteAllServices(): Int
}
