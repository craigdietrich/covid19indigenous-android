package com.craigdietrich.covid19indigenous

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

fun InputStream.writeToFile(file: File) {
    file.outputStream().use { output ->
        copyTo(output)
    }
}