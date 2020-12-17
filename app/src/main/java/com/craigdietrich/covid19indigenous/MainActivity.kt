package com.craigdietrich.covid19indigenous

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.craigdietrich.covid19indigenous.common.Constant
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)
        actionBar?.hide()


        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                //take action when network connection is gained
                Log.e("internet", "connect")
                Constant.uploadingAnswerDialog(this@MainActivity)
            }

            override fun onLost(network: Network?) {
                //take action when network connection is lost
                Log.e("internet", "lost")
            }
        })
    }

    fun switchTo(what: String) {
        when (what) {
            "about" -> {
                val navView: BottomNavigationView = findViewById(R.id.nav_view)
                navView.selectedItemId = R.id.navigation_dashboard
            }
            "survey" -> {
                val navView: BottomNavigationView = findViewById(R.id.nav_view)
                navView.selectedItemId = R.id.navigation_notifications
            }
            "culture" -> {
                val navView: BottomNavigationView = findViewById(R.id.nav_view)
                navView.selectedItemId = R.id.navigation_culture
            }
        }
    }
}