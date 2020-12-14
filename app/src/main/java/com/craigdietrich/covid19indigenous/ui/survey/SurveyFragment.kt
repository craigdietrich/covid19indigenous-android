package com.craigdietrich.covid19indigenous.ui.survey

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.retrfit.GetApi
import com.craigdietrich.covid19indigenous.retrfit.RetrofitInstance
import com.dueeeke.tablayout.listener.OnTabSelectListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_cul_res.*
import kotlinx.android.synthetic.main.fragment_survey.*
import kotlinx.android.synthetic.main.fragment_survey.view.*
import retrofit2.Call
import retrofit2.Callback
import java.io.*


open class NotificationsFragment : Fragment(), ClickListener {

    private lateinit var surveyViewModel: SurveyViewModel
    private var root: View? = null
    private val writeRequestCode = 10111
    var jsonResponse = ""
    private var dir = File(
        Environment.getExternalStorageDirectory().absolutePath,
        "/Covid19Indigenous"
    )

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

        if (!dir.exists()) {
            dir.mkdir()
        }

        dir = File(dir, "/Survey")
        if (!dir.exists()) {
            dir.mkdir()
        }

        onWebButtonClick(this)
        surveyViewModel =
            ViewModelProviders.of(this).get(SurveyViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_survey, container, false)

        root!!.webView.settings.javaScriptEnabled = true
        root!!.webView.loadUrl("file:///android_asset/aboutSurvey.html")
        root!!.webView.settings.javaScriptEnabled = true;
        root!!.webView.addJavascriptInterface(
            this.context?.let { SurveyWebAppInterface(it) },
            "Android"
        )

        val titles = arrayOf(getString(R.string.about_survey), getString(R.string.take_survey_tab))
        root!!.tabAbout.setTabData(titles)

        root!!.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    root!!.webView.loadUrl("file:///android_asset/aboutSurvey.html")
                } else {


                    val file = File(dir, "questionnaires.json")
                    val text = StringBuilder()

                    try {
                        val br = BufferedReader(FileReader(file))
                        var line: String?
                        while (br.readLine().also { line = it } != null) {
                            text.append(line)
                            text.append('\n')
                        }
                        br.close()
                    } catch (e: IOException) {
                        //You'll need to add proper error handling here
                        Log.e("error", e.toString())
                    }

                    var json = text.toString()
                    json = json.replace("'", "\\'")
                    json = json.replace("(", "\\(")
                    json = json.replace(")", "\\)")
                    json = json.replace("\"", "\\\"")
                    json = json.trim()

                    root!!.webView.loadUrl("file:///android_asset/common/index.html")
                    root!!.webView.webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String) {
                            root!!.webView.loadUrl("javascript:getJsonFromSystem('$json')")
                        }
                    }

                }
            }

            override fun onTabReselect(position: Int) {}
        })
        root!!.txtSubmit.setOnClickListener {
            if (root!!.edtCode.text.isNotEmpty()) {
                downloadFile()
            }
        }

        /*root!!.webViewConsent.settings.javaScriptEnabled = true
        root!!.webViewConsent.addJavascriptInterface(
            this.context?.let { SurveyWebAppInterface(it) },
            "Android"
        )
        root!!.webViewConsent.loadUrl("file:///android_asset/consent.html")*/

        return root
    }

    private fun downloadFile() {

        try {
            val service: GetApi = RetrofitInstance.getRetrofitInstance().create(GetApi::class.java)
            val call = service.getQuestions(edtCode.text.toString(), Constant.TIME, Constant.cookie)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                    Log.e("res", response.body().toString())

                    try {
                        jsonResponse = response.body().toString()

                        val gson = GsonBuilder().create()
                        val responseJson = gson.toJsonTree(jsonResponse).asJsonObject

                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            writeRequestCode
                        )
                        /*listData = response.body() as String
                        checkData()*/

                        /*requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            writeRequestCode
                        )*/
                    } catch (e: Exception) {
                        Log.e("error", e.toString())
                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    txtProgress.visibility = View.GONE
                    Log.e("res", t.toString())
                }
            })
        } catch (e: Exception) {
            Log.e("error", e.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            if (requestCode == writeRequestCode) {
                writeFiles()
            }
        }

    }

    private fun writeFiles() {

        try {
            //Log.e("data", listData.toString())

            /*val gson = GsonBuilder().create()
            val responseJson = gson.toJsonTree(listData).asJsonArray*/

            try {
                val gpxfile = File(dir, "manifest.json")
                val writer = FileWriter(gpxfile)
                writer.append(jsonResponse)
                writer.flush()
                writer.close()

                llSurveyDownload.visibility = View.GONE
                llSurveyBeforeContinue.visibility = View.VISIBLE
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


        } catch (e: java.lang.Exception) {

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