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
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.ui.culture.CulResFragment
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*


class Constant {
    companion object {

        const val BASE_URL = "http://covid19indigenous.ca/"
        const val BASE_MEDIA_URL = "https://covid19indigenous.ca/feeds/content/"

        const val SHARE_PREF = "SHARE_PREF"

        const val CULTURE = "feeds/content/manifest.json?"
        const val QUESTIONS = "dashboard/pages/app?"

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

        fun surveyFile(): File {

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

            return File(dir, "questionnaires.json")
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
            val number: Int = rnd.nextInt(999999)
            return System.currentTimeMillis().toString() + "." + String.format("%06d", number)
        }

        fun openImageDialog(c: Context) {
            val dialog = Dialog(c, R.style.NewDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_upload)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
            /*val llCancel: LinearLayout = dialog.findViewById(R.id.llCancel)
            val llTakePicture: LinearLayout = dialog.findViewById(R.id.llTakePicture)
            val llGallery: LinearLayout = dialog.findViewById(R.id.llGallery)*/

            dialog.show()
        }
    }
}