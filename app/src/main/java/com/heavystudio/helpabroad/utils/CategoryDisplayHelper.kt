package com.heavystudio.helpabroad.utils

import android.content.Context
import android.content.res.Resources
import com.heavystudio.helpabroad.data.database.CategoryEntity

object CategoryDisplayHelper {

    fun getDisplayName(category: CategoryEntity, context: Context): String {
        if (category.isPredefined && category.nameResId != null) {
            return try {
                context.getString(category.nameResId)
            } catch (e: Resources.NotFoundException) {
                category.customName ?: "Category (ID: ${category.id})"
            }
        } else {
            return category.customName ?: "Unnamed Category (ID: ${category.id})"
        }
    }
}