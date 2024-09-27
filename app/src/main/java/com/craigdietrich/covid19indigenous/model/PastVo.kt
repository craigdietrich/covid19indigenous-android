package com.craigdietrich.covid19indigenous.model

import java.io.File

data class PastVo(
    val name: String,
    val file: File,
    val modified: Long,
    var selected: Boolean = false,
    var index: Int? = null,
)
