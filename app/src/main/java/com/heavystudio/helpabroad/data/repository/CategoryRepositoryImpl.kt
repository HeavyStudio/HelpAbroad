package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.CategoryDao
import com.heavystudio.helpabroad.data.database.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    override suspend fun updateCategory(category: CategoryEntity) {
        return categoryDao.updateCategory(category)
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        return categoryDao.deleteCategory(category)
    }

    override suspend fun deleteCategoryById(catId: Int): Int {
        return categoryDao.deleteCategoryById(catId)
    }

    override suspend fun deleteAllCategories() {
        return categoryDao.deleteAllCategories()
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override fun getPredefinedCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getPredefinedCategories()
    }

    override fun getCustomCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getCustomCategories()
    }

}