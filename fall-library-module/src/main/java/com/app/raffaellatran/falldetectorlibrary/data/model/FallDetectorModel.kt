package com.app.raffaellatran.falldetectorlibrary.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDateTime

@Entity(tableName = "fallDetectorDatabase")
data class FallDetectorModel(
    @PrimaryKey(autoGenerate = true) var fallId: Int = 0,
    @ColumnInfo(name = "fall_detector_date") val fallDate: LocalDateTime,
    @ColumnInfo(name = "fall_detector_duration") val fallDuration: Long
)

class Converters {

    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? = date?.toString()

}
