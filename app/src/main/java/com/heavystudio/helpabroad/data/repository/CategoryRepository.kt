package com.heavystudio.helpabroad.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.database.CategoryEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    private val entityType = "Category"
    private val tag = "CategoryRepository"

    /**
     * Creates a new category in the database.
     *
     * This function attempts to insert the provided [CategoryEntity] into the database. It logs
     * debug information before the insertion attempt. If the insertion is successful, it returns a
     * [Result] containing the new row ID. If the DAO returns an invalid ID (<= 0), it returns a
     * [Result.failure] with a generic exception. If an [SQLiteConstraintException] occurs (e.g.,
     * due to a unique constraint violation), it logs an error with details of the category being
     * inserted and returns a [Result.failure] with the original exception. For any other [Exception],
     * it logs an error with details of the category and returns a [Result.failure] with the original
     * exception.
     *
     * @param category The [CategoryEntity] object to be inserted into the database.
     * @return A [Result] object which is either [Result.success] containing the new row ID (Long)
     *         if the insertion was successful, or [Result.failure] containing an [Exception] if an
     *         error occurred.
     */
    suspend fun createCategory(category: CategoryEntity): Result<Long> {
        val categoryDetails = category.toString()
        return try {
            Log.d(tag, LogMessageUtils.attempting(
                "insert", entityType, category.id, categoryDetails)
            )
            val newId = categoryDao.insertCategory(category)
            if (newId > 0) {
                Result.success(newId)
            } else {
                Result.failure(Exception("Failed to insert category, DAO returned invalid ID"))
            }
        } catch (e: SQLiteConstraintException) {
            Log.e(tag, LogMessageUtils.constraintViolation(
                "insert", entityType, categoryDetails), e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(tag, LogMessageUtils.unknownError(
                "insert", entityType, categoryDetails), e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a category by its ID.
     *
     * This function attempts to fetch a [CategoryEntity] from the database based on the provided ID.
     * If an error occurs during the fetch operation, it logs the error and emits `null`.
     *
     * @param id The ID of the category to retrieve.
     * @return A [Flow] emitting the [CategoryEntity] if found, or `null` if not found or an error occurs.
     */
    fun getCategoryById(id: Int): Flow<CategoryEntity?> {
        return categoryDao.getCategoryById(id)
            .catch { e ->
                Log.e(tag, "Error fetching category by ID: $id", e)
                emit(null)
            }
    }

    /**
     * Retrieves a flow of category lists based on their deletable status.
     *
     * This function queries the database for categories that either can or cannot be deleted,
     * as specified by the [canBeDeleted] parameter. It returns a [Flow] that emits a list of
     * [CategoryEntity] objects.
     *
     * In case of an error during the database query, it logs an error message including the
     * value of [canBeDeleted] and emits an empty list to the flow, ensuring the flow does
     * not terminate due to the exception.
     *
     * @param canBeDeleted A boolean indicating whether to fetch categories that can be deleted
     *                     (`true`) or categories that cannot be deleted (`false`).
     * @return A [Flow] that emits a list of [CategoryEntity] objects matching the deletable status.
     *         Emits an empty list if an error occurs.
     */
    fun getCategoriesByDeletableStatus(canBeDeleted: Boolean): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesByDeletableStatus(canBeDeleted)
            .catch { e ->
                val errorMessage = "Error fetching categories by deletable status (canBeDeleted = $canBeDeleted)"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    // TODO: Think about keeping those getters or removing them since it's just calling the
    // TODO: above function with a hard-coded argument
    // --- Specific getters for predefined and custom categories ---
    fun getPredefinedCategories(): Flow<List<CategoryEntity>> {
        return getCategoriesByDeletableStatus(canBeDeleted = false)
    }

    fun getCustomCategories(): Flow<List<CategoryEntity>> {
        return getCategoriesByDeletableStatus(canBeDeleted = true)
    }

    /**
     * Retrieves all categories from the database as a [Flow] of [List] of [CategoryEntity].
     *
     * This function calls the DAO method `getAllCategories()` to fetch all category records.
     * It uses a `catch` operator to handle any exceptions that might occur during the database
     * operation. If an error occurs, it logs an error message with the tag "CategoryRepository"
     * and emits an empty list to the Flow, ensuring the Flow does not terminate due to the error.
     *
     * @return A [Flow] that emits a list of [CategoryEntity] objects. If an error occurs during
     *         data retrieval, the Flow will emit an empty list.
     */
    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
            .catch { e ->
                val errorMessage = "Error fetching all categories"
                Log.e(tag, errorMessage, e)
                emit(emptyList())
            }
    }

    /**
     * Updates an existing category in the database.
     *
     * This function attempts to update the provided [CategoryEntity] in the database. It logs
     * debug information before the update attempt. If the update is successful (i.e., one or more
     * rows are affected), it returns a [Result] containing the number of rows updated. If the DAO
     * returns 0, indicating no rows were updated, it returns a [Result.failure] with a generic
     * exception. If an [SQLiteConstraintException] occurs (e.g., due to a unique constraint
     * violation), it logs an error with details of the category being updated and returns a
     * [Result.failure] with the original exception. For any other [Exception], it logs an error
     * with details of the category and returns a [Result.failure] with the original exception.
     *
     * @param category The [CategoryEntity] object to be updated in the database.
     * @return A [Result] object which is either [Result.success] containing the number of rows
     *         updated (Int) if the update was successful, or [Result.failure] containing an
     *         [Exception] if an error occurred.
     */
    suspend fun updateCategory(category: CategoryEntity): Result<Int> {
        val categoryDetails = category.toString()
        return try {
            val message = LogMessageUtils.attempting("update", entityType, category.id, categoryDetails)
            Log.d(tag, message)
            val rowsUpdated = categoryDao.updateCategory(category)
            if (rowsUpdated > 0) {
                val message = LogMessageUtils.success("update", entityType, category.id, categoryDetails)
                Log.i(tag, message)
                Result.success(rowsUpdated)
            } else {
                val message = LogMessageUtils.failure("update", entityType, category.id, categoryDetails)
                Log.w(tag, message)
                Result.failure(Exception("Failed to update category, DAO returned invalid ID"))
            }
        } catch (e: SQLiteConstraintException) {
            val message = LogMessageUtils.constraintViolation("update", entityType, categoryDetails)
            Log.e(tag, message, e)
            Result.failure(e)
        } catch (e: Exception) {
            val message = LogMessageUtils.unknownError("update", entityType, categoryDetails)
            Log.e(tag, message, e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a category from the database.
     *
     * This function attempts to delete the provided [CategoryEntity] from the database. It logs
     * debug information before the deletion attempt. It then calls [deleteCategoryById] with the
     * ID of the provided category.
     *
     * @param category The [CategoryEntity] object to be deleted from the database.
     * @return A [Result] object which is either [Result.success] containing the number of rows
     *         deleted (Int) if the deletion was successful, or [Result.failure] containing an
     *         [Exception] if an error occurred.
     */
    suspend fun deleteCategory(category: CategoryEntity): Result<Int> {
        val message = LogMessageUtils.attempting("delete", entityType, category.id)
        Log.d(tag,message)
        return deleteCategoryById(category.id)
    }

    /**
     * Deletes a category from the database by its ID.
     *
     * This function attempts to delete the category with the provided [id] from the database. It logs
     * debug information before the deletion attempt. If the deletion is successful (i.e., one or more
     * rows are deleted), it returns a [Result.success] containing the number of rows deleted. If the
     * DAO indicates that no rows were deleted (e.g., the ID was not found), it logs a warning and
     * returns a [Result.success] with 0, as this is not strictly an error but an indication that
     * the target was not present. If any other [Exception] occurs during the process, it logs an
     * error with details of the category ID being deleted and returns a [Result.failure] with the
     * original exception.
     *
     * @param id The ID of the category to be deleted.
     * @return A [Result] object which is either [Result.success] containing the number of rows
     *         deleted (Int), or [Result.failure] containing an [Exception] if an
     *         error occurred. A successful deletion of a non-existent category will result in
     *         `Result.failure`.
     */
    suspend fun deleteCategoryById(id: Int): Result<Int> {
        return try {
            val attemptM = LogMessageUtils.attempting("delete", entityType, id)
            Log.d(tag, attemptM)
            val rowsDeleted = categoryDao.deleteCategoryById(id)
            if (rowsDeleted > 0) {
                val successM = LogMessageUtils.success("delete", entityType, id)
                Log.i(tag, successM)
                Result.success(rowsDeleted)
            } else {
                val failureM = LogMessageUtils.failure("delete", entityType, id)
                val exception = Exception("Failed to delete category, DAO returned invalid ID")
                Log.w(tag, failureM)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownEM = LogMessageUtils.unknownError("delete", entityType, id.toString())
            Log.e(tag, unknownEM, e)
            Result.failure(e)
        }
    }
}