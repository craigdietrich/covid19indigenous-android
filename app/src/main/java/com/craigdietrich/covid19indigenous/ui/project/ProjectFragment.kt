package com.craigdietrich.covid19indigenous.ui.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.dueeeke.tablayout.SegmentTabLayout
import com.dueeeke.tablayout.listener.OnTabSelectListener


class DashboardFragment : Fragment() {

    private lateinit var projectViewModel: ProjectViewModel

    @SuppressLint("JavascriptInterface")
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

        projectViewModel =
            ViewModelProviders.of(this).get(ProjectViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_project, container, false)

        val webView = root.findViewById<WebView>(R.id.webView)
        webView.settings.setJavaScriptEnabled(true)
        webView.loadUrl("file:///android_asset/aboutProject.html");
        webView.addJavascriptInterface(this.context?.let { WebAppInterface(it) }, "Android")

        val titles = arrayOf(getString(R.string.about_project_tab), getString(R.string.about_us))
        val tabAbout = root.findViewById<SegmentTabLayout>(R.id.tabAbout)
        tabAbout.setTabData(titles)

        tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    webView.loadUrl("file:///android_asset/aboutProject.html")
                } else {
                    webView.loadUrl("file:///android_asset/aboutUs.html")
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
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
}