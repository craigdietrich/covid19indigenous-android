package com.craigdietrich.covid19indigenous.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.craigdietrich.covid19indigenous.ui.culture.CulResFragment
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.io.File
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class Constant {
    companion object {

        const val BASE_URL = "http://covid19indigenous.ca/"
        const val BASE_MEDIA_URL = "https://covid19indigenous.ca/feeds/content/"

        const val SHARE_PREF = "SHARE_PREF"

        const val CULTURE = "feeds/content/manifest.json?"
        const val QUESTIONS = "dashboard/pages/app?"

        const val TIME = "1607063769.361629"

        const val cookie =
            "visid_incap_2404656=rDI/08e0SSWyIy0S0JOYugAT0l8AAAAAQUIPAAAAAADh4QFWymWVTDoRdWOjpg19; incap_ses_1292_2404656=U2ZMYZD3+nZ2ecBCMxvuEThu018AAAAA8zyR6doQGL3KIdj9zu8VmQ=="

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

        fun readAsset(context: Context, fileName: String): String =
            context
                .assets
                .open(fileName)
                .bufferedReader()
                .use(BufferedReader::readText)

        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            return try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory
                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
                builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
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
    }
}