package com.craigdietrich.covid19indigenous.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class Constant {
    companion object {

        const val BASE_URL = "https://covid19indigenous.ca/feeds/content/"
        const val BASE_MEDIA_URL = "https://covid19indigenous.ca/feeds/content/"


        const val CULTURE = "manifest.json?t=1607063769.361629"

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

        fun readAsset(context: Context, fileName: String): String =
            context
                .assets
                .open(fileName)
                .bufferedReader()
                .use(BufferedReader::readText)

        fun getUnsafeOkHttpClient(): OkHttpClient.Builder? {
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
    }
}