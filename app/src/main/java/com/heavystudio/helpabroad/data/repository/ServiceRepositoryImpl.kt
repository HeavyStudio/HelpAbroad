package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.dao.ServiceDao
import com.heavystudio.helpabroad.data.database.ServiceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao
) : ServiceRepository {

    override suspend fun insertService(service: ServiceEntity): Long {
        return serviceDao.insertService(service)
    }

    override suspend fun insertServices(services: List<ServiceEntity>) {
        return serviceDao.insertServices(services)
    }

    override suspend fun getServiceById(senId: Int): ServiceEntity? {
        return serviceDao.getServiceById(senId)
    }

    override fun getAllServices(): Flow<List<ServiceEntity>> {
        return serviceDao.getAllServices()
    }

    override suspend fun updateService(service: ServiceEntity) {
        return serviceDao.updateService(service)
    }

    override suspend fun deleteService(service: ServiceEntity) {
        return serviceDao.deleteService(service)
    }

    override suspend fun deleteServiceById(senId: Int) {
        return serviceDao.deleteServiceById(senId)
    }

    override suspend fun deleteAllServices() {
        return serviceDao.deleteAllServices()
    }
}