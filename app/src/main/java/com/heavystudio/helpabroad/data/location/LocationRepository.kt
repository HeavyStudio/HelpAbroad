package com.heavystudio.helpabroad.data.location

import android.location.Location

interface LocationRepository {
    suspend fun tryGetQuickLocation(timeoutMillis: Long = 2000L): Location?
}