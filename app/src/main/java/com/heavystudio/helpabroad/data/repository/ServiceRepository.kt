package com.heavystudio.helpabroad.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.ServiceDao
import com.heavystudio.helpabroad.data.database.ServiceEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(private val serviceDao: ServiceDao) {

    private val entityType = "Service"
    private val tag = "ServiceRepository"

    suspend fun createService(service: ServiceEntity): Result<Long> {
        val serviceDetails = service.toString()
        return try {
            val attempt = LogMessageUtils.attempting("insert", entityType, service.id, serviceDetails)
            Log.d(tag, attempt)
            val newId = serviceDao.insertService(service)
            if (newId > 0) {
                val success = LogMessageUtils.success("insert", entityType, service.id, serviceDetails)
                Log.i(tag, success)
                Result.success(newId)
            } else {
                val failure = LogMessageUtils.failure("insert", entityType, service.id, serviceDetails)
                val exception = Exception("Failed to insert service, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation("insert", entityType, serviceDetails)
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("insert", entityType, serviceDetails)
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    // TODO: getServiceById
    // TODO: getAllServices
    // TODO: getServicesByCategoryId
    // TODO: getServicesByDeletableStatus
    // TODO: getPredefinedServices
    // TODO: getCustomServices
    // TODO: deleteServicesByCategory
    // TODO: deleteAllServices

    suspend fun updateService(service: ServiceEntity): Result<Int> {
        val serviceDetails = service.toString()
        return try {
            val attempt = LogMessageUtils.attempting("update", entityType, service.id, serviceDetails)
            Log.d(tag, attempt)
            val rowsUpdated = serviceDao.updateService(service)
            if (rowsUpdated > 0) {
                val success = LogMessageUtils.success("update", entityType, service.id, serviceDetails)
                Log.i(tag, success)
                Result.success(rowsUpdated)
            } else {
                val failure = LogMessageUtils.failure("update", entityType, service.id, serviceDetails)
                val exception = Exception("Failed to update service, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation("update", entityType, serviceDetails)
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("update", entityType, serviceDetails)
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    suspend fun deleteService(service: ServiceEntity): Result<Int> {
        val attempt = LogMessageUtils.attempting("delete", entityType, service.id, service.toString())
        Log.d(tag, attempt)
        return deleteServiceById(service.id)
    }

    suspend fun deleteServiceById(id: Int): Result<Int> {
        return try {
            val attempt = LogMessageUtils.attempting("delete", entityType, id, "")
            Log.d(tag, attempt)
            val rowsDeleted = serviceDao.deleteServiceById(id)
            if (rowsDeleted > 0) {
                val success = LogMessageUtils.success("delete", entityType, id, "")
                Log.i(tag, success)
                Result.success(rowsDeleted)
            } else {
                val failure = LogMessageUtils.failure("delete", entityType, id, "")
                val exception = Exception("Failed to delete service, DAO returned invalid result")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError("delete", entityType, "")
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }
}