package com.app.raffaellatran.fall_detector

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.raffaellatran.fall_detector.presenter.FallDetectorPresenter
import com.app.raffaellatran.fall_detector.ui.DetectorRecycleAdapter
import com.app.raffaellatran.fall_detector.ui.FallDetectorView
import com.app.raffaellatran.falldetectorlibrary.data.model.FallDetectorModel
import com.app.raffaellatran.falldetectorlibrary.data.service.FallDetectorService
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), FallDetectorView {
    private lateinit var serviceIntent: Intent
    private lateinit var detectorPresenter: FallDetectorPresenter
    private lateinit var recyclerAdapter: DetectorRecycleAdapter
    private var fallBinder: FallDetectorService.FallDetectorServiceBinder? = null
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is FallDetectorService.FallDetectorServiceBinder) {
                fallBinder = service
                setupViews(service.fallList)
            }
        }
    }

    init {
        Timber.plant(Timber.DebugTree())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serviceIntent = Intent(this, FallDetectorService::class.java)

        serviceIntent.putExtra(FallDetectorService.EXTRA_ACTIVITY_CLASS, MainActivity::class.java)

        this.startForegroundService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        fallBinder?.let {
            unbindService(connection)
            fallBinder = null
        }
    }

    override fun onDestroy() {
        detectorPresenter.onDestroy()
        super.onDestroy()
    }

    override fun showFallList(fallList: ArrayList<FallDetectorModel>) {

        if (fallList.isEmpty()) {
            text_empty_list.visibility = View.VISIBLE
            text_title.visibility = View.GONE
            linearLayout.visibility = View.GONE
            recycler_view.visibility = View.GONE
        } else {
            text_empty_list.visibility = View.GONE
            text_title.visibility = View.VISIBLE
            linearLayout.visibility = View.VISIBLE
            recycler_view.visibility = View.VISIBLE

            recyclerAdapter.setFallList(fallList)
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    override fun showEmptyList() {

        text_empty_list.visibility = View.VISIBLE
        text_title.visibility = View.GONE
        linearLayout.visibility = View.GONE
        recycler_view.visibility = View.GONE
    }

    override fun showErrorLoadingData() {
        Toast.makeText(applicationContext, R.string.error_generic, Toast.LENGTH_SHORT).show()
    }

    private fun setupViews(fallList: Observable<List<FallDetectorModel>>) {
        detectorPresenter = FallDetectorPresenter(fallList)
        detectorPresenter.showTimeDurationFall()

        recycler_view.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = DetectorRecycleAdapter()
        recycler_view.adapter = recyclerAdapter

        detectorPresenter.onCreate(this)

        val dividerItemDecoration = DividerItemDecoration(
            recycler_view.context,
            DividerItemDecoration.VERTICAL
        )
        recycler_view.addItemDecoration(dividerItemDecoration)
    }
}
