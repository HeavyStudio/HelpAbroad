package com.heavystudio.helpabroad.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.heavystudio.helpabroad.data.dao.EmergencyNumberDao
import com.heavystudio.helpabroad.data.database.EmergencyNumberEntity
import com.heavystudio.helpabroad.utils.LogMessageUtils
import com.heavystudio.helpabroad.utils.catchAndLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyNumberRepository @Inject constructor(private val emergencyNumberDao: EmergencyNumberDao) {

    private val entityType = "EmergencyNumber"
    private val tag = "EmergencyNumberRepository"

    suspend fun insertEmergencyNumber(emergencyNumber: EmergencyNumberEntity): Result<Long> {
        val emergencyNumberDetails = emergencyNumber.toString()
        val emergencyNumberId = emergencyNumber.countryIsoCode + emergencyNumber.emergencyNumber
        return try {
            val attempt = LogMessageUtils.attempting(
                "insert", entityType,
                emergencyNumberId,
                emergencyNumberDetails
            )
            Log.i(tag, attempt)
            val newEmergencyNumberId = emergencyNumberDao.insertEmergencyNumber(emergencyNumber)
            if (newEmergencyNumberId > 0) {
                val success = LogMessageUtils.success(
                    "insert",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                Log.i(tag, success)
                Result.success(newEmergencyNumberId)
            } else {
                val failure = LogMessageUtils.failure(
                    "insert",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                val exception = Exception("Failed to insert emergency number, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation(
                "insert",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "insert",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    suspend fun updateEmergencyNumber(emergencyNumber: EmergencyNumberEntity): Result<Int> {
        val emergencyNumberDetails = emergencyNumber.toString()
        val emergencyNumberId = emergencyNumber.countryIsoCode + emergencyNumber.emergencyNumber
        return try {
            val attempt = LogMessageUtils.attempting(
                "update",
                entityType,
                emergencyNumberId,
                emergencyNumberDetails
            )
            Log.i(tag, attempt)
            val rowsUpdated = emergencyNumberDao.updateEmergencyNumber(emergencyNumber)
            if (rowsUpdated > 0) {
                val success = LogMessageUtils.success(
                    "update",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                Log.i(tag, success)
                Result.success(rowsUpdated)
            } else {
                val failure = LogMessageUtils.failure(
                    "update",
                    entityType,
                    emergencyNumberId,
                    emergencyNumberDetails
                )
                val exception =
                    Exception("Failed to update emergency number, DAO returned invalid result.")
                Log.w(tag, failure)
                Result.failure(exception)
            }
        } catch (e: SQLiteConstraintException) {
            val constraintViolation = LogMessageUtils.constraintViolation(
                "update",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, constraintViolation, e)
            Result.failure(e)
        } catch (e: Exception) {
            val unknownError = LogMessageUtils.unknownError(
                "update",
                entityType,
                emergencyNumberDetails
            )
            Log.e(tag, unknownError, e)
            Result.failure(e)
        }
    }

    fun getEmergencyNumbersByCountry(isoCode: String): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByCountry(isoCode)
            .catchAndLog(tag, "Error fetching emergency numbers by country", emptyList())
    }

    fun getEmergencyNumbersByService(serviceId: Int): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByService(serviceId)
            .catchAndLog(tag, "Error fetching emergency numbers by service", emptyList())
    }

    fun getEmergencyNumbersByCountryAndService(isoCode: String, serviceId: Int): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getEmergencyNumbersByCountryAndService(isoCode, serviceId)
            .catchAndLog(tag, "Error fetching emergency numbers by country and service", emptyList())
    }

    fun getSmsSupportedEmergencyNumbersByCountry(isoCode: String): Flow<List<EmergencyNumberEntity>> {
        return emergencyNumberDao.getSmsSupportedEmergencyNumbersByCountry(isoCode)
            .catchAndLog(tag, "Error fetching SMS-supported emergency numbers by country", emptyList())
    }
}