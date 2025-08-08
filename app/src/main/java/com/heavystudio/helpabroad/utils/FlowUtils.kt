package com.heavystudio.helpabroad.utils

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.catchAndLog(
    tag: String,
    errorMessage: String,
    defaultValue: T
): Flow<T> = this.catch { error ->
    Log.e(tag, errorMessage, error)
    emit(defaultValue)
}