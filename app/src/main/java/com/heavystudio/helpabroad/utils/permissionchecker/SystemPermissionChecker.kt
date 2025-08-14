package com.heavystudio.helpabroad.utils.permissionchecker

interface SystemPermissionChecker {
    fun isPermissionGranted(permission: String): Boolean
    fun shouldShowRationale(permission: String): Boolean
}