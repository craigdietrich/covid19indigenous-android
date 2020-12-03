package com.craigdietrich.covid19indigenous.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.MainActivity
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant

class BeginFragment : Fragment() {

    private lateinit var beginViewModel: BeginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Constant.changeStatusBar(isDark = false, context = context as Activity)

        beginViewModel =
            ViewModelProviders.of(this).get(BeginViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_begin, container, false)


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