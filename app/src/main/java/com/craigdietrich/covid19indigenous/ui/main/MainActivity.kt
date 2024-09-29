package com.craigdietrich.covid19indigenous.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navView?.menu?.forEach { item ->
                if (destination.hierarchy.any { it.id == item.itemId }) {
                    item.isChecked = true
                }
            }
            binding.railView?.menu?.forEach { item ->
                if (destination.hierarchy.any { it.id == item.itemId }) {
                    item.isChecked = true
                }
            }
        }

        binding.railView?.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_begin -> {
                    val link = NavDeepLinkRequest.Builder
                        .fromUri("https://craigdietrich.com/begin".toUri())
                        .build()
                    val options = NavOptions.Builder().apply {
                        setLaunchSingleTop(true)
                        setPopUpTo(R.id.mobile_navigation, true)
                    }.build()
                    navController.navigate(link, options)
                    true
                }

                R.id.navigation_project -> {
                    val link = NavDeepLinkRequest.Builder
                        .fromUri("https://craigdietrich.com/project".toUri())
                        .build()
                    val options = NavOptions.Builder().apply {
                        setLaunchSingleTop(true)
                        setPopUpTo(R.id.navigation_begin, false)
                    }.build()
                    navController.navigate(link, options)
                    true
                }

                R.id.navigation_survey -> {
                    val link = NavDeepLinkRequest.Builder
                        .fromUri("https://craigdietrich.com/survey".toUri())
                        .build()
                    val options = NavOptions.Builder().apply {
                        setLaunchSingleTop(true)
                        setPopUpTo(R.id.navigation_begin, false)
                    }.build()
                    navController.navigate(link, options)
                    true
                }

                R.id.navigation_more -> {
                    val link = NavDeepLinkRequest.Builder
                        .fromUri("https://craigdietrich.com/more".toUri())
                        .build()
                    val options = NavOptions.Builder().apply {
                        setLaunchSingleTop(true)
                        setPopUpTo(R.id.navigation_begin, false)
                    }.build()
                    navController.navigate(link, options)
                    true
                }

                else -> false
            }
        }

        binding.navView?.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_begin -> {
                    val link = NavDeepLinkRequest.Builder
                        .fromUri("https://craigdietrich.com/begin".toUri())
                        .build()
                    val options = NavOptions.Builder().apply {
                        setLaunchSingleTop(true)
                        setPopUpTo(R.id.mobile_navigation, true)
                    }.build()
                    navController.navigate(link, options)
                    true
                }

                R.id.navigation_project -> {
                    val link = NavDeepLinkRequest.Builder
                        .fromUri("https://craigdietrich.com/project".toUri())
                        .build()
                    val options = NavOptions.Builder().apply {
                        setLaunchSingleTop(true)
                        setPopUpTo(R.id.navigation_begin, false)
                    }.build()
                    navController.navigate(link, options)
                    true
                }

                R.id.navigation_survey -> {
                    val link = NavDeepLinkRequest.Builder
                        .fromUri("https://craigdietrich.com/survey".toUri())
                        .build()
                    val options = NavOptions.Builder().apply {
                        setLaunchSingleTop(true)
                        setPopUpTo(R.id.navigation_begin, false)
                    }.build()
                    navController.navigate(link, options)
                    true
                }

                R.id.navigation_more -> {
                    val link = NavDeepLinkRequest.Builder
                        .fromUri("https://craigdietrich.com/more".toUri())
                        .build()
                    val options = NavOptions.Builder().apply {
                        setLaunchSingleTop(true)
                        setPopUpTo(R.id.navigation_begin, false)
                    }.build()
                    navController.navigate(link, options)
                    true
                }

                else -> false
            }
        }

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    //take action when network connection is gained
                    Log.e("internet", "connect")

                    val sendResults = Constant.readSP(this@MainActivity, Constant.SEND_RESULTS_TO_SERVER)
                    if(sendResults.isEmpty() || sendResults == "false") {
                        Constant.uploadingAnswerDialog(this@MainActivity)
                    }
                }

                override fun onLost(network: Network) {
                    //take action when network connection is lost
                    Log.e("internet", "lost")
                }
            })
    }
}