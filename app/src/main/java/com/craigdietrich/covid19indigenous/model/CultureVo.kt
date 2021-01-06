package com.craigdietrich.covid19indigenous.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CultureVo {
    @SerializedName("title")
    @Expose
    var title: String = ""

    @SerializedName("url")
    @Expose
    var url: String = ""

    @SerializedName("mp4_filename")
    @Expose
    var mp4Filename: String = ""

    @SerializedName("image_filename")
    @Expose
    var imageFilename: String = ""

    @SerializedName("thumbnail_filename")
    @Expose
    var thumbnailFilename: String = ""

    @SerializedName("description")
    @Expose
    var description: String = ""

    @SerializedName("category")
    @Expose
    var category: String = ""

    @SerializedName("date")
    @Expose
    var date: String = ""

    @SerializedName("link")
    @Expose
    var link: String = ""

}