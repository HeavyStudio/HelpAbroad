package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.ServiceDao
import com.heavystudio.helpabroad.data.database.ServiceEntity
import com.heavystudio.helpabroad.utils.catchAndLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(
    private val serviceDao: ServiceDao
) {

    private val tag = "ServiceRepository"

    fun getAllServices(): Flow<List<ServiceEntity>> {
        return serviceDao.getAllServices()
            .catchAndLog(
                tag = tag,
                errorMessage = "Error fetching all services",
                defaultValue = emptyList()
            )
    }

    fun getServiceById(id: Int): Flow<ServiceEntity?> {
        return serviceDao.getServiceById(id)
            .catchAndLog(
                tag = tag,
                errorMessage = "Error fetching service by ID: $id",
                defaultValue = null
            )
    }
}