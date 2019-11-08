package com.app.raffaellatran.falldetectorlibrary.data.service

import com.app.raffaellatran.falldetectorlibrary.data.database.FallDetectorDao
import com.app.raffaellatran.falldetectorlibrary.data.model.FallDetectorModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FallDetectorRepository @Inject constructor(private val fallDetectorDao: FallDetectorDao) {
    fun addFall(fallDetector: FallDetectorModel) {
        Observable.fromCallable {
            fallDetectorDao.insert(fallDetector)
        }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getAll(): Observable<List<FallDetectorModel>> {
        return fallDetectorDao.getAll()
    }
}