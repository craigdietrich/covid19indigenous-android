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
import com.bumptech.glide.Glide
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.model.CultureVo
import java.io.File


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


        val file = File(Constant.culturePath(), data.thumbnailFilename)

        if (file.exists()) {
            Glide.with(context)
                .load(file)
                .into(holder.img)
        } else {
            Glide.with(context)
                .load(Constant.BASE_MEDIA_URL + data.thumbnailFilename)
                .into(holder.img)
        }


        holder.llMain.setOnClickListener {
            clickListener!!.onItemClick(data)
        }
    }

    private var clickListener: ClickListener? = null

    fun setOnItemClickListener(clickListener1: ClickListener?) {
        clickListener = clickListener1
    }

    interface ClickListener {
        fun onItemClick(data: CultureVo)
    }
}