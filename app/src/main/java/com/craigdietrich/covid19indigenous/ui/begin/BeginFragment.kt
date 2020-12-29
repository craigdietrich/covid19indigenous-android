package com.craigdietrich.covid19indigenous.ui.begin

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.craigdietrich.covid19indigenous.MainActivity
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant

class BeginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Constant.changeStatusBar(
            isDark = false,
            context = context as Activity,
            color = R.color.whiteText
        )

        val root = inflater.inflate(R.layout.fragment_begin, container, false)


        val aboutButton = root.findViewById<LinearLayout>(R.id.llAboutProject)
        aboutButton.setOnClickListener {
            val mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("about")
        }

        val surveyButton = root.findViewById<LinearLayout>(R.id.llTakeSurvey)
        surveyButton.setOnClickListener {
            val mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("survey")
        }

        val cultureButton = root.findViewById<LinearLayout>(R.id.llCulture)
        cultureButton.setOnClickListener {
            val mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("culture")
        }

        return root
    }
}