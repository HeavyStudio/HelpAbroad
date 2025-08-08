package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.database.CategoryEntity
import com.heavystudio.helpabroad.utils.catchAndLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    private val entityType = "Category"
    private val tag = "CategoryRepository"

    fun getCategoryById(id: Int): Flow<CategoryEntity?> {
        return categoryDao.getCategoryById(id)
            .catchAndLog(tag, "Error fetching category by ID: $id", null)
    }

    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
            .catchAndLog(tag, "Error fetching all categories", emptyList())
    }
}