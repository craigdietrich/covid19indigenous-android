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
import androidx.fragment.app.Fragment
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.common.Constant.Companion.getFile
import com.craigdietrich.covid19indigenous.databinding.FragmentProjectBinding
import com.dueeeke.tablayout.listener.OnTabSelectListener

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentProjectBinding

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface")
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

        binding = FragmentProjectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.settings.javaScriptEnabled = true

        binding.webView.settings.setGeolocationEnabled(true)
        binding.webView.loadUrl(Constant.aboutProjectPath.getFile())

        this.context?.let { WebAppInterface(it) }
            ?.let { binding.webView.addJavascriptInterface(it, "Android") }

        val titles = arrayOf(getString(R.string.about_project_tab), getString(R.string.about_us))
        binding.tabAbout.setTabData(titles)

        binding.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    binding.webView.loadUrl(Constant.aboutProjectPath.getFile())
                } else {
                    binding.webView.loadUrl(Constant.aboutUsPath.getFile())
                }
            }

            override fun onTabReselect(position: Int) {}
        })
    }
}

class WebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        var url = toast
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        mContext.startActivity(browserIntent)
    }
}