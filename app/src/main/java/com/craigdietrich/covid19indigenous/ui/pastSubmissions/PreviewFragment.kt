package com.craigdietrich.covid19indigenous.ui.pastSubmissions

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.craigdietrich.covid19indigenous.BuildConfig
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.common.Constant.Companion.convertToLocal
import com.craigdietrich.covid19indigenous.databinding.FragmentPreviewBinding
import com.craigdietrich.covid19indigenous.isLandscape
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PreviewFragment : Fragment() {

    private lateinit var binding: FragmentPreviewBinding
    private var data: String? = null

    companion object {
        private const val DATA = "answers"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = arguments?.getString(DATA)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreviewBinding.inflate(inflater, container, false)
        Constant.changeStatusBar(
            isDark = false,
            context = context as Activity,
            color = R.color.whiteText
        )
        Constant.changeSystemNavBarColor(
            context = context as Activity,
            color = ContextCompat.getColor(
                requireContext(),
                if (resources.isLandscape) R.color.whiteText else R.color.colorPrimary
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (data != null) {
            if (view is WebView) {
                (requireActivity() as PastSubmissionActivity).setOnSelectAllListener(object :
                    OnActionListener {
                    override fun onDelete() {

                    }

                    override fun onShare() {
                        data?.let { path ->
                            val file = File(path)
                            try {
                                ShareCompat.IntentBuilder(requireContext()).apply {
                                    setStream(
                                        FileProvider.getUriForFile(
                                            requireContext(),
                                            requireContext().packageName + ".provider",
                                            file
                                        )
                                    )
                                    setType("*/*")
                                }.startChooser()
                            } catch (e: Exception) {
                                Log.e("onShare::", "${e.message}")
                            }
                        }
                    }
                })

                with(view) {
                    @SuppressLint("SetJavaScriptEnabled")
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowContentAccess = true
                    settings.allowFileAccess = true
                    settings.loadWithOverviewMode = true
                    settings.setGeolocationEnabled(true)
                    WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
                    loadUrl(Constant.getSubmissionFile())
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            data?.let { path ->
                                val file = File(path)
                                lifecycleScope.launch { loadData(file) }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun readFile(file: File): String {
        return withContext(Dispatchers.IO) {
            file.readText()
        }
    }

    suspend fun loadData(file: File) {
        val data: String = readFile(file)
        val map = Gson().fromJson(data, Map::class.java)
        val createdAt = map["created"]
        if (createdAt != null && createdAt is String) {
            (requireActivity() as PastSubmissionActivity).supportActionBar?.title =
                createdAt.convertToLocal()
        }
        val answers = (map["answers"] as List<*>).map { it as Map<*, *> }.toList()
        Log.v("answers", "$answers")

        val title =
            (requireActivity() as PastSubmissionActivity).supportActionBar?.title

        val titleScript = "javascript:setTitle('$title')"
        binding.root.evaluateJavascript(titleScript) { }

        delay(1500L)
        val values = Gson().toJson(answers)
        val dataScript = "javascript:setData($values)"
        binding.root.evaluateJavascript(dataScript) { }
    }
}