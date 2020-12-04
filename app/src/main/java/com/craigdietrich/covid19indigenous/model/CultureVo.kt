package com.craigdietrich.covid19indigenous.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CultureVo {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("mp4_filename")
    @Expose
    var mp4Filename: String? = null

    @SerializedName("image_filename")
    @Expose
    var imageFilename: String? = null

    @SerializedName("thumbnail_filename")
    @Expose
    var thumbnailFilename: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

}