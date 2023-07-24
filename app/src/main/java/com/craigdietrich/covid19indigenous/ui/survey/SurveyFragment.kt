package com.craigdietrich.covid19indigenous.ui.survey

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.craigdietrich.covid19indigenous.BuildConfig
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.databinding.FragmentSurveyBinding
import com.craigdietrich.covid19indigenous.retrfit.GetApi
import com.craigdietrich.covid19indigenous.retrfit.RetrofitInstance
import com.dueeeke.tablayout.listener.OnTabSelectListener
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.FileWriter

class NotificationsFragment : Fragment(), ClickListener {

    var jsonResponse = ""
    var isFirstTimeConsentAccept = false

    private lateinit var binding: FragmentSurveyBinding

    lateinit var callback: GeolocationPermissions.Callback
    lateinit var origin: String

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            callback.invoke(origin, true, false)
            Log.d("locationPermission", "result: Granted")
        } else {
            callback.invoke(origin, false, false)
            Log.d("locationPermission", "result: Not Granted")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("JavascriptInterface", "we", "AddJavascriptInterface")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Constant.changeStatusBar(
            isDark = true,
            context = context as Activity,
            color = R.color.grayBg
        )

        onWebButtonClick(this)

        binding = FragmentSurveyBinding.inflate(inflater, container, false)

        initWebView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.loadUrl(Constant.aboutSurveyPath)

        val titles = arrayOf(getString(R.string.about_survey), getString(R.string.take_survey_tab))
        binding.tabAbout.setTabData(titles)

        binding.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    binding.webView.loadUrl(Constant.aboutSurveyPath)
                } else {
                    setSurveyForm()
                }
            }

            override fun onTabReselect(position: Int) {}
        })
        binding.txtSubmit.setOnClickListener {
            if (binding.edtCode.text.isNotEmpty()) {
                downloadFile()
            }
        }

        binding.txtConsent.setOnClickListener {
            context?.let { Constant.writeSP(it, Constant.isAcceptSurvey, "true") }
            isFirstTimeConsentAccept = true
            setSurveyForm()
        }

        binding.txtCancel.setOnClickListener {
            cancelClicked()
        }

        binding.txtCancelDownload.setOnClickListener {
            cancelClicked()
        }
    }

    private fun cancelClicked() {
        binding.llSurvey.visibility = View.VISIBLE
        binding.llSurveyDownload.visibility = View.GONE
        binding.tabAbout.currentTab = 0
        binding.webViewConsent.loadUrl(Constant.aboutSurveyPath)
    }

    private var mCM: String? = null
    private var mUM: ValueCallback<Uri>? = null
    private var mUMA: ValueCallback<Array<Uri>>? = null
    private val fileRequestCode = 1

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initWebView() {

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.setGeolocationEnabled(true)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        this.context?.let { SurveyWebAppInterface(it) }?.let {
            binding.webView.addJavascriptInterface(
                it,
                "Android"
            )
        }

        binding.webView.webChromeClient = MyWeChromeClient() { origin, callback ->
            origin?.let { this.origin = it }
            callback?.let { this.callback = it }
        }
    }

    inner class MyWeChromeClient(
        private val request: (String?, GeolocationPermissions.Callback?) -> Unit,
    ) : WebChromeClient() {

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

        override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissions.Callback?
        ) {
            request.invoke(origin, callback)
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            super.onGeolocationPermissionsShowPrompt(origin, callback)
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
                    if (intent == null) { // Capture Photo if no image available
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
                binding.tabAbout.currentTab = 0
                binding.webView.loadUrl(Constant.aboutSurveyPath)
            } else {
                setSurveyForm()
            }
        }
    }

    private fun setSurveyForm() {
        binding.tabAbout.currentTab = 1

        if (Constant.surveyFile().exists()) {
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

                                    if (jsonResponse.startsWith("{\"error\":")) {
                                        Constant.showAlert(
                                            context as AppCompatActivity,
                                            getString(R.string.error),
                                            getString(
                                                R.string.code_not_valid
                                            )
                                        )
                                    } else {
                                        writeFiles()
                                    }

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
                    binding.llSurvey.visibility = View.VISIBLE

                    binding.webView.loadUrl(Constant.indexSurveyPath)
                    binding.webView.webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String) {
                            if (binding.tabAbout.currentTab == 1) {
                                val json = Constant.stringFromFile(Constant.surveyFile())
                                binding.webView.loadUrl("javascript:getJsonFromSystem('$json')")
                            }
                        }
                    }
                }
            } else {
                binding.llSurvey.visibility = View.GONE
                binding.llSurveyDownload.visibility = View.GONE
                binding.llSurveyContent.visibility = View.VISIBLE

                setSurveyConsentWeb()
            }
        } else {
            binding.llSurvey.visibility = View.GONE
            binding.llSurveyDownload.visibility = View.VISIBLE
        }
    }

    private fun downloadFile() {
        if (Constant.isOnline(context as AppCompatActivity)) {
            try {
                val service: GetApi =
                    RetrofitInstance.getRetrofitInstance().create(GetApi::class.java)
                val call =
                    service.getQuestions(binding.edtCode.text.toString(), Constant.timeStamp())
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
                                    binding.edtCode.text.toString()
                                )

                                writeFiles()
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

    private fun writeFiles() {
        try {

            jsonResponse = jsonResponse.replace("'", "\\'")
            jsonResponse = jsonResponse.replace("(", "\\(")
            jsonResponse = jsonResponse.replace(")", "\\)")
            jsonResponse = jsonResponse.replace("\"", "\\\"")
            jsonResponse = jsonResponse.trim()

            if (Constant.surveyFile().exists()) {
                Constant.surveyFile().delete()
            }

            val writer = FileWriter(Constant.surveyFile(), true)
            writer.append(jsonResponse)
            writer.flush()
            writer.close()

            if (!Constant.isfetch) {
                Constant.isfetch = true
                setSurveyForm()
            } else {
                binding.llSurveyDownload.visibility = View.GONE
                binding.llSurveyContent.visibility = View.VISIBLE

                setSurveyConsentWeb()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface")
    private fun setSurveyConsentWeb() {
        binding.webViewConsent.settings.javaScriptEnabled = true
        binding.webViewConsent.settings.domStorageEnabled = true
        binding.webViewConsent.settings.allowContentAccess = true
        binding.webViewConsent.settings.allowFileAccess = true
        binding.webViewConsent.settings.loadWithOverviewMode = true

        binding.webViewConsent.loadUrl(Constant.consentSurveyPath)

        this.context?.let { SurveyWebAppInterface(it) }?.let {
            binding.webViewConsent.addJavascriptInterface(
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
                            Constant.deleteSurveyFiles(Constant.surveyPath())
                            Constant.deleteCultureFiles(Constant.culturePath())
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
                File(Constant.surveyPath(), "answers_" + Constant.timeStamp() + ".json")
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
