package com.craigdietrich.covid19indigenous.ui.survey

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.craigdietrich.covid19indigenous.BuildConfig
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.common.Constant.Companion.getFile
import com.craigdietrich.covid19indigenous.databinding.FragmentSurveyBinding
import com.craigdietrich.covid19indigenous.retrfit.GetApi
import com.craigdietrich.covid19indigenous.retrfit.RetrofitInstance
import com.craigdietrich.covid19indigenous.ui.dialogPermission.PermissionDeniedDialogFragment
import com.dueeeke.tablayout.listener.OnTabSelectListener
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.FileWriter

class SurveyFragment : Fragment() {

    private var isFirstTimeConsentAccept = false
    var jsonResponse = ""

    private lateinit var binding: FragmentSurveyBinding

    private lateinit var origin: String
    private lateinit var callback: GeolocationPermissions.Callback

    private val geoLocationRequest: (String?, GeolocationPermissions.Callback?) -> Unit =
        { origin, callback ->
            origin?.let { this.origin = it }
            callback?.let { this.callback = it }
        }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkGPSEnable(): Boolean {
        val manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val geoLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.values.all { result -> result }) {
            if (checkGPSEnable()) {
                callback.invoke(origin, true, false)
            } else {
                checkLocationSetting()
            }
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
            !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            callback.invoke(origin, false, false)
        } else {
            callback.invoke(origin, false, false)
        }
    }
    private lateinit var webRequest: PermissionRequest
    private val audioVideoRequest: (PermissionRequest) -> Unit = { request ->
        webRequest = request
    }
    private val audioVideoPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            requireActivity().runOnUiThread {
                webRequest.grant(webRequest.resources)
            }
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            || !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
            || !shouldShowRequestPermissionRationale(Manifest.permission.MODIFY_AUDIO_SETTINGS)
        ) {
            webRequest.grant(emptyArray())
            showPermissionRationale()

        } else {
            /* filePathCallback.onReceiveValue(null)*/
            webRequest.deny()
        }
    }


    private lateinit var imageCallback: ValueCallback<Array<Uri>>
    private val imageRequest: (ValueCallback<Array<Uri>>) -> Unit = { callback ->
        imageCallback = callback
    }

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result -> result }


    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // User enabled location settings
            callback.invoke(origin, true, false)
        } else {
            // User did not enable location settings
            callback.invoke(origin, false, false)
        }
    }
    private var selectedTab = 0

    @SuppressLint("JavascriptInterface", "we", "AddJavascriptInterface")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSurveyBinding.inflate(inflater, container, false)
        Constant.changeStatusBar(
            isDark = false,
            context = context as Activity,
            color = R.color.whiteText
        )
        Constant.changeSystemNavBarColor(
            context = context as Activity,
            color = ContextCompat.getColor(requireContext(), R.color.whiteText)
        )

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0 // 👈 no bottom inset to allow edge-to-edge
            )
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView()

        val titles = arrayOf(getString(R.string.about_survey), getString(R.string.take_survey_tab))
        binding.tabAbout.setTabData(titles)

        binding.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                selectedTab = position
                if (position == 0) {
                    binding.llSurvey.visibility = View.VISIBLE
                    binding.llSurveyDownload.visibility = View.GONE
                    binding.llSurveyContent.visibility = View.GONE

                    if (binding.webView.url != Constant.ABOUT_SURVEY_PATH.getFile()) {
                        binding.webView.loadUrl(Constant.ABOUT_SURVEY_PATH.getFile())
                    }
                } else {
                    setSurveyForm()
                }
            }

            override fun onTabReselect(position: Int) {
                selectedTab = position
                if (position == 0) {
                    binding.llSurvey.visibility = View.VISIBLE
                    binding.llSurveyDownload.visibility = View.GONE
                    binding.llSurveyContent.visibility = View.GONE

                    if (binding.webView.url != Constant.ABOUT_SURVEY_PATH.getFile()) {
                        binding.webView.loadUrl(Constant.ABOUT_SURVEY_PATH.getFile())
                    }
                } else {
                    setSurveyForm()
                }
            }
        })

        binding.txtSubmit.setOnClickListener {
            if (binding.edtCode.text.isNotEmpty()) {
                downloadFile()
            }
        }

        binding.txtConsent.setOnClickListener {
            Constant.writeSP(requireContext(), Constant.IS_ACCEPT_SURVEY, "true")
            isFirstTimeConsentAccept = true

            setSurveyForm()
        }

        binding.txtCancel.setOnClickListener {
            cancelClicked()
        }

        binding.txtCancelDownload.setOnClickListener {
            cancelClicked()
        }

        isFirstTimeConsentAccept =
            Constant.readSP(requireContext(), Constant.IS_ACCEPT_SURVEY) == "true"

        changeTab(0)
    }

    override fun onResume() {
        super.onResume()
        changeTab(selectedTab)
    }

    private fun cancelClicked() {
        binding.llSurvey.visibility = View.VISIBLE
        binding.llSurveyDownload.visibility = View.GONE

        binding.tabAbout.currentTab = 0
        if (binding.webView.url != Constant.ABOUT_SURVEY_PATH.getFile()) {
            binding.webView.loadUrl(Constant.ABOUT_SURVEY_PATH.getFile())
        }
    }

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initWebView() {

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.allowFileAccess = true

        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.setGeolocationEnabled(true)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        binding.webView.addJavascriptInterface(SurveyWebAppInterface(), "Android")

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                val res = request?.resources
                if (res != null) {
                    audioVideoRequest.invoke(request)

                    val permissions = mutableListOf<String>()
                    for (i in res.indices) {
                        when (res[i]) {
                            "android.webkit.resource.VIDEO_CAPTURE" -> permissions.add(Manifest.permission.CAMERA)
                            "android.webkit.resource.AUDIO_CAPTURE" -> {
                                permissions.addAll(
                                    listOf(
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                                    )
                                )
                            }
                        }
                    }

                    audioVideoPermission.launch(permissions.toTypedArray())
                }

            }


            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (Constant.fileType == "photo") {
                    imageRequest.invoke(filePathCallback)
                    if (checkPermission(Manifest.permission.CAMERA)) {
                        TedImagePicker.with(requireContext())
                            .start { uri ->
                                imageCallback.onReceiveValue(arrayOf(uri))
                            }
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        ) {
                            val dialogFragment =
                                PermissionDeniedDialogFragment(
                                    content = "Camera permission is required. Please grant it in app settings.",
                                    positiveButtonClick = {
                                        openAppSettings()
                                    },
                                    negativeButtonClick = {
                                    })
                            dialogFragment.isCancelable = false
                            dialogFragment.show(
                                childFragmentManager,
                                "PermissionDeniedDialogFragment"
                            )
                        } else {
                            cameraPermission.launch(Manifest.permission.CAMERA)
                        }
                        return false
                    }
                    return true
                }
                return false
            }

            @RequiresApi(Build.VERSION_CODES.S)
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                val gpsEnabled = checkGPSEnable()
                val fineLocationGrated = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                val coarseLocationGranted =
                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                if (gpsEnabled && (fineLocationGrated || coarseLocationGranted)) {
                    callback!!.invoke(origin, true, false)
                } else {
                    if (gpsEnabled) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                        ) {
                            val dialogFragment =
                                PermissionDeniedDialogFragment(
                                    content = "Location permission is required. Please grant it in app settings.",
                                    positiveButtonClick = {
                                        callback!!.invoke(origin, false, false)
                                        openAppSettings()
                                    },
                                    negativeButtonClick = {
                                        callback!!.invoke(origin, false, false)
                                    })
                            dialogFragment.isCancelable = false
                            dialogFragment.show(
                                childFragmentManager,
                                "PermissionDeniedDialogFragment"
                            )
                        } else {
                            geoLocationRequest.invoke(origin, callback)
                            geoLocationPermission.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )

                        }
                    } else {
                        geoLocationRequest.invoke(origin, callback)
                        checkLocationSetting()
                    }
                }
            }
        }
    }

    private fun checkLocationSetting() {
        val builder = LocationSettingsRequest.Builder().apply {
            addLocationRequest(LocationRequest.Builder(1000).build())
            setAlwaysShow(true)
        }.build()

        val result =
            LocationServices.getSettingsClient(requireContext()).checkLocationSettings(builder)

        result.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (checkGPSEnable()) {
                    callback.invoke(origin, true, false)
                }
            } else {
                val exception = task.exception
                if (exception is ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            val resolvable = exception as ResolvableApiException
                            try {
                                val intentSenderRequest =
                                    IntentSenderRequest.Builder(resolvable.resolution.intentSender)
                                        .build()
                                locationSettingsLauncher.launch(intentSenderRequest)
                            } catch (sendEx: IntentSender.SendIntentException) {
                                // Handle the exception appropriately
                            }
                            /* LocationSettingsLauncher.launch(resolvable.resolution)*/
                            /*callback.invoke(origin, true, false)*/
                            Log.d(TAG, "checkSetting: RESOLUTION_REQUIRED")
                        }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            /*  callback.invoke(origin, false, false)*/
                        }
                    }
                } else {
                    // Handle other exceptions if needed
                }
            }
        }
    }


    fun changeTab(pos: Int) {
        requireActivity().runOnUiThread {
            if (pos == 0) {
                binding.tabAbout.currentTab = 0

                binding.llSurvey.visibility = View.VISIBLE
                binding.llSurveyDownload.visibility = View.GONE
                binding.llSurveyContent.visibility = View.GONE

                if (binding.webView.url != Constant.ABOUT_SURVEY_PATH.getFile()) {
                    binding.webView.loadUrl(Constant.ABOUT_SURVEY_PATH.getFile())
                }
            } else {
                setSurveyForm()
            }
        }
    }

    private fun setSurveyForm() {
        binding.tabAbout.currentTab = 1

        if (Constant.surveyFile().exists()) {
            if (isFirstTimeConsentAccept) {
                if (Constant.isOnline(requireContext()) && !Constant.isfetch) {
                    try {
                        val dialog = Dialog(context as AppCompatActivity, R.style.NewDialog)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_re_fetch)
                        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
                        dialog.show()

                        val service =
                            RetrofitInstance.getRetrofitInstance().create(GetApi::class.java)
                        val call = service.getQuestions(
                            Constant.readSP(
                                context as AppCompatActivity,
                                Constant.SURVEY_CODE
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
                                    Log.e("error", e.toString())
                                    dialog.dismiss()
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e("res", t.toString())
                                dialog.dismiss()
                            }
                        })
                    } catch (e: Exception) {
                        Log.e("error", e.toString())
                    }
                } else {
                    if (binding.webView.url != Constant.INDEX_SURVEY_PATH.getFile()) {
                        binding.webView.loadUrl(Constant.INDEX_SURVEY_PATH.getFile())
                    }
                    binding.llSurvey.visibility = View.VISIBLE
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

    @SuppressLint("SetTextI18n")
    private fun downloadFile() {
        if (Constant.isOnline(requireContext())) {
            try {
                binding.txtProgress.visibility = View.VISIBLE
                binding.txtProgress.text = "Downloading content"

                val service = RetrofitInstance.getRetrofitInstance().create(GetApi::class.java)
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
                                Constant.writeSP(
                                    requireContext(),
                                    Constant.SURVEY_CODE,
                                    binding.edtCode.text.toString()
                                )
                                Constant.isfetch = true
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

        if (binding.webViewConsent.url != Constant.CONSENT_SURVEY_PATH.getFile()) {
            binding.webViewConsent.loadUrl(Constant.CONSENT_SURVEY_PATH.getFile())
        }

        binding.webViewConsent.addJavascriptInterface(SurveyWebAppInterface(), "Android")
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireContext().packageName, null)
        )
        startActivity(intent)
    }

    private fun showPermissionRationale() {
        val rationaleDialog = AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("We need these permissions to capture audio and video.")
            .setPositiveButton("OK") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                /*webRequest.grant(emptyArray())*/

            }
            .setCancelable(true) // Prevent user from dismissing the dialog by tapping outside
            .create()

        rationaleDialog.show()
    }

    inner class SurveyWebAppInterface {
        /** Show a toast from the web page  */
        @JavascriptInterface
        fun showToast(toast: String) {
            when (toast) {
                "takeSurvey" -> {
                    selectedTab = 1
                    changeTab(1)
                }

                "deleteUserData" -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.reset_data))
                        .setMessage(getString(R.string.reset_desc))
                        .setPositiveButton("Ok") { dialog, _ ->
                            dialog.cancel()
                            runBlocking {
                                Constant.deleteSurveyFiles(Constant.surveyPath())
                                Constant.writeSP(
                                    requireContext(),
                                    Constant.IS_ACCEPT_SURVEY,
                                    "false"
                                )
                                Constant.writeSP(requireContext(), Constant.SURVEY_CODE, "")
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
                val sendResults = Constant.readSP(requireContext(), Constant.SEND_RESULTS_TO_SERVER)
                val isSendResult = sendResults.isEmpty() || sendResults == "false"

                val fileName = if (isSendResult) {
                    "answers_" + Constant.timeStamp() + ".json"
                } else {
                    "local_" + Constant.timeStamp() + ".json"
                }

                val file =
                    File(Constant.submissionsPath(), fileName)
                val writer = FileWriter(file)
                writer.append(msg)
                writer.flush()
                writer.close()

                if (isSendResult) {
                    Constant.uploadingAnswerDialog(requireContext(), file)
                }

                selectedTab = 0
                changeTab(0)
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
            startActivity(browserIntent)
        }
    }
}


