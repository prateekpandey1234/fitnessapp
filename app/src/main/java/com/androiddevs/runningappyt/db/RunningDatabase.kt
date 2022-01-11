package com.androiddevs.runningappyt.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],
    version = 1
)
//Room Database is a part of the Android Architecture components
// which provides an abstraction layer over SQLite which allows for
// more robust database access while still providing the full power of SQLite.
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDAO
}