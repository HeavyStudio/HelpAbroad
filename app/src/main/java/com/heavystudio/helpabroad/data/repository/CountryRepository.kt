package com.heavystudio.helpabroad.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.CountryDao
import com.heavystudio.helpabroad.data.database.CountryEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepository @Inject constructor(private val countryDao: CountryDao) {

    private val entityType = "Country"
    private val tag = "CountryRepository"

    /**
     * Inserts a new country into the database.
     *
     * This function attempts to insert the provided [CountryEntity] into the database.
     * It logs the attempt, success, or failure of the operation.
     *
     * @param country The [CountryEntity] to be inserted.
     * @return A [Result] object.
     *         - On success, it contains the row ID of the newly inserted country (a [Long] value
     *         greater than 0).
     *         - On failure, it contains an [Exception] indicating the cause of the error.
     *           This could be an [SQLiteConstraintException] if a database constraint is violated
     *           (e.g., trying to insert a country with an ISO code that already exists),
     *           or a generic [Exception] for other errors, such as the DAO returning an invalid result
     *           or an unexpected issue during the database operation.
     */
    suspend fun createCountry(country: CountryEntity): Result<Long> {
        val countryDetails = country.toString()
        return try {
            val attempt = LogMessageUtils.attempting("insert", entityType, country.isoCode, countryDetails
            )
            Log.d(tag, attempt)
            val newCountry = countryDao.insertCountry(country)
            if (newCountry > 0) {
                val success = LogMessageUtils.success("insert", entityType, country.isoCode, countryDetails)
                Log.i(tag, success)
                Result.success(newCountry)
            } else {
                val failure = LogMessageUtils.failure("insert", entityType, country.isoCode, countryDetails)
                val exception = Exception("Failed to insert country, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation("insert", entityType, countryDetails)
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("insert", entityType, countryDetails)
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Updates an existing country in the local database.
     *
     * This function attempts to update the provided [CountryEntity] in the `countryDao`.
     * It logs the attempt, success, or failure of the operation.
     *
     * @param country The [CountryEntity] object to be updated.
     * @return A [Result] object containing:
     *         - [Result.success] with the number of rows updated if the operation was successful.
     *         - [Result.failure] with an [Exception] if the DAO returned an invalid result (0 rows updated).
     *         - [Result.failure] with an [SQLiteConstraintException] if a database constraint was violated.
     *         - [Result.failure] with a generic [Exception] for any other errors during the operation.
     */
    suspend fun updateCountry(country: CountryEntity): Result<Int> {
        val countryDetails = country.toString()
        return try {
            val attempt = LogMessageUtils.attempting("update", entityType, country.isoCode, countryDetails)
            Log.d(tag, attempt)
            val rowsUpdated = countryDao.updateCountry(country)
            if (rowsUpdated > 0) {
                val success = LogMessageUtils.success("update", entityType, country.isoCode, countryDetails)
                Log.i(tag, success)
                Result.success(rowsUpdated)
            } else {
                val failure = LogMessageUtils.failure("update", entityType, country.isoCode, countryDetails)
                val exception = Exception("Failed to update country, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation("update", entityType, countryDetails)
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("update", entityType, countryDetails)
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a country from the local database.
     *
     * This function attempts to delete the specified [CountryEntity] from the local Room database
     * using the `countryDao`. It logs the attempt, success, or failure of the deletion process.
     *
     * @param country The [CountryEntity] object to be deleted.
     * @return A [Result] object containing:
     *         - [Result.Success] with the number of rows deleted (should be 1 if successful)
     *         - [Result.Failure] with an [Exception] if the deletion fails or an error occurs.
     *           This can be due to the DAO returning 0 rows deleted or an unexpected exception
     *           during the database operation.
     */
    suspend fun deleteCountry(country: CountryEntity): Result<Int> {
        val countryDetails = country.toString()
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, country.isoCode, countryDetails)
            Log.d(tag, attempt)
            val rowsDeleted = countryDao.deleteCountry(country)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, country.isoCode, countryDetails)
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, country.isoCode, countryDetails)
                val exception = Exception("Failed to delete country, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("delete", entityType, countryDetails)
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a country from the local database by its ISO code.
     *
     * @param isoCode The ISO code of the country to retrieve.
     * @return A Flow emitting the [CountryEntity] if found, or null if not found or an error occurs.
     *         Errors during database access are logged and null is emitted.
     */
    fun getCountryByIsoCode(isoCode: String): Flow<CountryEntity?> {
        return countryDao.getCountryByIsoCode(isoCode)
            .catch { e ->
                Log.e(tag, "Error fetching country by ISO code: $isoCode", e)
                emit(null)
            }
    }

    /**
     * Retrieves a flow of lists of countries that are members of the 112 emergency number dispatch.
     *
     * This function fetches the data from the local database via `countryDao.get112Countries()`.
     * In case of an error during the fetching process, it logs the error and emits an empty list
     * to prevent the flow from crashing.
     *
     * @return A [Flow] emitting a list of [CountryEntity] objects.
     *         The emitted list will be empty if an error occurs during data retrieval.
     */
    fun getCountriesMember112(): Flow<List<CountryEntity>> {
        return countryDao.get112Countries()
            .catch { e ->
                val errorMessage = "Error fetching countries members of 112 dispatch"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Retrieves a list of countries that are members of the 911 dispatch system.
     *
     * This function fetches data from the `countryDao`.
     * If an error occurs during the fetching process, it logs the error and emits an empty list.
     *
     * @return A [Flow] emitting a list of [CountryEntity] objects representing countries
     *         that are members of the 911 dispatch system.
     */
    fun getCountriesMember911(): Flow<List<CountryEntity>> {
        return countryDao.get911Countries()
            .catch { e ->
                val errorMessage = "Error fetching countries members of 911 dispatch"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Retrieves all countries from the local database.
     *
     * This function fetches all `CountryEntity` objects stored in the `countryDao`.
     * If an error occurs during the database operation, it logs the error
     * and emits an empty list to prevent the Flow from terminating with an exception.
     *
     * @return A [Flow] emitting a list of [CountryEntity] objects.
     *         Emits an empty list if an error occurs.
     */
    fun getAllCountries(): Flow<List<CountryEntity>> {
        return countryDao.getAllCountries()
            .catch { e ->
                val errorMessage = "Error fetching all countries"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Deletes a country from the database by its ISO code.
     *
     * @param isoCode The ISO code of the country to delete.
     * @return A [Result] object containing the number of rows deleted on success, or an exception on failure.
     *         - On successful deletion (rowsDeleted > 0), returns [Result.success] with the number of rows deleted.
     *         - If the DAO operation indicates failure (rowsDeleted <= 0), returns [Result.failure] with an [Exception].
     *         - If any other exception occurs during the process, returns [Result.failure] with the caught [Exception].
     *         Logs appropriate messages for attempt, success, failure, and unknown errors.
     */
    suspend fun deleteCountryByIsoCode(isoCode: String): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, isoCode)
            Log.d(tag, attempt)
            val rowsDeleted = countryDao.deleteCountryByIsoCode(isoCode)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, isoCode)
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, isoCode)
                val exception = Exception("Failed to delete country, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("delete", entityType, isoCode)
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }
}