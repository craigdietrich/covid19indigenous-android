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
import com.craigdietrich.covid19indigenous.databinding.FragmentBeginBinding

class BeginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBeginBinding.inflate(inflater, container, false)
        Constant.changeStatusBar(
            isDark = false,
            context = context as Activity,
            color = R.color.whiteText
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aboutButton = view.findViewById<LinearLayout>(R.id.llAboutProject)
        aboutButton.setOnClickListener {
            val mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("about")
        }

        val surveyButton = view.findViewById<LinearLayout>(R.id.llTakeSurvey)
        surveyButton.setOnClickListener {
            val mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("survey")
        }

        val cultureButton = view.findViewById<LinearLayout>(R.id.llCulture)
        cultureButton.setOnClickListener {
            val mainActivity: MainActivity = activity as MainActivity
            mainActivity.switchTo("culture")
        }
    }
}