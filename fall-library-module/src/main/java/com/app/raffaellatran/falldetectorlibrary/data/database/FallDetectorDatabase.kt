package com.app.raffaellatran.falldetectorlibrary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.raffaellatran.falldetectorlibrary.data.model.Converters
import com.app.raffaellatran.falldetectorlibrary.data.model.FallDetectorModel

@Database(entities = [FallDetectorModel::class], version = 9)
@TypeConverters(Converters::class)
abstract class FallDetectorDatabase : RoomDatabase() {
    abstract fun fallDao(): FallDetectorDao
}