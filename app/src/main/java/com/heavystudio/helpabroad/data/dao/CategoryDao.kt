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

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategories(vararg categories: CategoryEntity): List<Long>

    @Update
    suspend fun updateCategory(category: CategoryEntity): Int

    @Delete
    suspend fun deleteCategory(category: CategoryEntity): Int

    // --- Queries ---

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Int): Flow<CategoryEntity?>

    @Query("SELECT * FROM categories ORDER BY name_res_key ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>
}