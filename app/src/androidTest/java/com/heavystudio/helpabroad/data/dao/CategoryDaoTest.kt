package com.heavystudio.helpabroad.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heavystudio.helpabroad.data.database.AppDatabase
import com.heavystudio.helpabroad.data.database.CategoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {
    private lateinit var categoryDao: CategoryDao
    private lateinit var db: AppDatabase

    private val category1 = CategoryEntity(1, "cat_one", "Custom 1", true)
    private val category2 = CategoryEntity(2, "cat_two", "Custom 2", true)
    private val category3 = CategoryEntity(3, "cat_three", "Custom 3", true)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        categoryDao = db.categoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertCategoryAndGetById() = runBlocking {
        categoryDao.insertCategory(category1)

        val retrievedCategory = categoryDao.getCategoryById(category1.id)
        assertNotNull("Retrieved category should not be null", retrievedCategory)
        assertEquals("ID", category1.id, retrievedCategory!!.id)
        assertEquals("nameResKey", category1.nameResKey, retrievedCategory.nameResKey)
        assertEquals("customName", category1.customName, retrievedCategory.customName)
        assertEquals("isPredefined", category1.isPredefined, retrievedCategory.isPredefined)
    }

    @Test
    @Throws(Exception::class)
    fun getCategory_nonExistentId_returnsNull() = runBlocking {
        val retrievedCategory = categoryDao.getCategoryById(666)
        assertNull("Category with non-existent ID should be null", retrievedCategory)
    }

    @Test
    @Throws(Exception::class)
    fun getAllCategories_multipleCategories_returnsAll() = runBlocking {
        categoryDao.insertCategory(category1)
        categoryDao.insertCategory(category2)
        categoryDao.insertCategory(category3)

        val allCategories = categoryDao.getAllCategories().first()
        assertEquals("Should be 3 categories", 3, allCategories.size)
        assertEquals("Category 1", category1, allCategories[0])
        assertEquals("Category 2", category2, allCategories[1])
        assertEquals("Category 3", category3, allCategories[2])
    }

    @Test
    @Throws(Exception::class)
    fun getAllCategories_emptyDatabase_returnsEmptyList() = runBlocking {
        val allCategories = categoryDao.getAllCategories().first()
        assertTrue("Category list should be empty", allCategories.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun updateCategory_changesExistingCategory() = runBlocking {
        categoryDao.insertCategory(category1)
        val updatedCategory = CategoryEntity(1, "updated_key", "Updated Custom", false)
        categoryDao.updateCategory(updatedCategory)

        val retrievedCategory = categoryDao.getCategoryById(1)
        assertNotNull(retrievedCategory)
        assertEquals("nameResKey should be updated", "updated_key", retrievedCategory!!.nameResKey)
        assertEquals("customName should be updated", "Updated Custom", retrievedCategory.customName)
        assertEquals("isPredefined should be updated", false, retrievedCategory.isPredefined)
    }

    @Test
    @Throws(Exception::class)
    fun updateCategory_nonExistent_doesNothingOrError() = runBlocking {
        categoryDao.updateCategory(category1)

        val retrievedCategory = categoryDao.getCategoryById(404)
        assertNull("Category should still be null as update shouldn't insert", retrievedCategory)

        val allCategories = categoryDao.getAllCategories().first()
        assertTrue("Database should remain empty or unchanged in count", allCategories.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun deleteCategory_removesCategoryFromDatabase() = runBlocking {
        categoryDao.insertCategory(category1)
        categoryDao.deleteCategory(category1)

        val retrievedCategory = categoryDao.getCategoryById(category1.id)
        assertNull("Deleted category should be null", retrievedCategory)

        val allCategories = categoryDao.getAllCategories().first()
        assertTrue("Database should be empty or have one less item", allCategories.none { it.id == category1.id })
    }

    @Test
    @Throws(Exception::class)
    fun deleteCategory_nonExistent_doesNothing() = runBlocking {
        categoryDao.insertCategory(category1)
        categoryDao.deleteCategory(category2)

        val allCategories = categoryDao.getAllCategories().first()
        assertEquals("Count should remain 1", 1, allCategories.size)
        assertNotNull("Other category should still exist", categoryDao.getCategoryById(category1.id))
    }
}