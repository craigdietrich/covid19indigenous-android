package com.craigdietrich.covid19indigenous.ui.culture

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.dueeeke.tablayout.SegmentTabLayout
import com.dueeeke.tablayout.listener.OnTabSelectListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class CulResFragment : Fragment() {

    companion object {
        fun newInstance() = CulResFragment()
    }

    private lateinit var viewModel: CulResViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_cul_res, container, false)

        Constant.changeStatusBar(isDark = true, context = context as Activity)

        val titles = arrayOf(getString(R.string.culture), getString(R.string.resilience))
        val tabAbout = root.findViewById<SegmentTabLayout>(R.id.tabAbout)
        val txtDownload = root.findViewById<TextView>(R.id.txtDownload)

        tabAbout.setTabData(titles)

        tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    //webView.loadUrl("file:///android_asset/aboutProject.html")
                } else {
                    //webView.loadUrl("file:///android_asset/aboutUs.html")
                }
            }

            override fun onTabReselect(position: Int) {}
        })

        txtDownload.setOnClickListener {
            if (Constant.isOnline(context as AppCompatActivity)) {

                try {
                    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

                    StrictMode.setThreadPolicy(policy)

                    val client: OkHttpClient = OkHttpClient().newBuilder()
                        .build()
                    val request: Request = Request.Builder()
                        .url("https://covid19indigenous.ca/feeds/content/manifest.json?t=1606985583.858901")
                        .method("GET", null)
                        .build()
                    val response: Response = client.newCall(request).execute()

                    print(response.message)
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }

            } else {
                AlertDialog.Builder(context)
                    .setTitle("No Connection")
                    .setMessage("Your device does not appear to have an Internet connection. Please establish a connection and try again.") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Ok",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        }) // A null listener allows the button to dismiss the dialog and take no further action.
                    .show()
            }
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CulResViewModel::class.java)

    }

}