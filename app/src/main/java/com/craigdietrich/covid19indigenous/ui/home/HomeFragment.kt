package com.craigdietrich.covid19indigenous.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.MainActivity
import com.craigdietrich.covid19indigenous.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        val aboutButton = root.findViewById<LinearLayout>(R.id.llAboutProject)
        aboutButton.setOnClickListener {
            var mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("about")
        }

        val surveyButton = root.findViewById<LinearLayout>(R.id.llTakeSurvey)
        surveyButton.setOnClickListener { it: View? ->
            var mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("survey")
        }

        val cultureButton = root.findViewById<LinearLayout>(R.id.llCulture)
        cultureButton.setOnClickListener {
            var mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("culture")
        }

        return root
    }
}