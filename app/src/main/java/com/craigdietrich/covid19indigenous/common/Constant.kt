package com.craigdietrich.covid19indigenous.common

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
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

        const val BASE_URL = "http://covid19indigenous.ca/"
        const val BASE_MEDIA_URL = "https://covid19indigenous.ca/feeds/content/"

        const val SHARE_PREF = "SHARE_PREF"

        const val CULTURE = "feeds/content/manifest.json?"
        const val QUESTIONS = "dashboard/pages/app?"
        const val ANSWERS = "dashboard/pages/handler"

        var myTask: CulResFragment.DownloadFileFromURL? = null

        fun changeStatusBar(isDark: Boolean, context: Context, color: Int) {
            // for change statusbar color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = (context as Activity?)!!.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(context, color)
                if (isDark) {

                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

                    val decorView: View = window.decorView
                    var systemUiVisibilityFlags = decorView.systemUiVisibility
                    systemUiVisibilityFlags =
                        systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    decorView.systemUiVisibility = systemUiVisibilityFlags
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
            }
        }

        fun isOnline(activity: AppCompatActivity): Boolean {
            val connectivityManager =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        fun internetAlert(context: Context) {
            AlertDialog.Builder(context)
                .setTitle("No Connection")
                .setMessage("Your device does not appear to have an Internet connection. Please establish a connection and try again.") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                .show()
        }

        fun showAlert(context: Context, title: String, msg: String) {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    }) // A null listener allows the button to dismiss the dialog and take no further action.
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

        fun writeSP(context: Context, key: String?, values: String?) {
            val writeData = context.getSharedPreferences(
                Constant.SHARE_PREF, Context.MODE_PRIVATE
            )
            val editor = writeData.edit()
            editor.putString(key, values)
            editor.apply()
        }

        fun readSP(context: Context, key: String?): String {
            var readData: SharedPreferences? = null
            try {
                readData = context.getSharedPreferences(
                    Constant.SHARE_PREF, Context.MODE_PRIVATE
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return readData!!.getString(key, "").toString()
        }

        fun deleteFiles(fileOrDirectory: File) {
            if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteFiles(
                child
            )
            fileOrDirectory.delete()
        }

        fun culturePath(): File {
            var dir = File(
                Environment.getExternalStorageDirectory(),
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

        fun surveyPath(): File {
            var dir = File(
                Environment.getExternalStorageDirectory(),
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

        fun surveyFile(): File {
            return File(surveyPath(), "questionnaires.json")
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
            Handler().postDelayed({

                if (isOnline(c as AppCompatActivity)) {
                    val files = surveyPath().listFiles()
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

                                val answer = Gson().fromJson(text.toString(), AnswerVo::class.java)

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
                                            var answerVo = response.body() as AnswerVo

                                            answerFile[i].delete()
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
                } else {
                    dialog.dismiss()
                }

            }, 3000)
            dialog.show()
        }


    }
}