package com.craigdietrich.covid19indigenous.ui.begin

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.databinding.FragmentBeginBinding
import com.craigdietrich.covid19indigenous.isLandscape

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
            color = if (resources.isLandscape) R.color.whiteText else R.color.colorPrimary
        )
        Constant.changeSystemNavBarColor(
            context = context as Activity,
            color = ContextCompat.getColor(requireContext(), R.color.whiteText)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aboutButton = view.findViewById<LinearLayout>(R.id.llAboutProject)
        aboutButton.setOnClickListener {
            val navController = findNavController()
            val link = NavDeepLinkRequest.Builder
                .fromUri("https://craigdietrich.com/project".toUri())
                .build()
            val options = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.navigation_begin, false)
            }.build()
            navController.navigate(link, options)
        }

        val surveyButton = view.findViewById<LinearLayout>(R.id.llTakeSurvey)
        surveyButton.setOnClickListener {
            val navController = findNavController()
            val link = NavDeepLinkRequest.Builder
                .fromUri("https://craigdietrich.com/survey".toUri())
                .build()
            val options = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.navigation_begin, false)
            }.build()
            navController.navigate(link, options)
        }

        val cultureButton = view.findViewById<LinearLayout>(R.id.llCulture)
        cultureButton.setOnClickListener {
            val link = NavDeepLinkRequest.Builder
                .fromUri("https://craigdietrich.com/more".toUri())
                .build()
            val options = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.navigation_begin, false)
            }.build()
            findNavController().navigate(link, options)
        }
    }
}