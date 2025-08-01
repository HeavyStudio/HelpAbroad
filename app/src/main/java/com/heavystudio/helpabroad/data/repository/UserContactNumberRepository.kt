package com.heavystudio.helpabroad.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.UserContactNumberDao
import com.heavystudio.helpabroad.data.database.UserContactNumberEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserContactNumberRepository @Inject constructor(private val userContactNumberDao: UserContactNumberDao) {

    private val entityType = "UserContactNumber"
    private val tag = "UserContactNumberRepository"

    /**
     * Inserts a new user contact number into the database.
     *
     * This function attempts to insert the provided [userContactNumber] into the local database
     * using the `userContactNumberDao`. It logs the attempt, success, or failure of the operation.
     *
     * @param userContactNumber The [UserContactNumberEntity] to be inserted.
     * @return A [Result] object:
     *   - On success, it contains the ID of the newly inserted user contact number ([Long]).
     *   - On failure, it contains an [Exception] detailing the error.
     *     - This can be a [SQLiteConstraintException] if a database constraint is violated
     *       (e.g., trying to insert a duplicate entry).
     *     - It can be a generic [Exception] if the DAO returns an invalid result (ID <= 0)
     *       or if an unexpected error occurs during the insertion process.
     */
    suspend fun insertUserContactNumber(userContactNumber: UserContactNumberEntity): Result<Long> {
        val userContactNumberDetails = userContactNumber.toString()

        return try {
            val attempt = LogMessageUtils.attempting(
                "insert", entityType,
                userContactNumber.id,
                userContactNumberDetails
            )
            Log.i(tag, attempt)
            val newUserContactNumberId = userContactNumberDao.insertUserContactNumber(userContactNumber)
            if (newUserContactNumberId > 0) {
                val success = LogMessageUtils.success(
                    "insert", entityType,
                    userContactNumber.id,
                    userContactNumberDetails
                )
                Log.i(tag, success)
                Result.success(newUserContactNumberId)
            } else {
                val failure = LogMessageUtils.failure(
                    "insert", entityType,
                    userContactNumber.id,
                    userContactNumberDetails
                )
                val exception = Exception("Failed to insert user contact number, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation(
                "insert", entityType,
                userContactNumberDetails
            )
            Log.e(tag, constraintViolation)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "insert", entityType,
                userContactNumberDetails
            )
            Log.e(tag, unknownError)
            Result.failure(e)
        }
    }

    /**
     * Updates an existing user contact number in the local database.
     *
     * This function attempts to update the provided [UserContactNumberEntity] in the
     * `userContactNumberDao`. It logs the attempt, success, or failure of the operation.
     *
     * @param userContactNumber The [UserContactNumberEntity] object containing the updated
     * contact number details. The `id` of this entity must match an existing record in the database.
     * @return A [Result] object:
     *         - On success: [Result.success] containing the number of rows updated (should be 1 if successful).
     *         - On failure: [Result.failure] containing an [Exception] indicating the cause of the failure.
     *           This can be a [SQLiteConstraintException] if a database constraint is violated,
     *           or a generic [Exception] if the DAO returns an unexpected result or another error occurs.
     */
    suspend fun updateUserContactNumber(userContactNumber: UserContactNumberEntity): Result<Int> {
        val userContactNumberDetails = userContactNumber.toString()

        return try {
            val attempt = LogMessageUtils.attempting(
                "update", entityType,
                userContactNumber.id,
                userContactNumberDetails
            )
            Log.i(tag, attempt)
            val rowsUpdated = userContactNumberDao.updateUserContactNumber(userContactNumber)
            if (rowsUpdated > 0) {
                val success = LogMessageUtils.success(
                    "update", entityType,
                    userContactNumber.id,
                    userContactNumberDetails
                )
                Log.i(tag, success)
                Result.success(rowsUpdated)
            } else {
                val failure = LogMessageUtils.failure(
                    "update", entityType,
                    userContactNumber.id,
                    userContactNumberDetails
                )
                val exception = Exception("Failed to update user contact number, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation(
                "update", entityType,
                userContactNumberDetails
            )
            Log.e(tag, constraintViolation)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "update", entityType,
                userContactNumberDetails
            )
            Log.e(tag, unknownError)
            Result.failure(e)
        }
    }

    /**
     * Deletes a user contact number from the local database.
     *
     * This function attempts to delete the provided [UserContactNumberEntity] from the
     * `userContactNumberDao`. It logs the attempt, success, or failure of the deletion
     * process.
     *
     * @param userContactNumber The [UserContactNumberEntity] to be deleted.
     * @return A [Result] object containing:
     *         - [Result.success] with the number of rows deleted (should be 1 if successful).
     *         - [Result.failure] with an [Exception] if the deletion fails. This can be
     *           due to a database error (e.g., [SQLiteConstraintException]) or if the DAO
     *           reports that no rows were deleted.
     */
    suspend fun deleteUserContactNumber(userContactNumber: UserContactNumberEntity): Result<Int> {
        val userContactNumberDetails = userContactNumber.toString()

        return try {
            val attempt = LogMessageUtils.attempting(
                "delete", entityType,
                userContactNumber.id,
                userContactNumberDetails
            )
            Log.i(tag, attempt)
            val rowsDeleted = userContactNumberDao.deleteUserContactNumber(userContactNumber)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success(
                    "delete", entityType,
                    userContactNumber.id,
                    userContactNumberDetails
                )
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure(
                    "delete", entityType,
                    userContactNumber.id,
                    userContactNumberDetails
                    )
                val exception = Exception("Failed to delete user contact number, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation(
                "delete", entityType,
                userContactNumberDetails
            )
            Log.e(tag, constraintViolation)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "delete", entityType,
                userContactNumberDetails
            )
            Log.e(tag, unknownError)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a user's contact number by their ID.
     *
     * This function fetches the contact number from the local database (userContactNumberDao).
     * If an error occurs during the fetch operation, it logs the error and emits `null`.
     *
     * @param id The ID of the user whose contact number is to be retrieved.
     * @return A Flow emitting the [UserContactNumberEntity] if found, or `null` if not found or an error occurs.
     */
    fun getUserContactNumberById(id: Int): Flow<UserContactNumberEntity?> {
        return userContactNumberDao.getUserContactNumberById(id)
            .catch { e ->
                val errorMessage = "Error fetching user contact by ID: $id"
                Log.e(tag, errorMessage, e)
                emit(null)
            }
    }

    /**
     * Retrieves a list of user contact numbers based on the provided country ISO code.
     *
     * This function queries the local database via `userContactNumberDao` to find all
     * `UserContactNumberEntity` objects associated with the given `isoCode`.
     *
     * In case of an error during the database operation, an error message is logged,
     * and an empty list is emitted to the Flow, ensuring the stream does not terminate
     * abruptly.
     *
     * @param isoCode The ISO 3166-1 alpha-2 country code (e.g., "US", "GB") to filter
     *                user contact numbers by.
     * @return A [Flow] emitting a list of [UserContactNumberEntity] objects matching the
     *         specified country ISO code. Emits an empty list if no contacts are found
     *         or if an error occurs.
     */
    fun getUserContactNumberByCountry(isoCode: String): Flow<List<UserContactNumberEntity>> {
        return userContactNumberDao.getUserContactNumbersByCountry(isoCode)
            .catch { e ->
                val errorMessage = "Error fetching user contact numbers by country"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Retrieves a flow of lists of user contact numbers associated with a specific service ID.
     *
     * This function queries the local database via `userContactNumberDao` to find all
     * `UserContactNumberEntity` objects that match the provided `serviceId`.
     *
     * If an error occurs during the database query, it logs the error and emits an empty list
     * to prevent the flow from terminating due to an exception.
     *
     * @param serviceId The ID of the service for which to retrieve contact numbers.
     * @return A [Flow] emitting a list of [UserContactNumberEntity] objects.
     *         Emits an empty list if no numbers are found or if an error occurs.
     */
    fun getUserContactNumberByService(serviceId: Int): Flow<List<UserContactNumberEntity>> {
        return userContactNumberDao.getUserContactNumbersByService(serviceId)
            .catch { e ->
                val errorMessage = "Error fetching numbers by service: $serviceId"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Retrieves a list of user contact numbers based on the specified country ISO code and service ID.
     *
     * This function queries the local database for contact numbers associated with a particular country
     * (identified by its ISO code) and a specific service (identified by its ID).
     * It returns a [Flow] that emits a list of [UserContactNumberEntity] objects.
     *
     * In case of an error during the database query, an error message is logged, and the Flow
     * will emit an empty list.
     *
     * @param isoCode The ISO code of the country to filter by.
     * @param serviceId The ID of the service to filter by.
     * @return A [Flow] emitting a list of [UserContactNumberEntity] objects that match the criteria,
     * or an empty list if no matches are found or an error occurs.
     */
    fun getUserContactNumberByCountryAndService(isoCode: String, serviceId: Int): Flow<List<UserContactNumberEntity>> {
        return userContactNumberDao.getUserContactNumbersByCountryAndService(isoCode, serviceId)
            .catch { e ->
                val errorMessage = "Error fetching user contacts by country and service"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Deletes a user contact number from the local database.
     *
     * @param userContactNumber The [UserContactNumberEntity] to be deleted.
     * @return A [Result] indicating the success or failure of the deletion.
     *         On success, it contains the number of rows affected (should be 1).
     *         On failure, it contains an [Exception].
     */
    suspend fun deleteUserContact(userContactNumber: UserContactNumberEntity): Result<Int> {
        val message = LogMessageUtils.attempting(
            "delete", entityType,
            userContactNumber.id
        )
        Log.d(tag, message)
        return deleteUserContactNumber(userContactNumber)
    }

    /**
     * Deletes a user contact by its ID from the local database.
     *
     * This function attempts to delete a user contact number entry from the
     * `userContactNumberDao`. It logs the attempt, success, or failure of the
     * operation.
     *
     * @param userContactId The ID of the user contact to delete.
     * @return A [Result] object:
     *         - On success, it contains the number of rows deleted (should be 1 if successful).
     *         - On failure, it contains an [Exception] detailing the error. This can occur
     *           if the DAO operation fails (e.g., no rows were deleted) or if an unexpected
     *           exception occurs during the process.
     */
    suspend fun deleteUserContactById(userContactId: Int): Result<Int> {
        val userContactDetails = userContactId.toString()
        return try {
            val attempt = LogMessageUtils.attempting(
                "delete", entityType, userContactId
            )
            Log.i(tag, attempt)
            val rowsDeleted = userContactNumberDao.deleteUserContactNumberById(userContactId)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success(
                    "delete", entityType, userContactId
                )
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure(
                    "delete", entityType, userContactId
                )
                val exception = Exception("Failed to delete user contact, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "delete", entityType, userContactDetails
            )
            Log.e(tag, unknownError)
            Result.failure(e)
        }
    }

    /**
     * Deletes user contact numbers associated with a specific country identified by its ISO code.
     *
     * This function attempts to delete all user contact numbers from the local database
     * where the country matches the provided `isoCode`.
     *
     * It logs the attempt, success, or failure of the deletion operation.
     *
     * @param isoCode The ISO code of the country for which user contact numbers should be deleted.
     * @return A [Result] object:
     *         - On success, it contains the number of rows (user contact numbers) deleted.
     *         - On failure (e.g., if the DAO operation returns an invalid result or an unexpected exception occurs),
     *           it contains an [Exception] detailing the error.
     */
    suspend fun deleteUserContactByCountry(isoCode: String): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting(
                "delete", entityType, isoCode
            )
            Log.i(tag, attempt)
            val rowsDeleted = userContactNumberDao.deleteUserContactNumbersByCountry(isoCode)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success(
                    "delete", entityType, isoCode
                )
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure(
                    "delete", entityType, isoCode
                )
                val exception = Exception("Failed to delete user contact, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "delete", entityType, isoCode
            )
            Log.e(tag, unknownError)
            Result.failure(e)
        }
    }

    /**
     * Deletes user contact numbers associated with a specific service.
     *
     * This function attempts to delete all user contact numbers linked to the provided `serviceId`
     * from the local database using the `userContactNumberDao`.
     *
     * It logs the attempt, success, or failure of the deletion process.
     *
     * @param serviceId The ID of the service whose associated user contact numbers are to be deleted.
     * @return A [Result] object:
     *         - On success: Contains the number of rows (user contact numbers) deleted.
     *         - On failure: Contains an [Exception] detailing the error. This can occur if the
     *           DAO operation fails or if an unexpected exception is caught during the process.
     */
    suspend fun deleteUserContactByService(serviceId: Int): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting(
                "delete", entityType, serviceId
            )
            Log.i(tag, attempt)
            val rowsDeleted = userContactNumberDao.deleteUserContactNumbersByService(serviceId)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success(
                    "delete", entityType, serviceId
                )
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure(
                    "delete", entityType, serviceId
                )
                val exception = Exception("Failed to delete user contact, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "delete", entityType, ""
            )
            Log.e(tag, unknownError)
            Result.failure(e)
        }
    }

    /**
     * Deletes all user contact numbers from the database.
     *
     * @return A [Result] object containing the number of rows deleted on success,
     * or an exception on failure.
     */
    suspend fun deleteAllUserContactNumbers(): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, "", "")
            Log.i(tag, attempt)
            val rowsDeleted = userContactNumberDao.deleteAllUserContactNumbers()
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, "", "")
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, "", "")
                val exception = Exception("Failed to delete all user contact numbers, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("delete", entityType, "")
            Log.e(tag, unknownError)
            Result.failure(e)
        }
    }
}