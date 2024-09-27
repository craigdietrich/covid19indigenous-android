package com.craigdietrich.covid19indigenous.ui.pastSubmissions

import androidx.lifecycle.MutableLiveData
import com.craigdietrich.covid19indigenous.common.Constant.Companion.submissionsPath
import com.craigdietrich.covid19indigenous.model.PastVo
import java.io.File

object PastSubmissions {

    val ITEMS: MutableList<PastVo> = mutableListOf()

    val selectionEnable: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        val files = submissionsPath().listFiles()
        files?.forEach(::addItem)
    }

    fun resetData() {
        ITEMS.clear()
        val files = submissionsPath().listFiles()
        files?.forEach(::addItem)
    }

    private fun addItem(item: File) {
        if (item.name.startsWith("local")) {
            val model = PastVo(
                name = item.name,
                file = item,
                modified = item.lastModified(),
            )
            ITEMS.add(model)
        }
    }
}