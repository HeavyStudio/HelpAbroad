package com.heavystudio.helpabroad.utils

object LogMessageUtils {

    // --- Exceptions ---
    fun constraintViolation(operation: String, entityType: String, entityDetails: String): String {
        return "A constraint violation occurred during $operation of $entityType:\n$entityDetails"
    }

    fun unknownError(operation: String, entityType: String, entityDetails: String): String {
        return "An unknown error occurred during $operation of $entityType:\n$entityDetails"
    }

    // --- Messages ---
    fun success(operation: String, entityType: String, entityId: Any?, details: String = ""): String {
        val idPart = entityId?.let { " with ID: $it" } ?: ""
        return "Successfully $operation $entityType$idPart. $details".trim()
    }

    fun failure(operation: String, entityType: String, entityId: Any?, details: String = "", reason: String = ""): String {
        val idPart = entityId?.let { " with ID: $it" } ?: ""
        return "Failed to $operation $entityType$idPart. Reason: $reason. Details: $details".trim()
    }

    fun attempting(operation: String, entityType: String, entityId: Any? = null, additionalInfo: String = ""): String {
        val idPart = entityId?.let { " with ID: $it" } ?: ""
        return "Attempting to $operation $entityType$idPart. ${additionalInfo.trim()}"
    }
}