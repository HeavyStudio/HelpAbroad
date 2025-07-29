package com.heavystudio.helpabroad.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.database.CategoryEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

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
        val entityType = "Category"
        val categoryDetails = category.toString()
        return try {
            Log.d("CategoryRepository", LogMessageUtils.attempting(
                "insert", entityType, category.id, categoryDetails)
            )
            val newId = categoryDao.insertCategory(category)
            if (newId > 0) {
                Result.success(newId)
            } else {
                Result.failure(Exception("Failed to insert category, DAO returned invalid ID"))
            }
        } catch (e: SQLiteConstraintException) {
            Log.e("CategoryRepository", LogMessageUtils.constraintViolation(
                "insert", entityType, categoryDetails), e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("CategoryRepository", LogMessageUtils.unknownError(
                "insert", entityType, categoryDetails), e)
            Result.failure(e)
        }
    }
}