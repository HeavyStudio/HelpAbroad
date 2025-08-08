package com.heavystudio.helpabroad.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.database.CategoryEntity
import com.heavystudio.helpabroad.data.database.CountryEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    private val entityType = "Category"
    private val tag = "CategoryRepository"

    fun getCategoryById(id: Int): Flow<CategoryEntity?> {
        return categoryDao.getCategoryById(id)
            .catch { error ->
                Log.e(tag, "Error fetching category by ID: $id", error)
                emit(null)
            }
    }

    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
            .catch { error ->
                Log.e(tag, "Error fetching all categories", error)
                emit(emptyList())
            }
    }
}