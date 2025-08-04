package com.heavystudio.helpabroad.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyNumberRepository @Inject constructor(private val emergencyNumberDao: EmergencyNumberDao) {

    private val entityType = "EmergencyNumber"
    private val tag = "EmergencyNumberRepository"

    /**
     * Inserts an emergency number into the database.
     *
     * This function attempts to insert the provided [EmergencyNumberEntity] into the database.
     * It logs the attempt, success, or failure of the operation.
     *
     * @param emergencyNumber The [EmergencyNumberEntity] to be inserted.
     * @return A [Result] object containing the ID of the newly inserted emergency number if successful,
     *         or an [Exception] if the insertion fails.
     *         Possible failure reasons include:
     *         - The DAO operation returns an invalid result (e.g., 0 or negative).
     *         - A [SQLiteConstraintException] occurs, indicating a violation of database constraints
     *           (e.g., duplicate entry).
     *         - Any other unexpected [Exception] during the insertion process.
     */
    suspend fun insertEmergencyNumber(emergencyNumber: EmergencyNumberEntity): Result<Long> {
        val emergencyNumberDetails = emergencyNumber.toString()
        val emergencyNumberId = emergencyNumber.countryIsoCode + emergencyNumber.emergencyNumber
        return try {
            val attempt = LogMessageUtils.attempting(
                "insert", entityType,
                emergencyNumberId,
                emergencyNumberDetails
            )
            Log.i(tag, attempt)
            val newEmergencyNumberId = emergencyNumberDao.insertEmergencyNumber(emergencyNumber)
            if (newEmergencyNumberId > 0) {
                val success = LogMessageUtils.success(
                    "insert",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                Log.i(tag, success)
                Result.success(newEmergencyNumberId)
            } else {
                val failure = LogMessageUtils.failure(
                    "insert",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                val exception = Exception("Failed to insert emergency number, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation(
                "insert",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "insert",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Updates an existing emergency number in the database.
     *
     * This function attempts to update the provided [EmergencyNumberEntity] in the database.
     * It logs the attempt, success, or failure of the operation.
     *
     * @param emergencyNumber The [EmergencyNumberEntity] with updated information.
     *                        The entity must already exist in the database, identified by its primary key.
     * @return A [Result] object containing the number of rows updated if successful (should be 1),
     *         or an [Exception] if the update fails.
     *         Possible failure reasons include:
     *         - The DAO operation returns an invalid result (e.g., 0, indicating no rows were updated,
     *           or a negative value). This could mean the emergency number to be updated was not found.
     *         - A [SQLiteConstraintException] occurs, indicating a violation of database constraints
     *           (e.g., if the update tries to create a duplicate entry based on unique constraints).
     *         - Any other unexpected [Exception] during the update process.
     */
    suspend fun updateEmergencyNumber(emergencyNumber: EmergencyNumberEntity): Result<Int> {
        val emergencyNumberDetails = emergencyNumber.toString()
        val emergencyNumberId = emergencyNumber.countryIsoCode + emergencyNumber.emergencyNumber
        return try {
            val attempt = LogMessageUtils.attempting(
                "update",
                entityType,
                emergencyNumberId,
                emergencyNumberDetails
            )
            Log.i(tag, attempt)
            val rowsUpdated = emergencyNumberDao.updateEmergencyNumber(emergencyNumber)
            if (rowsUpdated > 0) {
                val success = LogMessageUtils.success(
                    "update",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                Log.i(tag, success)
                Result.success(rowsUpdated)
            } else {
                val failure = LogMessageUtils.failure(
                    "update",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                val exception =
                    Exception("Failed to update emergency number, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation(
                "update",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "update",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Deletes an emergency number from the database.
     *
     * This function attempts to delete the provided [EmergencyNumberEntity] from the database.
     * It logs the attempt, success, or failure of the operation.
     *
     * @param emergencyNumber The [EmergencyNumberEntity] to be deleted.
     * @return A [Result] object containing the number of rows deleted if successful (should be 1),
     *         or an [Exception] if the deletion fails.
     *         Possible failure reasons include:
     *         - The DAO operation returns an invalid result (e.g., 0, indicating the number was not found or not deleted).
     *         - A [SQLiteConstraintException] occurs, which is less common for deletes but might indicate
     *           related data preventing deletion if foreign key constraints are involved.
     *         - Any other unexpected [Exception] during the deletion process.
     */
    suspend fun deleteEmergencyNumber(emergencyNumber: EmergencyNumberEntity): Result<Int> {
        val emergencyNumberDetails = emergencyNumber.toString()
        val emergencyNumberId = emergencyNumber.countryIsoCode + emergencyNumber.emergencyNumber
        return try {
            val attempt = LogMessageUtils.attempting(
                "delete",
                entityType,
                emergencyNumberId,
                emergencyNumberDetails
            )
            Log.i(tag, attempt)
            val rowsDeleted = emergencyNumberDao.deleteEmergencyNumber(emergencyNumber)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success(
                    "delete",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure(
                    "delete",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                val exception =
                    Exception("Failed to delete emergency number, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation(
                "delete",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "delete",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a flow of emergency numbers for a specific country.
     *
     * This function queries the database for all emergency numbers associated with the given
     * ISO country code. It returns a [Flow] that emits a list of [EmergencyNumberEntity] objects.
     * If an error occurs during the database query, it logs the error and emits an empty list
     * to ensure the flow does not terminate with an exception, allowing the UI to handle
     * the absence of data gracefully.
     *
     * @param isoCode The ISO country code (e.g., "US", "GB") for which to retrieve emergency numbers.
     * @return A [Flow] that emits a list of [EmergencyNumberEntity] objects.
     *         Emits an empty list if no numbers are found or if an error occurs.
     */
    fun getEmergencyNumbersByCountry(isoCode: String): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByCountry(isoCode)
            .catch { e ->
                val errorMessage = "Error fetching emergency numbers by country"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Retrieves a flow of emergency numbers associated with a specific service ID.
     *
     * This function queries the database for all [EmergencyNumberEntity] instances
     * that are linked to the given [serviceId]. It returns a [Flow] that will emit
     * a list of these emergency numbers.
     *
     * If an error occurs during the database query, it logs the error and emits an empty list
     * to ensure the flow does not terminate due to the exception.
     *
     * @param serviceId The ID of the service for which to retrieve emergency numbers.
     * @return A [Flow] emitting a list of [EmergencyNumberEntity] objects.
     *         Emits an empty list if no numbers are found or if an error occurs.
     */
    fun getEmergencyNumbersByService(serviceId: Int): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByService(serviceId)
            .catch { e ->
                val errorMessage = "Error fetching emergency numbers by service"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Retrieves a flow of emergency numbers for a specific country and service.
     *
     * This function queries the database for emergency numbers matching the given ISO country code
     * and service ID. It returns a [Flow] that emits a list of [EmergencyNumberEntity] objects.
     *
     * If an error occurs during the database query, it logs the error and emits an empty list
     * to ensure the flow does not terminate with an exception.
     *
     * @param isoCode The ISO country code (e.g., "US", "GB") to filter emergency numbers by.
     * @param serviceId The ID of the emergency service (e.g., police, fire, ambulance) to filter by.
     * @return A [Flow] emitting a list of [EmergencyNumberEntity] objects matching the criteria.
     *         Emits an empty list if no numbers are found or if an error occurs.
     */
    fun getEmergencyNumbersByCountryAndService(isoCode: String, serviceId: Int): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByCountryAndService(isoCode, serviceId)
            .catch { e ->
                val errorMessage = "Error fetching emergency numbers by country and service"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Deletes all emergency numbers associated with a specific country from the database.
     *
     * This function attempts to delete all [EmergencyNumberEntity] entries that match the provided
     * ISO country code. It logs the attempt, success, or failure of the operation.
     *
     * @param isoCode The ISO country code (e.g., "US", "GB") for which all emergency numbers
     *                should be deleted.
     * @return A [Result] object containing the number of rows deleted if successful. This can be 0
     *         if no emergency numbers were found for the given country, or a positive integer
     *         representing the count of deleted entries.
     *         It returns an [Exception] if the deletion fails.
     *         Possible failure reasons include:
     *         - The DAO operation returns an invalid result (e.g., a negative value, though 0 is acceptable
     *           if no matching entries were found).
     *         - Any other unexpected [Exception] during the deletion process.
     */
    suspend fun deleteEmergencyNumbersByCountry(isoCode: String): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, isoCode, "")
            Log.i(tag, attempt)
            val rowsDeleted = emergencyNumberDao.deleteEmergencyNumbersByCountry(isoCode)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, isoCode, "")
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, isoCode, "")
                val exception = Exception("Failed to delete emergency numbers, DAO returned invalid result.")
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
     * Deletes all emergency numbers associated with a specific service ID from the database.
     *
     * This function attempts to delete all emergency number records that are linked to the given [serviceId].
     * It logs the attempt, success, or failure of the operation.
     *
     * @param serviceId The ID of the service whose emergency numbers are to be deleted.
     * @return A [Result] object containing the number of rows deleted if successful.
     *         This can be 0 if no emergency numbers were associated with the given service ID.
     *         Returns an [Exception] if the deletion fails.
     *         Possible failure reasons include:
     *         - The DAO operation returns a negative result, indicating an error.
     *         - Any other unexpected [Exception] during the deletion process.
     */
    suspend fun deleteEmergencyNumbersByService(serviceId: Int): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, serviceId.toString(), "")
            Log.i(tag, attempt)
            val rowsDeleted = emergencyNumberDao.deleteEmergencyNumbersByService(serviceId)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, serviceId.toString(), "")
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, serviceId.toString(), "")
                val exception = Exception("Failed to delete emergency numbers, DAO returned invalid result.")
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
     * Deletes all emergency numbers from the database.
     *
     * **Note: This function is intended for testing or debugging purposes only.**
     * **It should not be used in production code as it will clear all emergency number data.**
     *
     * This function attempts to delete all entries from the emergency numbers table in the database.
     * It logs the attempt, success, or failure of the operation.
     *
     * @return A [Result] object containing the total number of rows deleted if successful,
     *         or an [Exception] if the deletion fails.
     *         Possible failure reasons include:
     *         - The DAO operation returns a negative value, indicating an error. Note that if no rows
     *           are deleted because the table was already empty, this is considered a success and
     *           will return 0.
     *         - Any unexpected [Exception] during the deletion process.
     */
    internal suspend fun deleteAllEmergencyNumbers(): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, "", "")
            Log.i(tag, attempt)
            val rowsDeleted = emergencyNumberDao.deleteAllEmergencyNumbers()
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, "", "")
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, "", "")
                val exception = Exception("Failed to delete emergency numbers, DAO returned invalid result.")
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