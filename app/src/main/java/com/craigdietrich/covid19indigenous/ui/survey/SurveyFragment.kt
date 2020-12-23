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
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
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
import java.text.SimpleDateFormat
import java.util.*


class NotificationsFragment : Fragment(), ClickListener {

    private lateinit var surveyViewModel: SurveyViewModel
    private var root: View? = null
    private val writeRequestCode = 10111
    var jsonResponse = ""

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
        surveyViewModel = ViewModelProviders.of(this).get(SurveyViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_survey, container, false)

        initWebView()

        root!!.webView.loadUrl("file:///android_asset/aboutSurvey.html")

        val titles = arrayOf(getString(R.string.about_survey), getString(R.string.take_survey_tab))
        root!!.tabAbout.setTabData(titles)

        root!!.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    root!!.webView.loadUrl("file:///android_asset/aboutSurvey.html")
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
        root!!.webViewConsent.loadUrl("file:///android_asset/consent.html")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initWebView() {

        root!!.webView.settings.javaScriptEnabled = true
        root!!.webView.settings.domStorageEnabled = true
        root!!.webView.settings.allowContentAccess = true
        root!!.webView.settings.allowFileAccess = true
        root!!.webView.settings.setLoadWithOverviewMode(true)
        root!!.webView.settings.setAllowFileAccess(true)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        root!!.webView.addJavascriptInterface(
            this.context?.let { SurveyWebAppInterface(it) },
            "Android"
        )
       // root!!.webView.webChromeClient = PQChromeClient(this.activity?.applicationContext)

        root!!.webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                /*if (mUMA != null) {
                    mUMA!!.onReceiveValue(null)
                }
                mUMA = filePathCallback*/
                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
              /*  if (takePictureIntent!!.resolveActivity(this@NotificationsFragment.getPackageManager()) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", mCM)
                    } catch (ex: Exception) {
                        Log.e("Webview", "Image file creation failed", ex)
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath()
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile)
                        )
                    } else {
                        takePictureIntent = null
                    }
                }*/
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "*/*"
                val intentArray: Array<Intent>
                intentArray = takePictureIntent?.let { arrayOf(it) } ?: arrayOf<Intent>()
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startActivityForResult(chooserIntent, 1)
                return true
            }
        }
    }

    // Create an image file
    private fun createImageFile(): File? {
        @SuppressLint("SimpleDateFormat") val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun openFileChooser(
        uploadMsg: ValueCallback<Uri?>?,
        acceptType: String?
    ) {
        this.openFileChooser(uploadMsg, acceptType, null)
    }

    fun openFileChooser(
        uploadMsg: ValueCallback<Uri?>?,
        acceptType: String?,
        capture: String?
    ) {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "*/*"
        this@NotificationsFragment.startActivityForResult(
            Intent.createChooser(i, "File Browser"),
            1
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
      /*  if (Build.VERSION.SDK_INT >= 21) {
            var results: Array<Uri>? = null
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
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
            if (requestCode == FCR) {
                if (null == mUM) return
                val result =
                    if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
                mUM!!.onReceiveValue(result)
                mUM = null
            }
        }*/
    }


    class PQChromeClient(private val context: Context?) : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            // Double check that we don't have any existing callbacks
            /*if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null)
            }
            mUploadMessage = filePathCallback*/
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"

            startActivityForResult(intent, 1);
            Log.e("clicked", "image picker")
            //openFileSelectionDialog()
            return true
        }

        private fun startActivityForResult(intent: Intent, i: Int) {

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
                webView.loadUrl("file:///android_asset/aboutSurvey.html")
            } else {
                setSurveyForm()
            }
        }

    }

    private fun setSurveyForm() {

        root!!.tabAbout.currentTab = 1

        if (Constant.surveyFile().exists()) {

            if (context?.let { Constant.readSP(it, Constant.isAcceptSurvey) } == "true") {
                if (Constant.isOnline(context as AppCompatActivity) && !Constant.isfetch) {
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

                    root!!.webView.loadUrl("file:///android_asset/common/index.html")
                    root!!.webView.webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String) {
                            if (root!!.tabAbout.currentTab == 1) {
                                root!!.webView.loadUrl(
                                    "javascript:getJsonFromSystem('" + Constant.stringFromFile(
                                        Constant.surveyFile()
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
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun writeFiles() {

        try {

            jsonResponse = jsonResponse.replace("'", "\\'")
            jsonResponse = jsonResponse.replace("(", "\\(")
            jsonResponse = jsonResponse.replace(")", "\\)")
            jsonResponse = jsonResponse.replace("\"", "\\\"")
            jsonResponse = jsonResponse.trim()

            val writer = FileWriter(Constant.surveyFile())
            writer.append(jsonResponse)
            writer.flush()
            writer.close()

            if (!Constant.isfetch) {
                Constant.isfetch = true
                setSurveyForm()
            } else {
                llSurveyDownload.visibility = View.GONE
                llSurveyContent.visibility = View.VISIBLE
                root!!.webViewConsent.loadUrl("file:///android_asset/consent.html")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}

class SurveyWebAppInterface(private val mContext: Context) {
    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        if (toast == "takeSurvey") {
            clickListener!!.changeTab(1)
        } else {
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

    @JavascriptInterface
    fun showAns(msg: String) {
        try {
            val file = File(Constant.surveyPath(), "answers_" + Constant.timeStamp() + ".json")
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
}

private var clickListener: ClickListener? = null

fun onWebButtonClick(clickListener1: ClickListener?) {
    clickListener = clickListener1
}

interface ClickListener {
    fun changeTab(pos: Int)
}