package com.heavystudio.helpabroad.data.repository

import android.app.Service
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.ServiceDao
import com.heavystudio.helpabroad.data.database.ServiceEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(private val serviceDao: ServiceDao) {

    private val entityType = "Service"
    private val tag = "ServiceRepository"

    /**
     * Creates a new service in the database.
     *
     * This function attempts to insert the given [ServiceEntity] into the database.
     * It logs the attempt, success, or failure of the operation.
     *
     * @param service The [ServiceEntity] object to be created.
     * @return A [Result] object containing the ID of the newly created service if successful,
     *         or an [Exception] if the operation fails.
     *         Specifically, it can return [SQLiteConstraintException] if a database constraint is
     *         violated, or a generic [Exception] for other errors.
     */
    suspend fun createService(service: ServiceEntity): Result<Long> {
        val serviceDetails = service.toString()
        return try {
            val attempt = LogMessageUtils.attempting("insert", entityType, service.id, serviceDetails)
            Log.d(tag, attempt)
            val newId = serviceDao.insertService(service)
            if (newId > 0) {
                val success = LogMessageUtils.success("insert", entityType, service.id, serviceDetails)
                Log.i(tag, success)
                Result.success(newId)
            } else {
                val failure = LogMessageUtils.failure("insert", entityType, service.id, serviceDetails)
                val exception = Exception("Failed to insert service, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation("insert", entityType, serviceDetails)
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("insert", entityType, serviceDetails)
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves all services from the database.
     *
     * This function fetches all [ServiceEntity] objects stored in the database.
     * If an error occurs during the fetching process, it logs the error and emits an empty list.
     *
     * @return A [Flow] emitting a list of [ServiceEntity] objects.
     *         Emits an empty list if an error occurs.
     */
    fun getAllServices(): Flow<List<ServiceEntity>> {
        return serviceDao.getAllServices()
            .catch { e ->
                val errorMesage = "Error fetching all services"
                Log.e(tag, errorMesage, e)
                emit(emptyList())
            }
    }

    /**
     * Retrieves a service by its ID.
     *
     * This function attempts to fetch a [ServiceEntity] from the database based on the given ID.
     * If an error occurs during the fetching process, it logs the error and emits `null`.
     *
     * @param id The ID of the service to retrieve.
     * @return A [Flow] emitting the [ServiceEntity] if found, or `null` if not found or an error occurs.
     */
    fun getServiceById(id: Int): Flow<ServiceEntity?> {
        return serviceDao.getServiceById(id)
            .catch { e ->
                val errorMessage = "Error fetching service by ID: $id"
                Log.e(tag, errorMessage, e)
                emit(null)
            }
    }

    /**
     * Retrieves a list of services associated with a specific category ID.
     *
     * This function fetches all [ServiceEntity] objects from the database that belong to the
     * category identified by [categoryId]. It returns a [Flow] that emits the list of services.
     * In case of an error during the database operation, it logs the error and emits an empty list.
     *
     * @param categoryId The ID of the category for which to retrieve services.
     * @return A [Flow] emitting a list of [ServiceEntity] objects.
     *         Emits an empty list if no services are found or if an error occurs.
     */
    fun getServicesByCategoryId(categoryId: Int): Flow<List<ServiceEntity>> {
        return serviceDao.getServicesByCategoryId(categoryId)
            .catch { e ->
                val errorMessage = "Error fetching services by category ID: $categoryId"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Retrieves services based on their deletable status.
     *
     * This function queries the database for services that either can or cannot be deleted,
     * as specified by the [canBeDeleted] parameter.
     * It returns a [Flow] that emits a list of [ServiceEntity] objects.
     * In case of an error during the database operation, it logs the error and emits an empty list.
     *
     * @param canBeDeleted A boolean indicating whether to fetch deletable (true) or non-deletable
     *        (false) services.
     * @return A [Flow] emitting a list of [ServiceEntity] objects matching the deletable status.
     *         Emits an empty list if an error occurs.
     */
    fun getServicesByDeletableStatus(canBeDeleted: Boolean): Flow<List<ServiceEntity>> {
        return serviceDao.getServicesByDeletableStatus(canBeDeleted)
            .catch { e ->
                val errorMessage = "Error fetching services by deletable status (canBeDeleted = $canBeDeleted)"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    // --- Specific getters for predefined and custom categories ---
    /**
     * Retrieves a [Flow] of predefined services.
     *
     * Predefined services are those that cannot be deleted by the user.
     * This function internally calls [getServicesByDeletableStatus] with `canBeDeleted = false`.
     * If an error occurs during fetching, an empty list is emitted.
     *
     * @return A [Flow] emitting a list of [ServiceEntity] objects that are predefined.
     */
    fun getPredefinedServices(): Flow<List<ServiceEntity>> {
        return getServicesByDeletableStatus(canBeDeleted = false)
    }

    /**
     * Retrieves a [Flow] of custom [ServiceEntity] objects from the database.
     * Custom services are defined as those that can be deleted (i.e., `canBeDeleted` is true).
     *
     * This function calls [getServicesByDeletableStatus] with `canBeDeleted` set to `true`.
     * It handles potential errors during the database query by logging them and emitting an empty list.
     *
     * @return A [Flow] that emits a list of custom [ServiceEntity] objects.
     *         If an error occurs, it emits an empty list.
     */
    fun getCustomServices(): Flow<List<ServiceEntity>> {
        return getServicesByDeletableStatus(canBeDeleted = true)
    }

    /**
     * Updates an existing service in the database.
     *
     * This function attempts to update the given [ServiceEntity] in the database.
     * It logs the attempt, success, or failure of the operation.
     *
     * @param service The [ServiceEntity] object to be updated. It must have a valid ID.
     * @return A [Result] object containing the number of rows updated if successful (should be 1),
     *         or an [Exception] if the operation fails.
     *         Specifically, it can return [SQLiteConstraintException] if a database constraint is
     *         violated, or a generic [Exception] for other errors or if no rows were updated.
     */
    suspend fun updateService(service: ServiceEntity): Result<Int> {
        val serviceDetails = service.toString()
        return try {
            val attempt = LogMessageUtils.attempting("update", entityType, service.id, serviceDetails)
            Log.d(tag, attempt)
            val rowsUpdated = serviceDao.updateService(service)
            if (rowsUpdated > 0) {
                val success = LogMessageUtils.success("update", entityType, service.id, serviceDetails)
                Log.i(tag, success)
                Result.success(rowsUpdated)
            } else {
                val failure = LogMessageUtils.failure("update", entityType, service.id, serviceDetails)
                val exception = Exception("Failed to update service, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation("update", entityType, serviceDetails)
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("update", entityType, serviceDetails)
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a service from the database.
     *
     * This function attempts to delete the given [ServiceEntity] from the database.
     * It logs the attempt and then calls [deleteServiceById] to perform the actual deletion.
     *
     * @param service The [ServiceEntity] object to be deleted.
     * @return A [Result] object containing the number of rows deleted if successful (should be 1),
     *         or an [Exception] if the operation fails.
     */
    suspend fun deleteService(service: ServiceEntity): Result<Int> {
        val attempt = LogMessageUtils.attempting("delete", entityType, service.id, service.toString())
        Log.d(tag, attempt)
        return deleteServiceById(service.id)
    }

    /**
     * Deletes a service from the database by its ID.
     *
     * This function attempts to delete the service with the given [id] from the database.
     * It logs the attempt, success, or failure of the operation.
     *
     * @param id The ID of the service to be deleted.
     * @return A [Result] object containing the number of rows deleted if successful (should be 1),
     *         or an [Exception] if the operation fails or no rows are deleted.
     */
    suspend fun deleteServiceById(id: Int): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, id, "")
            Log.d(tag, attempt)
            val rowsDeleted = serviceDao.deleteServiceById(id)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, id, "")
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, id, "")
                val exception = Exception("Failed to delete service, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("delete", entityType, "")
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Deletes all services associated with a specific category ID from the database.
     *
     * This function attempts to delete services belonging to the given [categoryId].
     * It logs the attempt, success, or failure of the operation.
     *
     * @param categoryId The ID of the category whose services are to be deleted.
     * @return A [Result] object containing the number of rows deleted if successful,
     *         or an [Exception] if the operation fails. A failure can occur if the DAO
     *         returns an invalid result (e.g., 0 rows deleted when services were expected to be deleted)
     *         or if any other unexpected error occurs during the database operation.
     */
    suspend fun deleteServiceByCategory(categoryId: Int): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, categoryId, "")
            Log.d(tag, attempt)
            val rowsDeleted = serviceDao.deleteServicesByCategory(categoryId)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, categoryId, "")
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, categoryId, "")
                val exception = Exception("Failed to delete services, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("delete", entityType, "")
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }
}