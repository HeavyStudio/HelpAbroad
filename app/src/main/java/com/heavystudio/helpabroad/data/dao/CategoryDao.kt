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

    // --- Convenience Methods ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateCategory(category: CategoryEntity): Int

    @Delete
    suspend fun deleteCategory(category: CategoryEntity): Int

    // --- Queries ---
    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Int): Flow<CategoryEntity?>

    @Query("SELECT * FROM categories WHERE can_be_deleted = :canBeDeleted ORDER BY name_res_key ASC")
    fun getCategoriesByDeletableStatus(canBeDeleted: Boolean): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY name_res_key ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("DELETE FROM categories WHERE id = :id AND can_be_deleted = 1")
    suspend fun deleteCategoryById(id: Int): Int

    @Query("DELETE FROM categories WHERE can_be_deleted = 1")
    suspend fun deleteAllCategories(): Int
}