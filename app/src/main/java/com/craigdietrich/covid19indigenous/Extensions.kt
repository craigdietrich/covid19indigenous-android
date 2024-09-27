package com.craigdietrich.covid19indigenous

import android.content.res.Configuration
import android.content.res.Resources
import android.os.StrictMode
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun String.loadFile(): InputStream? {
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    val url = URL(this)
    val connection: HttpURLConnection?
    try {
        connection = url.openConnection() as HttpURLConnection
        connection.connect()
        val inputStream: InputStream = connection.inputStream
        return BufferedInputStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}

val Resources.isLandscape
    get(): Boolean {
        val orientation: Int = this.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }

fun InputStream.writeToFile(file: File) {
    file.outputStream().use { output ->
        copyTo(output)
    }
}