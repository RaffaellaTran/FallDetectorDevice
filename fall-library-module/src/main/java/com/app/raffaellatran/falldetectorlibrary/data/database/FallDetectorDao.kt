package com.app.raffaellatran.falldetectorlibrary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.raffaellatran.falldetectorlibrary.data.model.FallDetectorModel
import io.reactivex.Observable

@Dao
interface FallDetectorDao {
    @Query("SELECT DISTINCT * FROM fallDetectorDatabase")
    fun getAll(): Observable<List<FallDetectorModel>>

    @Insert
    fun insert(fallDetectorModel: FallDetectorModel)
}