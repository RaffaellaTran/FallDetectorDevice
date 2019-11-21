package com.app.raffaellatran.fall_detector.ui

import com.app.raffaellatran.falldetectorlibrary.data.model.FallModel

interface FallView {
    fun showFallList(fallList: ArrayList<FallModel>)
    fun showEmptyList()
    fun showErrorLoadingData()
}