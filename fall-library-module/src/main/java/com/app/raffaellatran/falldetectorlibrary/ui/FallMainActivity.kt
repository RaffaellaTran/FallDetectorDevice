package com.app.raffaellatran.falldetectorlibrary.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.raffaellatran.falldetectorlibrary.R

abstract class FallMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fall_activity_main)

    }
}
