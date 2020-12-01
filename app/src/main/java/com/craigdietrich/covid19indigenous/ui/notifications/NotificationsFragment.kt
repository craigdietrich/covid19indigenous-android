package com.craigdietrich.covid19indigenous.ui.notifications

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.MainActivity
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.ui.dashboard.WebAppInterface

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        val webView = root.findViewById<WebView>(R.id.webView)
        webView.settings.setJavaScriptEnabled(true)
        webView.loadUrl("file:///android_asset/aboutSurvey.html");
        webView.addJavascriptInterface(this.context?.let { SurveyWebAppInterface(it) }, "Android")

        val aboutSurveyButton = root.findViewById<TextView>(R.id.button9)
        val takeSurveyButton = root.findViewById<TextView>(R.id.button10)

        aboutSurveyButton.setTextColor(resources.getColor(R.color.colorPrimary))
        takeSurveyButton.setTextColor(Color.DKGRAY)

        aboutSurveyButton.setOnClickListener {
            aboutSurveyButton.setTextColor(resources.getColor(R.color.colorPrimary))
            takeSurveyButton.setTextColor(Color.DKGRAY)
            webView.loadUrl("file:///android_asset/aboutSurvey.html");
        }

        takeSurveyButton.setOnClickListener {
            takeSurveyButton.setTextColor(resources.getColor(R.color.colorPrimary))
            aboutSurveyButton.setTextColor(Color.DKGRAY)
            webView.loadData("<HTML><BODY></BODY></HTML>","text/html","utf-8");
        }

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