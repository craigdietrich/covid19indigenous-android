package com.craigdietrich.covid19indigenous

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide();

        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        actionBar?.hide();
    }

    fun switchTo(what: String) {
        if (what == "about") {
            val navView: BottomNavigationView = findViewById(R.id.nav_view)
            navView.setSelectedItemId(R.id.navigation_dashboard);
        } else if (what == "survey") {
            val navView: BottomNavigationView = findViewById(R.id.nav_view)
            navView.setSelectedItemId(R.id.navigation_notifications);
        } else if (what == "culture") {
            val navView: BottomNavigationView = findViewById(R.id.nav_view)
            navView.setSelectedItemId(R.id.navigation_culture);
        }
    }
}