package com.craigdietrich.covid19indigenous.ui.pastSubmissions

import java.io.File

interface OnItemClickListener {
    fun onShare(file: File)
    fun onPreview(path: String)
}