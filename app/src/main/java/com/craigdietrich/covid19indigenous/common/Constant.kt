package com.craigdietrich.covid19indigenous.common

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.model.AnswerVo
import com.craigdietrich.covid19indigenous.retrfit.GetApi
import com.craigdietrich.covid19indigenous.retrfit.RetrofitInstance
import com.craigdietrich.covid19indigenous.ui.culture.CulResFragment
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class Constant {
    companion object {

        const val BASE_URL = "https://ourdataindigenous.ca/"
        const val BASE_MEDIA_URL = "https://ourdataindigenous.ca/feeds/content/"

        private const val SHARE_PREF = "SHARE_PREF"

        const val CULTURE = "feeds/content/manifest.json?"
        const val QUESTIONS = "dashboard/pages/app?"
        const val ANSWERS = "dashboard/pages/handler"

        const val isAcceptSurvey = "isAcceptSurvey"
        const val surveyCode = "surveyCode"

        const val aboutSurveyPath = "file:///android_asset/aboutSurvey.html"
        const val indexSurveyPath = "file:///android_asset/common/index.html"
        const val consentSurveyPath = "file:///android_asset/consent.html"
        const val aboutProjectPath = "file:///android_asset/aboutProject.html"
        const val aboutUsPath = "file:///android_asset/aboutUs.html"

        var isfetch = false
        var fileType = "image"// for file picker in survey form

        var myTask: CulResFragment.DownloadFileFromURL? = null

        fun changeStatusBar(isDark: Boolean, context: Context, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = (context as Activity?)!!.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(context, color)
                if (isDark) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        val decorView: View = window.decorView
                        var systemUiVisibilityFlags = decorView.systemUiVisibility
                        systemUiVisibilityFlags =
                            systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                        decorView.systemUiVisibility = systemUiVisibilityFlags
                    }


                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                }
            }
        }

        fun isOnline(activity: AppCompatActivity): Boolean {
            val connectivityManager =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

                return when {

                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    else -> false
                }
            } else {
                return connectivityManager.activeNetworkInfo != null &&
                        connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting
            }
        }

        fun showAlert(context: Context, title: String, msg: String) {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }

        fun millsToRemainingHS(millis: Long): String {
            val minutes = millis / 1000 / 60
            val seconds = (millis / 1000 % 60).toInt()

            if (minutes <= 0 && seconds <= 0) {
                return String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
            }
            return "-" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
        }

        fun millsToHS(millis: Long): String {
            val minutes = millis / 1000 / 60
            val seconds = (millis / 1000 % 60).toInt()

            return String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
        }

        fun deleteCultureFiles(fileOrDirectory: File) {
            if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteCultureFiles(
                child
            )
            fileOrDirectory.delete()
        }

        fun deleteSurveyFiles(fileOrDirectory: File) {
            if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteSurveyFiles(
                child
            )
            fileOrDirectory.delete()
        }

        fun culturePath(c: Context): File {
            var dir = File(
                c.externalCacheDir!!.absolutePath,
                "/Covid19Indigenous"
            )
            if (!dir.exists()) {
                dir.mkdir()
            }
            dir = File(dir, "/Content")
            if (!dir.exists()) {
                dir.mkdir()
            }

            return dir
        }

        fun surveyPath(c: Context): File {
            var dir = File(
                c.externalCacheDir!!.absolutePath,
                "/Covid19Indigenous"
            )
            if (!dir.exists()) {
                dir.mkdir()
            }
            dir = File(dir, "/Survey")
            if (!dir.exists()) {
                dir.mkdir()
            }

            return dir
        }

        fun surveyFile(c: Context): File {
            return File(surveyPath(c), "questionnaires.json")
        }

        fun stringFromFile(file: File): StringBuilder {
            val text = StringBuilder()

            try {
                val br = BufferedReader(FileReader(file))
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
                br.close()
            } catch (e: IOException) {
                Log.e("error", e.toString())
            }

            return text
        }

        fun timeStamp(): String {
            val rnd = Random()
            val number: Int = rnd.nextInt(888889) + 111111
            return (System.currentTimeMillis() / 1000).toString() + "." + String.format(
                "%06d",
                number
            )
        }

        fun uploadingAnswerDialog(c: Context) {
            val dialog = Dialog(c, R.style.NewDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_upload)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent)

            val txtUploading: TextView = dialog.findViewById(R.id.txtUploading)
            val txtInfo: TextView = dialog.findViewById(R.id.txtInfo)

            txtInfo.text = c.getString(R.string.checking_internet)

            val answerFile = ArrayList<File>()
            var pastTitle = ""

            val files = surveyPath(c).listFiles()
            if (files != null) {
                for (i in files.indices) {
                    //Log.d("Files", "FileName:" + files[i].name)
                    if (files[i].name.startsWith("answer")) {
                        answerFile.add(files[i])

                        var name = files[i].name.substring(files[i].name.lastIndexOf("_") + 1)
                        name = name.substring(0, name.lastIndexOf("."))

                        name = "Past answers\n$name"
                        pastTitle += if (pastTitle == "") {
                            name
                        } else {
                            "\n\n" + name
                        }

                    }
                }

                if (answerFile.size > 0) {
                    Handler().postDelayed({

                        isfetch = false

                        if (isOnline(c as AppCompatActivity)) {
                            txtInfo.text = c.getString(R.string.uploading_answers)
                            txtUploading.text = pastTitle

                            for (i in answerFile.indices) {

                                runBlocking {
                                    val text = StringBuilder()

                                    try {
                                        val br = BufferedReader(FileReader(answerFile[i]))
                                        var line: String?
                                        while (br.readLine().also { line = it } != null) {
                                            text.append(line)
                                            text.append('\n')
                                        }
                                        br.close()
                                    } catch (e: IOException) {

                                    }

                                    val answer =
                                        Gson().fromJson(text.toString(), AnswerVo::class.java)

                                    val service: GetApi = RetrofitInstance.getRetrofitInstance()
                                        .create(GetApi::class.java)
                                    val call = service.uploadAnswer(answer)
                                    call.enqueue(object : Callback<AnswerVo> {
                                        override fun onResponse(
                                            call: Call<AnswerVo>,
                                            response: retrofit2.Response<AnswerVo>
                                        ) {
                                            Log.d("res", response.body().toString())

                                            try {
                                                response.body() as AnswerVo//if success than it converts

                                                answerFile[i].delete()
                                                Log.e("success", "success")
                                            } catch (e: java.lang.Exception) {
                                                Log.e("errorUploading", e.toString())
                                                answerFile[i].delete()
                                            }

                                            if (i == answerFile.size - 1) {
                                                dialog.dismiss()
                                            }
                                        }

                                        override fun onFailure(call: Call<AnswerVo>, t: Throwable) {
                                            Log.e("failUploading", t.toString())
                                            answerFile[i].delete()

                                            if (i == answerFile.size - 1) {
                                                dialog.dismiss()
                                            }
                                        }
                                    })
                                }
                            }
                        } else {
                            dialog.dismiss()
                        }
                    }, 3000)
                    dialog.show()
                }
            }

        }

        fun writeSP(context: Context, key: String?, values: String?) {
            val writeData = context.getSharedPreferences(
                SHARE_PREF, Context.MODE_PRIVATE
            )
            val editor = writeData.edit()
            editor.putString(key, values)
            editor.apply()
        }

        fun readSP(context: Context, key: String?): String {
            var readData: SharedPreferences? = null
            try {
                readData = context.getSharedPreferences(
                    SHARE_PREF, Context.MODE_PRIVATE
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return readData!!.getString(key, "").toString()
        }

        abstract class DoubleClickListener : View.OnClickListener {

            private val doubleClickTime: Long = 300 //milliseconds

            var lastClickTime: Long = 0
            override fun onClick(v: View?) {
                val clickTime = System.currentTimeMillis()
                if (clickTime - lastClickTime < doubleClickTime) {
                    onDoubleClick(v)
                } else {
                    onSingleClick(v)
                }
                lastClickTime = clickTime
            }

            abstract fun onSingleClick(v: View?)
            abstract fun onDoubleClick(v: View?)

        }
    }
}
