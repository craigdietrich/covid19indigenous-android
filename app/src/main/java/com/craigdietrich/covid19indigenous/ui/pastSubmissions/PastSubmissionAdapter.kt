package com.craigdietrich.covid19indigenous.ui.pastSubmissions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant.Companion.convertToLocal
import com.craigdietrich.covid19indigenous.databinding.ItemPastSubmissionsBinding
import com.craigdietrich.covid19indigenous.model.PastVo
import java.util.*

class PastSubmissionAdapter(
    val values: MutableList<PastVo>,
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<PastSubmissionAdapter.ViewHolder>() {

    var selectionEnable: Boolean = false

    fun getSelectedFiles(): List<PastVo> {
        return values.filter { pastVo -> pastVo.selected }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPastSubmissionsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position], onItemClickListener, position)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemPastSubmissionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val root = binding.root
        private val contentView: TextView = binding.content
        private val preview: ImageView = binding.preview
        private val download: ImageView = binding.share

        fun bind(item: PastVo, onItemClickListener: OnItemClickListener, position: Int) {
            if (item.selected) {
                root.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.black_30
                    )
                )
            } else {
                root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.colorPrimary))
            }

            root.setOnLongClickListener {
                if (selectionEnable) return@setOnLongClickListener true
                selectionEnable = true
                PastSubmissions.selectionEnable.postValue(true)
                values[position].selected = true
                notifyItemChanged(position)
                true
            }

            root.setOnClickListener {
                if (selectionEnable) {
                    values[position].selected = !values[position].selected
                    notifyItemChanged(position)

                    val selectedAny = values.any { it.selected }
                    if (!selectedAny) {
                        selectionEnable = false
                        PastSubmissions.selectionEnable.postValue(false)
                    }
                }
            }

            val calender = Calendar.getInstance()
            calender.timeInMillis = item.modified
            val createdAt = calender.time
            contentView.text = contentView.context.getString(
                R.string.submitted_on,
                createdAt.convertToLocal(),
            )

            preview.setOnClickListener { onItemClickListener.onPreview(item.file.path) }
            download.setOnClickListener { onItemClickListener.onShare(item.file) }
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + " '"
        }
    }
}