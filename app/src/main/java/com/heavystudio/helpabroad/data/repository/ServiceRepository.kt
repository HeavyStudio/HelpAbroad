package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.database.ServiceEntity
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {

    /**
     * Inserts a new service into the local database.
     *
     * @param service The [ServiceEntity] to be inserted.
     * @return The row ID of the newly inserted service, or -1 if an error occurred.
     */
    suspend fun insertService(service: ServiceEntity): Long

    /**
     * Inserts a list of services into the local database.
     * If a service with the same ID already exists, it will be replaced.
     *
     * @param services The list of [ServiceEntity] objects to insert.
     */
    suspend fun insertServices(services: List<ServiceEntity>)

    /**
     * Retrieves a service by its ID.
     *
     * @param senId The ID of the service to retrieve.
     * @return The [ServiceEntity] if found, otherwise null.
     */
    suspend fun getServiceById(senId: Int): ServiceEntity?

    /**
     * Retrieves all services from the repository as a Flow.
     *
     * This function allows observing changes to the list of services.
     * Each emission from the Flow will be a new list of [ServiceEntity] objects.
     *
     * @return A [Flow] that emits a list of [ServiceEntity] objects.
     */
    fun getAllServices(): Flow<List<ServiceEntity>>

    /**
     * Updates an existing service in the data source.
     *
     * @param service The [ServiceEntity] object to be updated.
     */
    suspend fun updateService(service: ServiceEntity)

    /**
     * Deletes a service.
     *
     * @param service The service to delete.
     */
    suspend fun deleteService(service: ServiceEntity)

    /**
     * Deletes a service from the database by its ID.
     *
     * @param senId The ID of the service to delete.
     */
    suspend fun deleteServiceById(senId: Int)

    /**
     * Deletes all services from the local data source.
     */
    suspend fun deleteAllServices()
}