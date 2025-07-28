package com.heavystudio.helpabroad.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heavystudio.helpabroad.data.database.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    /**
     * Retrieves a category from the cat_categories table by its ID.
     *
     * @param catId The ID of the category to retrieve.
     * @return The CategoryEntity if found, or null otherwise.
     */
    @Query("SELECT * FROM cat_categories WHERE cat_id = :catId")
    suspend fun getCategoryById(catId: Int): CategoryEntity?

    /**
     * Retrieves all categories from the database, ordered first by whether they are predefined (predefined first),
     * then by their name resource ID (ascending), and finally by their custom name (ascending).
     *
     * @return A [Flow] emitting a list of [CategoryEntity] objects.
     */
    @Query("SELECT * FROM cat_categories ORDER BY cat_is_predefined DESC, cat_name_res_key ASC, cat_custom_name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Retrieves all predefined categories from the database, ordered by their name resource ID.
     * Predefined categories are those with `cat_is_predefined` set to 1.
     *
     * @return A [Flow] emitting a list of [CategoryEntity] objects representing the predefined categories.
     *         The list is ordered alphabetically by the string resource ID of the category name.
     */
    @Query("SELECT * FROM cat_categories WHERE cat_is_predefined = 1 ORDER BY cat_name_res_key ASC")
    fun getPredefinedCategories(): Flow<List<CategoryEntity>>

    /**
     * Retrieves all custom categories from the database, ordered by their custom name in ascending order.
     * Custom categories are identified by `cat_is_predefined = 0`.
     *
     * @return A [Flow] emitting a list of [CategoryEntity] objects representing custom categories.
     */
    @Query("SELECT * FROM cat_categories WHERE cat_is_predefined = 0 ORDER BY cat_custom_name ASC")
    fun getCustomCategories(): Flow<List<CategoryEntity>>

    /**
     * Deletes a category from the database by its ID.
     *
     * @param catId The ID of the category to delete.
     * @return The number of rows affected.
     */
    @Query("DELETE FROM cat_categories WHERE cat_id = :catId")
    suspend fun deleteCategoryById(catId: Int): Int

    /**
     * Deletes all categories from the cat_categories table.
     */
    @Query("DELETE FROM cat_categories")
    suspend fun deleteAllCategories()

}