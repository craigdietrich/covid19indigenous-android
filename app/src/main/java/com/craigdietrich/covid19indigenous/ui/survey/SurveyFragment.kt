package com.craigdietrich.covid19indigenous.ui.survey

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.dueeeke.tablayout.listener.OnTabSelectListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_survey.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


open class NotificationsFragment : Fragment(), ClickListener {

    private lateinit var surveyViewModel: SurveyViewModel
    private var root: View? = null

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

        onWebButtonClick(this)
        surveyViewModel =
            ViewModelProviders.of(this).get(SurveyViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_survey, container, false)

        root!!.webView.settings.javaScriptEnabled = true
        root!!.webView.loadUrl("file:///android_asset/aboutSurvey.html");
        root!!.webView.addJavascriptInterface(
            this.context?.let { SurveyWebAppInterface(it!!) },
            "Android"
        )

        val titles = arrayOf(getString(R.string.about_survey), getString(R.string.take_survey_tab))
        root!!.tabAbout.setTabData(titles)

        root!!.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    root!!.webView.loadUrl("file:///android_asset/aboutSurvey.html")
                } else {
                    root!!.webView.loadData("<HTML><BODY></BODY></HTML>", "text/html", "utf-8");
                }
            }

            override fun onTabReselect(position: Int) {}
        })
        root!!.txtSubmit.setOnClickListener {
            if (root!!.edtCode.text.isNotEmpty()) {
                downloadFile()
            }
        }

        root!!.webViewConsent.settings.javaScriptEnabled = true
        root!!.webViewConsent.addJavascriptInterface(
            this.context?.let { SurveyWebAppInterface(it) },
            "Android"
        )
        root!!.webViewConsent.loadUrl("file:///android_asset/consent.html")

        return root
    }

    private fun downloadFile() {

        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)

            val client: OkHttpClient = Constant.getUnsafeOkHttpClient().build()

            val request: Request = Request.Builder()
                .url("https://covid19indigenous.ca/dashboard/pages/app?key=" + root!!.edtCode.text.toString() + "&t=1607063769.361629")
                .method("GET", null)
                .addHeader(
                    "Cookie",
                    Constant.cookie
                )
                .build()
            val response: Response = client.newCall(request).execute()

            val gson = GsonBuilder().create()

            /*responseJson = response.body!!.string()

            val data = gson.fromJson(responseJson, Array<CultureVo>::class.java).toList()

            listData = data

            setData()*/

        } catch (e: Exception) {
            Log.e("error", e.toString())
        }
    }

    override fun changeTab(pos: Int) {
        runOnUiThread {
            root!!.tabAbout.currentTab = pos
            root!!.webView.loadData("<HTML><BODY></BODY></HTML>", "text/html", "utf-8")
        }

    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }
}

class SurveyWebAppInterface(private val mContext: Context) {
    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        if (toast == "takeSurvey") {
            clickListener!!.changeTab(1)
            //root!!.tabAbout.currentTab = 1
        } else {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }
    }
}

private var clickListener: ClickListener? = null

fun onWebButtonClick(clickListener1: ClickListener?) {
    clickListener = clickListener1
}

interface ClickListener {
    fun changeTab(pos: Int)
}