package com.heavystudio.helpabroad.data.repository

import com.heavystudio.helpabroad.data.database.PersonalEmergencyNumberEntity
import kotlinx.coroutines.flow.Flow

interface PersonalEmergencyNumberRepository {

    /**
     * Inserts a new personal emergency number into the database.
     *
     * @param personalNumber The [PersonalEmergencyNumberEntity] to be inserted.
     * @return The row ID of the newly inserted personal emergency number, or -1 if an error occurred.
     */
    suspend fun insertPersonalNumber(personalNumber: PersonalEmergencyNumberEntity): Long

    /**
     * Retrieves a personal emergency number from the data source by its ID.
     *
     * @param penId The ID of the personal emergency number to retrieve.
     * @return The [PersonalEmergencyNumberEntity] if found, or null otherwise.
     */
    suspend fun getPersonalNumberById(penId: Int): PersonalEmergencyNumberEntity?

    /**
     * Retrieves a flow of lists of personal emergency numbers for a specific country.
     *
     * @param countryIsoCode The ISO code of the country to filter by.
     * @return A [Flow] emitting a list of [PersonalEmergencyNumberEntity] objects matching the country.
     */
    fun getPersonalNumbersByCountry(countryIsoCode: String): Flow<List<PersonalEmergencyNumberEntity>>

    /**
     * Retrieves a flow of personal emergency numbers that belong to a specific category.
     *
     * @param categoryId The ID of the category.
     * @return A [Flow] emitting a list of [PersonalEmergencyNumberEntity] objects.
     */
    fun getPersonalNumbersByCategory(categoryId: Int): Flow<List<PersonalEmergencyNumberEntity>>

    /**
     * Retrieves a flow of personal emergency numbers that are categorized as "uncategorized".
     *
     * @return A [Flow] emitting a list of [PersonalEmergencyNumberEntity] objects.
     */
    fun getUncategorizedPersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>>

    /**
     * Retrieves a flow of personal emergency numbers that are categorized as "favorite".
     *
     * @return A [Flow] emitting a list of [PersonalEmergencyNumberEntity] objects.
     */
    fun getFavoritePersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>>

    /**
     * Retrieves a flow of all personal emergency numbers.
     *
     * @return A [Flow] emitting a list of [PersonalEmergencyNumberEntity] objects.
     */
    fun getAllPersonalNumbers(): Flow<List<PersonalEmergencyNumberEntity>>

    /**
     * Retrieves the count of personal emergency numbers.
     *
     * @return The count of personal emergency numbers.
     */
    suspend fun getPersonalNumbersCount(): Int

    /**
     * Retrieves the count of favorite personal emergency numbers.
     *
     * @return The count of favorite personal emergency numbers.
     */
    suspend fun getFavoritePersonalNumbersCount(): Int

    /**
     * Updates an existing personal emergency number in the data source.
     *
     * @param personalNumber The [PersonalEmergencyNumberEntity] to update.
     */
    suspend fun updatePersonalNumber(personalNumber: PersonalEmergencyNumberEntity)

    /**
     * Sets the favorite status of a personal emergency number.
     *
     * @param penId The ID of the personal emergency number.
     * @param isFavorite The new favorite status.
     */
    suspend fun setFavoriteStatus(penId: Int, isFavorite: Boolean)

    /**
     * Deletes a personal emergency number from the data source.
     *
     * @param personalNumber The personal emergency number entity to delete.
     */
    suspend fun deletePersonalNumber(personalNumber: PersonalEmergencyNumberEntity)

    /**
     * Deletes a personal emergency number by its ID.
     *
     * @param penId The ID of the personal emergency number to delete.
     */
    suspend fun deletePersonalNumberById(penId: Int)

    /**
     * Deletes all personal emergency numbers from the data source.
     */
    suspend fun deleteAllPersonalNumbers()
}