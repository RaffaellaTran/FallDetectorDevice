package com.app.raffaellatran.fall_detector.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.raffaellatran.fall_detector.R
import com.app.raffaellatran.falldetectorlibrary.data.model.FallModel
import kotlinx.android.synthetic.main.adapter_detector.view.*
import java.time.format.DateTimeFormatter

class DetectorRecycleAdapter :
    RecyclerView.Adapter<DetectorRecycleAdapter.MyViewHolder>() {

    private var fallList = ArrayList<FallModel>()
    private var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_detector, parent, false)
        return MyViewHolder(view)
    }

    fun setFallList(fallList: List<FallModel>) {
        this.fallList.clear()
        this.fallList.addAll(fallList)
    }

    override fun getItemCount() = fallList.count()

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val fallItem = fallList[position]
        holder.dateName.text = fallItem.fallDate.format(formatter)
        holder.durationName.text = fallItem.fallDuration.toString()
    }

    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val durationName: TextView = v.duration
        val dateName: TextView = v.date
    }
}
