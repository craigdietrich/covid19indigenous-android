package com.craigdietrich.covid19indigenous.ui.survey

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.dueeeke.tablayout.SegmentTabLayout
import com.dueeeke.tablayout.listener.OnTabSelectListener


class NotificationsFragment : Fragment() {

    private lateinit var surveyViewModel: SurveyViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Constant.changeStatusBar(
            isDark = true,
            context = context as Activity,
            color = R.color.grayBg
        )

        surveyViewModel =
            ViewModelProviders.of(this).get(SurveyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_survey, container, false)

        val webView = root.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/aboutSurvey.html");
        webView.addJavascriptInterface(this.context?.let { SurveyWebAppInterface(it) }, "Android")

        val titles = arrayOf(getString(R.string.about_survey), getString(R.string.take_survey_tab))
        val tabAbout = root.findViewById<SegmentTabLayout>(R.id.tabAbout)
        tabAbout.setTabData(titles)

        tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    webView.loadUrl("file:///android_asset/aboutSurvey.html")
                } else {
                    webView.loadData("<HTML><BODY></BODY></HTML>", "text/html", "utf-8");
                }
            }

            override fun onTabReselect(position: Int) {}
        })

        return root
    }
}

class SurveyWebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
}