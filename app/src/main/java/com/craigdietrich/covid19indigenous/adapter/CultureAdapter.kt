package com.craigdietrich.covid19indigenous.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.model.CultureVo

class CultureAdapter(private val context: Context, private val data: ArrayList<CultureVo>) :
    RecyclerView.Adapter<CultureAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_culture, parent, false)
        return MyViewHolder(itemView)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtDesc: TextView = view.findViewById(R.id.txtDesc)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
        var llMain: LinearLayout = view.findViewById(R.id.llMain)
        var img: ImageView = view.findViewById(R.id.img)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: CultureVo = data[position]

        holder.txtTitle.text = data.title
        holder.txtDesc.text = data.description
        holder.txtDate.text = "Published: " + data.date
        //holder.im = data.title

        //Glide.with(context).load("https://covid19indigenous.ca/feeds/content/E4-Kahkakiw-Straight-Talk-Kids-and-Coronaviru.png").into(holder.img);

        holder.llMain.setOnClickListener {
            clickListener!!.onAppointmentClicked(data)
        }
    }

    private var clickListener: ClickListener? = null

    fun setOnItemClickListener(clickListener1: ClickListener?) {
        clickListener = clickListener1
    }

    interface ClickListener {
        fun onAppointmentClicked(data: CultureVo)
    }
}