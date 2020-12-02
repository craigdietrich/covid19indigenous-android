package com.craigdietrich.covid19indigenous.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.R


class DashboardFragment : Fragment() {

    private lateinit var projectViewModel: ProjectViewModel

    @SuppressLint("JavascriptInterface")
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

        projectViewModel =
            ViewModelProviders.of(this).get(ProjectViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_project, container, false)

        val webView = root.findViewById<WebView>(R.id.webView)
        webView.settings.setJavaScriptEnabled(true)
        webView.loadUrl("file:///android_asset/aboutProject.html");
        webView.addJavascriptInterface(this.context?.let { WebAppInterface(it) }, "Android")

        val rdProject = root.findViewById<RadioButton>(R.id.rdProject)
        val rgAbout = root.findViewById<RadioGroup>(R.id.rgAbout)

        rdProject.isChecked = true
        rgAbout.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId -> // checkedId is the RadioButton selected

            if (rdProject.isChecked) {
                webView.loadUrl("file:///android_asset/aboutProject.html")
            } else {
                webView.loadUrl("file:///android_asset/aboutUs.html")
            }
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