package com.craigdietrich.covid19indigenous.common

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.craigdietrich.covid19indigenous.Covid19Indigenous
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.model.AnswerVo
import com.craigdietrich.covid19indigenous.retrfit.GetApi
import com.craigdietrich.covid19indigenous.retrfit.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Constant {
    companion object {

        private const val FORMAT = "yyyy-MM-dd HH:mm:ss"

        const val BASE_URL = "https://ourdataindigenous.ca/"
        const val BASE_MEDIA_URL = "https://ourdataindigenous.ca/feeds/content/"

        private const val SHARE_PREF = "SHARE_PREF"

        const val CULTURE = "feeds/content/manifest.json?"
        const val QUESTIONS = "dashboard/pages/app?"
        const val ANSWERS = "dashboard/pages/handler"

        const val SEND_RESULTS_TO_SERVER = "sendResultsToServer"
        const val IS_ACCEPT_SURVEY = "isAcceptSurvey"
        const val SURVEY_CODE = "surveyCode"

        const val ABOUT_SURVEY_PATH = "aboutSurvey"
        const val INDEX_SURVEY_PATH = "common/index"
        const val CONSENT_SURVEY_PATH = "consent"
        const val ABOUT_PROJECT_PATH = "aboutProject"
        const val ABOUT_US_PATH = "aboutUs"
        private const val PREVIEW_PAST_SUBMISSION = "common/preview_past_submission.html"

        var isfetch = false

        var fileType = "photo"// for file picker in survey form

        fun String.convertToLocal(): String? {
            val formatter = SimpleDateFormat(FORMAT, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = formatter.parse(this)
            formatter.timeZone = TimeZone.getDefault()
            return date?.let(formatter::format)
        }

        fun Date.convertToLocal(): String? {
            val formatter = SimpleDateFormat(FORMAT, Locale.getDefault())
            formatter.timeZone = TimeZone.getDefault()
            return let(formatter::format)
        }

        fun getSubmissionFile(): String {
            return "file:///android_asset/$PREVIEW_PAST_SUBMISSION"
        }

        fun String.getFile(): String {
            val origin = "file:///android_asset/$this"
            val lan = Covid19Indigenous.language
            val locale = when {
                lan.contains("_") -> lan.split("_").first()
                lan.contains("-") -> lan.split("-").first()
                else -> lan
            }

            val languages = HashMap<String, String>()
            val xml = Covid19Indigenous.instance.resources.getXml(R.xml.locales_config)
            while (xml.next() != XmlResourceParser.END_TAG) {
                if (xml.eventType != XmlResourceParser.START_TAG) {
                    continue
                }
                if (xml.name == "locale") {
                    if (xml.attributeCount > 0) {
                        val l = xml.getAttributeValue(0)
                        if (l.contains("-")) {
                            val li = l.split("-")
                            val local = Locale(li.first(), li.last())
                            languages["${local.displayLanguage} (${local.displayCountry})"] =
                                local.language
                        } else {
                            val local = Locale(l)
                            languages[local.displayLanguage] = local.language
                        }
                        xml.nextTag()
                    }
                }
            }

            val supportedLocales = languages.values.toMutableList()
            supportedLocales.sortBy { it }

            val path = when {
                supportedLocales.contains(locale) -> "${origin}_${locale}"
                else -> "${origin}_${supportedLocales.first()}"
            }

            return "${path}.html"
        }

        @Suppress("DEPRECATION")
        fun changeStatusBar(isDark: Boolean, context: Context, color: Int) {
            val window: Window = (context as Activity?)!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context, color)
            if (isDark) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                val decorView: View = window.decorView
                var systemUiVisibilityFlags = decorView.systemUiVisibility
                systemUiVisibilityFlags =
                    systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                decorView.systemUiVisibility = systemUiVisibilityFlags
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        fun changeSystemNavBarColor(context: Context, @ColorInt color: Int) {
            val window: Window = (context as Activity?)!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = color
        }

        fun isOnline(activity: Context): Boolean {
            val connectivityManager =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || activeNetwork.hasTransport(
                    NetworkCapabilities.TRANSPORT_ETHERNET
                ) || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                else -> false
            }
        }

        fun showAlert(context: Context, title: String, msg: String) {
            AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.cancel()
                }.show()
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
            fileOrDirectory.deleteRecursively()
        }

        fun deleteSurveyFiles(fileOrDirectory: File) {
            fileOrDirectory.deleteRecursively()
        }

        private fun directoryPath(): File {
            val file = File(Covid19Indigenous.instance.filesDir.absolutePath, "/Covid19Indigenous")
            if (!file.exists()) file.mkdir()
            return file
        }

        fun culturePath(): File {
            val dir = File(directoryPath(), "/Content")
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

        fun manifestFile(): File {
            return File(culturePath(), "manifest.json")
        }

        fun surveyPath(): File {
            val dir = File(directoryPath(), "/Survey")
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

        fun submissionsPath(): File {
            val dir = File(surveyPath(), "/Submissions")
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

        fun surveyFile(): File {
            return File(surveyPath(), "questionnaires.json")
        }

        fun stringFromFile(file: File): String {
            val text = StringBuilder()
            try {
                val string = file.bufferedReader().use { it.readText() }
                text.append(string)
            } catch (e: IOException) {
                Log.e("error", e.toString())
            }

            return text.toString()
        }

        fun timeStamp(): String {
            val rnd = Random()
            val number: Int = rnd.nextInt(888889) + 111111
            return (System.currentTimeMillis() / 1000).toString() + "." + String.format(
                "%06d",
                number
            )
        }

        fun uploadingAnswerDialog(c: Context, file: File? = null) {
            val dialog = Dialog(c, R.style.NewDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_upload)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent)

            val txtUploading: TextView = dialog.findViewById(R.id.txtUploading)
            val txtInfo: TextView = dialog.findViewById(R.id.txtInfo)

            txtInfo.text = c.getString(R.string.checking_internet)

            if (file != null) {
                var pastTitle = ""
                if (file.name.startsWith("answer")) {
                    var name = file.name.substring(file.name.lastIndexOf("_") + 1)
                    name = name.substring(0, name.lastIndexOf("."))

                    name = "Past answers\n$name"
                    pastTitle += if (pastTitle == "") {
                        name
                    } else {
                        "\n\n" + name
                    }
                }

                dialog.show()

                isfetch = false

                if (isOnline(c as AppCompatActivity)) {
                    c.runOnUiThread {
                        txtInfo.text = c.getString(R.string.uploading_answers)
                        txtUploading.text = pastTitle
                    }

                    runBlocking {
                        val text = stringFromFile(file)

                        val answer = Gson().fromJson(text, AnswerVo::class.java)

                        val service =
                            RetrofitInstance.getRetrofitInstance().create(GetApi::class.java)
                        val call = service.uploadAnswer(answer)
                        call.enqueue(object : Callback<AnswerVo> {
                            override fun onResponse(
                                call: Call<AnswerVo>, response: retrofit2.Response<AnswerVo>
                            ) {
                                Log.d("res", response.body().toString())

                                try {
                                    response.body() as AnswerVo
                                    Log.e("success", "success")
                                    file.delete()
                                } catch (e: java.lang.Exception) {
                                    Log.e("errorUploading", e.toString())
                                }

                                dialog.dismiss()
                            }

                            override fun onFailure(call: Call<AnswerVo>, t: Throwable) {
                                Log.e("failUploading", t.toString())
                                dialog.dismiss()
                            }
                        })
                    }
                } else {
                    dialog.dismiss()
                }
            } else {
                val answerFile = ArrayList<File>()
                var pastTitle = ""

                val files = submissionsPath().listFiles()
                if (files != null) {
                    for (i in files.indices) {
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
                        dialog.show()

                        isfetch = false

                        if (isOnline(c as AppCompatActivity)) {
                            c.runOnUiThread {
                                txtInfo.text = c.getString(R.string.uploading_answers)
                                txtUploading.text = pastTitle
                            }

                            for (i in answerFile.indices) {
                                runBlocking {
                                    val text = stringFromFile(answerFile[i])

                                    val answer = Gson().fromJson(text, AnswerVo::class.java)

                                    val service = RetrofitInstance.getRetrofitInstance()
                                        .create(GetApi::class.java)
                                    val call = service.uploadAnswer(answer)
                                    call.enqueue(object : Callback<AnswerVo> {
                                        override fun onResponse(
                                            call: Call<AnswerVo>,
                                            response: retrofit2.Response<AnswerVo>
                                        ) {
                                            Log.d("res", response.body().toString())

                                            try {
                                                response.body() as AnswerVo
                                                //if success than it converts
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
                    }
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
            return try {
                val readData: SharedPreferences? = context.getSharedPreferences(
                    SHARE_PREF, Context.MODE_PRIVATE
                )
                readData?.getString(key, "") ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        abstract class DoubleClickListener : View.OnClickListener {

            private val doubleClickTime: Long = 300 //milliseconds

            private var lastClickTime: Long = 0

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
