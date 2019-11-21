package com.app.raffaellatran.falldetectorlibrary.data.service

import com.app.raffaellatran.falldetectorlibrary.data.database.FallDao
import com.app.raffaellatran.falldetectorlibrary.data.model.FallModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FallRepository @Inject constructor(private val fallDao: FallDao) {
    fun addFall(fall: FallModel) {
        Observable.fromCallable {
            fallDao.insert(fall)
        }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getAll(): Observable<List<FallModel>> {
        return fallDao.getAll()
    }
}