package com.craigdietrich.covid19indigenous.ui.home

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.MainActivity
import com.craigdietrich.covid19indigenous.R

class BeginFragment : Fragment() {

    private lateinit var beginViewModel: BeginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // for change statusbar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = (context as Activity?)!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context as Activity, R.color.whiteText)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

        }

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