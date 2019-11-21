package com.app.raffaellatran.falldetectorlibrary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.raffaellatran.falldetectorlibrary.data.model.FallModel
import io.reactivex.Observable

@Dao
interface FallDao {
    @Query("SELECT DISTINCT * FROM fallDatabase")
    fun getAll(): Observable<List<FallModel>>

    @Insert
    fun insert(fallModel: FallModel)
}