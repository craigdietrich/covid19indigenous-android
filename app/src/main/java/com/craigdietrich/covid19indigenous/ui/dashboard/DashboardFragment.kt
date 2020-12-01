package com.craigdietrich.covid19indigenous.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.MainActivity
import com.craigdietrich.covid19indigenous.R

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val webView = root.findViewById<WebView>(R.id.webView)
        webView.settings.setJavaScriptEnabled(true)
        webView.loadUrl("file:///android_asset/aboutProject.html");
        webView.addJavascriptInterface(this.context?.let { WebAppInterface(it) }, "Android")

        val aboutProjectButton = root.findViewById<TextView>(R.id.button3)
        val aboutUsButton = root.findViewById<TextView>(R.id.button5)

        aboutProjectButton.setTextColor(resources.getColor(R.color.colorPrimary))
        aboutUsButton.setTextColor(Color.DKGRAY)

        aboutProjectButton.setOnClickListener {
            aboutProjectButton.setTextColor(resources.getColor(R.color.colorPrimary))
            aboutUsButton.setTextColor(Color.DKGRAY)
            webView.loadUrl("file:///android_asset/aboutProject.html");
        }

        aboutUsButton.setOnClickListener {
            aboutUsButton.setTextColor(resources.getColor(R.color.colorPrimary))
            aboutProjectButton.setTextColor(Color.DKGRAY)
            webView.loadUrl("file:///android_asset/aboutUs.html");
        }

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