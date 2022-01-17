package com.craigdietrich.covid19indigenous.ui.survey

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.craigdietrich.covid19indigenous.BuildConfig
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.retrfit.GetApi
import com.craigdietrich.covid19indigenous.retrfit.RetrofitInstance
import com.dueeeke.tablayout.listener.OnTabSelectListener
import kotlinx.android.synthetic.main.fragment_survey.*
import kotlinx.android.synthetic.main.fragment_survey.view.*
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.FileWriter

class NotificationsFragment : Fragment(), ClickListener {

    private var root: View? = null
    private val writeRequestCode = 10111
    var jsonResponse = ""
    var isFirstTimeConsentAccept = false

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("JavascriptInterface", "we", "AddJavascriptInterface")
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
        root = inflater.inflate(R.layout.fragment_survey, container, false)

        initWebView()

        root!!.webView.loadUrl(Constant.aboutSurveyPath)

        val titles = arrayOf(getString(R.string.about_survey), getString(R.string.take_survey_tab))
        root!!.tabAbout.setTabData(titles)

        root!!.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    root!!.webView.loadUrl(Constant.aboutSurveyPath)
                } else {
                    setSurveyForm()
                }
            }

            override fun onTabReselect(position: Int) {}
        })
        root!!.txtSubmit.setOnClickListener {
            if (root!!.edtCode.text.isNotEmpty()) {
                downloadFile()
            }
        }

        root!!.txtConsent.setOnClickListener {
            context?.let { Constant.writeSP(it, Constant.isAcceptSurvey, "true") }
            isFirstTimeConsentAccept = true
            setSurveyForm()
        }

        root!!.txtCancel.setOnClickListener {
            cancelClicked()
        }

        root!!.txtCancelDownload.setOnClickListener {
            cancelClicked()
        }
        return root
    }

    private fun cancelClicked() {
        llSurvey.visibility = View.VISIBLE
        llSurveyDownload.visibility = View.GONE
        root!!.tabAbout.currentTab = 0
        root!!.webViewConsent.loadUrl(Constant.aboutSurveyPath)
    }

    private var mCM: String? = null
    private var mUM: ValueCallback<Uri>? = null
    private var mUMA: ValueCallback<Array<Uri>>? = null
    private val fileRequestCode = 1

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initWebView() {

        root!!.webView.settings.javaScriptEnabled = true
        root!!.webView.settings.domStorageEnabled = true
        root!!.webView.settings.allowContentAccess = true
        root!!.webView.settings.allowFileAccess = true
        root!!.webView.settings.loadWithOverviewMode = true

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        this.context?.let { SurveyWebAppInterface(it) }?.let {
            root!!.webView.addJavascriptInterface(
                it,
                "Android"
            )
        }

        root!!.webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (mUMA != null) {
                    mUMA!!.onReceiveValue(null)
                }
                mUMA = filePathCallback

                if (Constant.fileType == "image") {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, fileRequestCode)
                } else {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, fileRequestCode)
                }

                return true
            }
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= 21) {
            var results: Array<Uri>? = null
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == fileRequestCode) {
                    if (null == mUMA) {
                        return
                    }
                    if (intent == null) { //Capture Photo if no image available
                        if (mCM != null) {
                            results = arrayOf(Uri.parse(mCM))
                        }
                    } else {
                        val dataString = intent.dataString
                        if (dataString != null) {
                            results = arrayOf(Uri.parse(dataString))
                        }
                    }
                }
            }
            mUMA!!.onReceiveValue(results)
            mUMA = null
        } else {
            if (requestCode == fileRequestCode) {
                if (null == mUM) return
                val result =
                    if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
                mUM!!.onReceiveValue(result)
                mUM = null
            }
        }
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }


    override fun changeTab(pos: Int) {
        runOnUiThread {
            if (pos == 0) {
                root!!.tabAbout.currentTab = 0
                webView.loadUrl(Constant.aboutSurveyPath)
            } else {
                setSurveyForm()
            }
        }
    }

    private fun setSurveyForm() {

        root!!.tabAbout.currentTab = 1

        if (Constant.surveyFile(context as AppCompatActivity).exists()) {

            if (context?.let { Constant.readSP(it, Constant.isAcceptSurvey) } == "true") {
                if (Constant.isOnline(context as AppCompatActivity) && !Constant.isfetch && !isFirstTimeConsentAccept) {
                    try {

                        val dialog = Dialog(context as AppCompatActivity, R.style.NewDialog)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_re_fetch)
                        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)

                        dialog.show()

                        val service: GetApi =
                            RetrofitInstance.getRetrofitInstance().create(GetApi::class.java)
                        val call = service.getQuestions(
                            Constant.readSP(
                                context as AppCompatActivity,
                                Constant.surveyCode
                            ), Constant.timeStamp()
                        )
                        call.enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: retrofit2.Response<ResponseBody>
                            ) {
                                try {
                                    jsonResponse = response.body()!!.string()

                                    requestPermissions(
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        writeRequestCode
                                    )

                                    dialog.dismiss()
                                } catch (e: Exception) {
                                    dialog.dismiss()
                                    Log.e("error", e.toString())
                                }

                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                dialog.dismiss()
                                Log.e("res", t.toString())
                            }
                        })
                    } catch (e: Exception) {
                        Log.e("error", e.toString())
                    }
                } else {
                    root!!.llSurvey.visibility = View.VISIBLE

                    root!!.webView.loadUrl(Constant.indexSurveyPath)
                    root!!.webView.webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String) {
                            if (root!!.tabAbout.currentTab == 1) {
                                root!!.webView.loadUrl(
                                    "javascript:getJsonFromSystem('" + Constant.stringFromFile(
                                        Constant.surveyFile(context as AppCompatActivity)
                                    ).toString() + "')"
                                )
                            }
                        }
                    }
                }
            } else {
                root!!.llSurvey.visibility = View.GONE
                root!!.llSurveyDownload.visibility = View.GONE
                root!!.llSurveyContent.visibility = View.VISIBLE

                setSurveyConsentWeb()
            }
        } else {
            root!!.llSurvey.visibility = View.GONE
            root!!.llSurveyDownload.visibility = View.VISIBLE
        }
    }

    private fun downloadFile() {

        if (Constant.isOnline(context as AppCompatActivity)) {
            try {

                val service: GetApi =
                    RetrofitInstance.getRetrofitInstance().create(GetApi::class.java)
                val call = service.getQuestions(edtCode.text.toString(), Constant.timeStamp())
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: retrofit2.Response<ResponseBody>
                    ) {
                        try {
                            jsonResponse = response.body()!!.string()

                            if (jsonResponse.startsWith("{\"error\":")) {
                                Constant.showAlert(
                                    context as AppCompatActivity,
                                    getString(R.string.error),
                                    getString(
                                        R.string.code_not_valid
                                    )
                                )
                            } else {
                                Constant.isfetch = true

                                Constant.writeSP(
                                    context as AppCompatActivity,
                                    Constant.surveyCode,
                                    edtCode.text.toString()
                                )
                                requestPermissions(
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    writeRequestCode
                                )
                            }

                        } catch (e: Exception) {
                            Constant.showAlert(
                                context as AppCompatActivity, getString(R.string.error), getString(
                                    R.string.code_not_valid
                                )
                            )
                            Log.e("error", e.toString())
                        }

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Constant.showAlert(
                            context as AppCompatActivity, getString(R.string.error), getString(
                                R.string.code_not_valid
                            )
                        )
                        Log.e("res", t.toString())
                    }
                })
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }
        } else {
            Constant.showAlert(
                context as AppCompatActivity,
                getString(R.string.no_connection),
                getString(R.string.no_connection_desc)
            )
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
        } else {
            changeTab(0)
        }
    }


    private fun writeFiles() {

        try {

            jsonResponse = jsonResponse.replace("'", "\\'")
            jsonResponse = jsonResponse.replace("(", "\\(")
            jsonResponse = jsonResponse.replace(")", "\\)")
            jsonResponse = jsonResponse.replace("\"", "\\\"")
            jsonResponse = jsonResponse.trim()

            val writer = FileWriter(Constant.surveyFile(context as AppCompatActivity))
            writer.append(jsonResponse)
            writer.flush()
            writer.close()

            if (!Constant.isfetch) {
                Constant.isfetch = true
                setSurveyForm()
            } else {
                llSurveyDownload.visibility = View.GONE
                llSurveyContent.visibility = View.VISIBLE

                setSurveyConsentWeb()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface")
    private fun setSurveyConsentWeb() {
        root!!.webViewConsent.settings.javaScriptEnabled = true
        root!!.webViewConsent.settings.domStorageEnabled = true
        root!!.webViewConsent.settings.allowContentAccess = true
        root!!.webViewConsent.settings.allowFileAccess = true
        root!!.webViewConsent.settings.loadWithOverviewMode = true

        root!!.webViewConsent.loadUrl(Constant.consentSurveyPath)

        this.context?.let { SurveyWebAppInterface(it) }?.let {
            root!!.webViewConsent.addJavascriptInterface(
                it,
                "Android"
            )
        }
    }
}

class SurveyWebAppInterface(private val mContext: Context) {
    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        when (toast) {
            "takeSurvey" -> {
                clickListener!!.changeTab(1)
            }
            "deleteUserData" -> {
                AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.reset_data))
                    .setMessage(mContext.getString(R.string.reset_desc))
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.cancel()
                        runBlocking {
                            Constant.deleteSurveyFiles(Constant.surveyPath(mContext))
                            Constant.deleteCultureFiles(Constant.culturePath(mContext))
                            Constant.writeSP(mContext, Constant.isAcceptSurvey, "false")
                            Constant.writeSP(mContext as AppCompatActivity, Constant.surveyCode, "")
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
            }
        }
    }

    @JavascriptInterface
    fun setFileType(type: String) {
        Constant.fileType = type
    }

    @JavascriptInterface
    fun showAns(msg: String) {
        try {
            val file =
                File(Constant.surveyPath(mContext), "answers_" + Constant.timeStamp() + ".json")
            val writer = FileWriter(file)
            writer.append(msg)
            writer.flush()
            writer.close()

            Constant.uploadingAnswerDialog(mContext)
            clickListener!!.changeTab(0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @JavascriptInterface
    fun showConsent(msg: String) {
        var url = msg

        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        mContext.startActivity(browserIntent)
    }
}

private var clickListener: ClickListener? = null

fun onWebButtonClick(clickListener1: ClickListener?) {
    clickListener = clickListener1
}

interface ClickListener {
    fun changeTab(pos: Int)
}