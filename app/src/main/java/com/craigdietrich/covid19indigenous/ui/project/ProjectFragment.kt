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
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.dueeeke.tablayout.SegmentTabLayout
import com.dueeeke.tablayout.listener.OnTabSelectListener

class DashboardFragment : Fragment() {

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

        val root = inflater.inflate(R.layout.fragment_project, container, false)

        val webView = root.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(Constant.aboutProjectPath)
        webView.addJavascriptInterface(this.context?.let { WebAppInterface(it) }, "Android")

        val titles = arrayOf(getString(R.string.about_project_tab), getString(R.string.about_us))
        val tabAbout = root.findViewById<SegmentTabLayout>(R.id.tabAbout)
        tabAbout.setTabData(titles)

        tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    webView.loadUrl(Constant.aboutProjectPath)
                } else {
                    webView.loadUrl(Constant.aboutUsPath)
                }
            }

            override fun onTabReselect(position: Int) {}
        })

        return root
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