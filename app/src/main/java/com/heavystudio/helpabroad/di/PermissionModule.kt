package com.heavystudio.helpabroad.di

import com.heavystudio.helpabroad.utils.permissionchecker.AndroidSystemPermissionChecker
import com.heavystudio.helpabroad.utils.permissionchecker.SystemPermissionChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionModule {

    @Binds
    @Singleton
    abstract fun bindSystemPermissionChecker(
        androidSystemPermissionChecker: AndroidSystemPermissionChecker
    ): SystemPermissionChecker
}