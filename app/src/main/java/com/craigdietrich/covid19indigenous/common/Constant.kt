package com.craigdietrich.covid19indigenous.common

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.craigdietrich.covid19indigenous.R

class Constant {
    companion object {
        fun changeStatusBar(isDark: Boolean, context: Context) {
            // for change statusbar color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = (context as Activity?)!!.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                if (isDark) {

                    window.statusBarColor = ContextCompat.getColor(context, R.color.grayBg)
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

                    val decorView: View = window.decorView
                    var systemUiVisibilityFlags = decorView.systemUiVisibility
                    systemUiVisibilityFlags =
                        systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    decorView.systemUiVisibility = systemUiVisibilityFlags
                } else {
                    window.statusBarColor = ContextCompat.getColor(context, R.color.whiteText)
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
    }
}