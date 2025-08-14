package com.heavystudio.helpabroad.utils.permissionchecker

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import javax.inject.Inject

class AndroidSystemPermissionChecker @Inject constructor(
    private val application: Application
) : SystemPermissionChecker {
    override fun isPermissionGranted(permission: String): Boolean {
        val checkSelfPermission = ContextCompat.checkSelfPermission(application, permission)
        return checkSelfPermission == PermissionChecker.PERMISSION_GRANTED
    }

    override fun shouldShowRationale(permission: String): Boolean {
        return false
    }

}