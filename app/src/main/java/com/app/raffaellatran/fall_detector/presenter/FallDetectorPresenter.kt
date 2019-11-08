package com.app.raffaellatran.fall_detector.presenter

import com.app.raffaellatran.fall_detector.ui.FallDetectorView
import com.app.raffaellatran.falldetectorlibrary.data.model.FallDetectorModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class FallDetectorPresenter constructor(private val fallListObservable: Observable<List<FallDetectorModel>>) {

    private lateinit var view: FallDetectorView
    private val subscriptions = CompositeDisposable()

    fun onCreate(view: FallDetectorView) {
        this.view = view
    }

    fun showTimeDurationFall() {
        subscriptions.add(
            fallListObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ fallList ->
                    if (fallList.isEmpty()) {
                        view.showEmptyList()
                    } else {
                        view.showFallList(fallList as ArrayList<FallDetectorModel>)

                    }
                }, { throwable ->
                    Timber.e(throwable, "while loading data")
                    view.showErrorLoadingData()
                })
        )
    }

    fun onDestroy() {
        subscriptions.clear()
    }
}