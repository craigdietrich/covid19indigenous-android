package com.craigdietrich.covid19indigenous.ui.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.common.Constant.Companion.getFile
import com.craigdietrich.covid19indigenous.databinding.FragmentProjectBinding
import com.dueeeke.tablayout.listener.OnTabSelectListener

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        Constant.changeStatusBar(
            isDark = false,
            context = context as Activity,
            color = R.color.whiteText
        )
        Constant.changeSystemNavBarColor(
            context = context as Activity,
            color = ContextCompat.getColor(requireContext(), R.color.whiteText)
        )
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0 // 👈 no bottom inset to allow edge-to-edge
            )
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @SuppressLint("SetJavaScriptEnabled")
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.settings.setGeolocationEnabled(true)
        binding.webView.loadUrl(Constant.ABOUT_PROJECT_PATH.getFile())

        this.context?.let { WebAppInterface(it) }
            ?.let { binding.webView.addJavascriptInterface(it, "Android") }

        val titles = arrayOf(getString(R.string.about_project_tab), getString(R.string.about_us))
        binding.tabAbout.setTabData(titles)

        binding.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    binding.webView.loadUrl(Constant.ABOUT_PROJECT_PATH.getFile())
                } else {
                    binding.webView.loadUrl(Constant.ABOUT_US_PATH.getFile())
                }
            }

            override fun onTabReselect(position: Int) {}
        })
    }
}

class WebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page */
    @JavascriptInterface
    fun showToast(toast: String) {
        var url = toast
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        mContext.startActivity(browserIntent)
    }
}