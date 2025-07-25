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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    /**
     * Retrieves a service entity from the database by its ID.
     *
     * @param senId The ID of the service to retrieve.
     * @return The [ServiceEntity] with the given ID, or null if no such service exists.
     */
    @Query("SELECT * FROM sen_services WHERE sen_id = :senId")
    suspend fun getServiceById(senId: Int): ServiceEntity?

    /**
     * Retrieves all services from the database, ordered by their name resource ID in ascending order.
     *
     * @return A Flow that emits a list of [ServiceEntity] objects.
     */
    @Query("SELECT * FROM sen_services ORDER BY sen_name_res_id ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    /**
     * Deletes a service from the database by its ID.
     *
     * @param senId The ID of the service to delete.
     */
    @Query("DELETE FROM sen_services WHERE sen_id = :senId")
    suspend fun deleteServiceById(senId: Int)

    /**
     * Deletes all services from the `sen_services` table.
     */
    @Query("DELETE FROM sen_services")
    suspend fun deleteAllServices()

}