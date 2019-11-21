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
import com.app.raffaellatran.fall_detector.presenter.FallPresenter
import com.app.raffaellatran.fall_detector.ui.DetectorRecycleAdapter
import com.app.raffaellatran.fall_detector.ui.FallView
import com.app.raffaellatran.falldetectorlibrary.data.model.FallModel
import com.app.raffaellatran.falldetectorlibrary.data.service.FallService
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), FallView {
    private lateinit var serviceIntent: Intent
    private lateinit var presenter: FallPresenter
    private lateinit var recyclerAdapter: DetectorRecycleAdapter
    private var fallBinder: FallService.FallDetectorServiceBinder? = null
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is FallService.FallDetectorServiceBinder) {
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

        serviceIntent = Intent(this, FallService::class.java)

        serviceIntent.putExtra(FallService.EXTRA_ACTIVITY_CLASS, MainActivity::class.java)

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
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun showFallList(fallList: ArrayList<FallModel>) {


        text_empty_list.visibility = View.GONE
        text_title.visibility = View.VISIBLE
        linearLayout.visibility = View.VISIBLE
        recycler_view.visibility = View.VISIBLE

        recyclerAdapter.setFallList(fallList)
        recyclerAdapter.notifyDataSetChanged()

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

    private fun setupViews(fallList: Observable<List<FallModel>>) {
        presenter = FallPresenter(fallList)
        presenter.showTimeDurationFall()

        recycler_view.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = DetectorRecycleAdapter()
        recycler_view.adapter = recyclerAdapter

        presenter.onCreate(this)

        val dividerItemDecoration = DividerItemDecoration(
            recycler_view.context,
            DividerItemDecoration.VERTICAL
        )
        recycler_view.addItemDecoration(dividerItemDecoration)
    }
}
