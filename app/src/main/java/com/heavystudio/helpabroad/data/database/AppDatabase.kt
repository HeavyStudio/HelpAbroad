package com.heavystudio.helpabroad.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
}