package com.app.raffaellatran.fall_detector.ui

import com.app.raffaellatran.falldetectorlibrary.data.model.FallDetectorModel

interface FallDetectorView {
    fun showFallList(fallList: ArrayList<FallDetectorModel>)
    fun showEmptyList()
    fun showErrorLoadingData()
}