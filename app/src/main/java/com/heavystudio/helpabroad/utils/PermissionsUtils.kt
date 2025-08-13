package com.heavystudio.helpabroad.utils

import android.content.Context
import android.content.pm.PackageManager

fun checkAndRequestPermissions(context: Context, permissions: List<String>): Map<String, Boolean> {
    return permissions.associateWith { permission ->
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}