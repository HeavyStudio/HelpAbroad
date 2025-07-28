package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.database.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    /**
     * Inserts a new category into the database.
     *
     * @param category The category to be inserted.
     * @return The ID of the newly inserted category.
     */
    suspend fun insertCategory(category: CategoryEntity): Long

    /**
     * Retrieves all categories from the repository.
     *
     * @return A Flow emitting a list of [CategoryEntity] objects.
     */
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Retrieves a flow of lists containing predefined category entities.
     *
     * Predefined categories are those that are built into the application
     * and are not created by the user.
     *
     * @return A [Flow] that emits a list of [CategoryEntity] objects representing predefined categories.
     */
    fun getPredefinedCategories(): Flow<List<CategoryEntity>>

    /**
     * Retrieves a flow of all custom categories.
     *
     * Custom categories are those created by the user.
     *
     * @return A Flow emitting a list of [CategoryEntity] objects representing custom categories.
     */
    fun getCustomCategories(): Flow<List<CategoryEntity>>

    /**
     * Updates an existing category in the database.
     *
     * @param category The category entity to be updated. The entity should have its ID set to the ID of the category to update.
     */
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * Deletes a category from the database.
     *
     * @param category The category to be deleted.
     */
    suspend fun deleteCategory(category: CategoryEntity)

    /**
     * Deletes a category from the database by its ID.
     *
     * @param catId The ID of the category to be deleted.
     * @return The number of rows affected by the delete operation.
     */
    suspend fun deleteCategoryById(catId: Int): Int

    /**
     * Deletes all categories from the database.
     */
    suspend fun deleteAllCategories()
}