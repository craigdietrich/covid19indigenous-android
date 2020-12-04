package com.craigdietrich.covid19indigenous.ui.culture

import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.adapter.CultureAdapter
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.model.CultureVo
import com.dueeeke.tablayout.SegmentTabLayout
import com.dueeeke.tablayout.listener.OnTabSelectListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_cul_res.*
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.util.*
import kotlin.collections.ArrayList


class CulResFragment : Fragment() {

    private lateinit var viewModel: CulResViewModel
    private val WRITE_REQUEST_CODE = 10111

    private var responseJson = ""
    lateinit var listData: List<CultureVo>

    var culData = ArrayList<CultureVo>()
    var resData = ArrayList<CultureVo>()

    var list = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cul_res, container, false)

        Constant.changeStatusBar(isDark = true, context = context as Activity)

        val titles = arrayOf(getString(R.string.culture), getString(R.string.resilience))
        val tabAbout = root.findViewById<SegmentTabLayout>(R.id.tabAbout)
        val txtDownload = root.findViewById<TextView>(R.id.txtDownload)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)
        val llList = root.findViewById<LinearLayout>(R.id.llList)
        val llDownload = root.findViewById<LinearLayout>(R.id.llDownload)

        tabAbout.setTabData(titles)

        tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    val adapter = CultureAdapter(context as Activity, culData)
                    recyclerView.adapter = adapter
                } else {
                    val adapter = CultureAdapter(context as Activity, resData)
                    recyclerView.adapter = adapter
                }
            }

            override fun onTabReselect(position: Int) {}
        })

        txtDownload.setOnClickListener {
            if (Constant.isOnline(context as AppCompatActivity)) {

                /*try {
                    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

                    StrictMode.setThreadPolicy(policy)

                    val client: OkHttpClient = Constant.getUnsafeOkHttpClient()?.build()!!

                    val request: Request = Request.Builder()
                        .url("https://covid19indigenous.ca/feeds/content/manifest.json?t=1607063769.361629")
                        .method("GET", null)
                        .addHeader(
                            "Cookie",
                            "visid_incap_2404656=sHcz0ua6QLShh9sqkYvutnGoyF8AAAAAQUIPAAAAAAAxme0mS+ivAHOYS0TYjqYS; incap_ses_76_2404656=HECrU0IZBhu1FPJQqQEOAX8Syl8AAAAAOXoim9oAe7I46JvDjrShGQ=="
                        )
                        .build()
                    val response: Response = client.newCall(request).execute()

                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_REQUEST_CODE
                    )

                    val gson = GsonBuilder().create()

                    responseJson = response.body!!.string()

                    val data = gson.fromJson(responseJson, Array<CultureVo>::class.java).toList()

                    listData = data

                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }*/

                val stringJson = Constant.readAsset(context as Activity, "manifest.json")
                val gson = GsonBuilder().create()
                listData = gson.fromJson(stringJson, Array<CultureVo>::class.java).toList()

                setData(listData)

                //set Data
                llList.visibility = View.VISIBLE
                llDownload.visibility = View.GONE

                culData
                for (i in listData.indices) {
                    if (listData[i].category == "culture") {
                        culData.add(listData[i])
                    } else {
                        resData.add(listData[i])
                    }
                }
                recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = CultureAdapter(context as Activity, culData)
                recyclerView.adapter = adapter
            } else {
                Constant.internetAlert(context as Activity)
            }
        }
        return root
    }

    private fun setData(data: List<CultureVo>) {


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
            if (requestCode == WRITE_REQUEST_CODE) {
                writeFiles()
            }
        }

    }

    private fun writeFiles() {


        Log.e("data", listData.toString())

        val dir = File(
            Environment.getExternalStorageDirectory(),
            "/Covid19Indigenous/Documents/Content"
        )
        if (!dir.exists()) {
            dir.mkdir()
        }

        try {
            val gpxfile = File(dir, "manifest.json")
            val writer = FileWriter(gpxfile)
            writer.append(responseJson)
            writer.flush()
            writer.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        for (i in listData.indices) {
            val file = File(dir, listData[i].mp4Filename!!)

            if (!file.exists()) {
                DownloadFileFromURL().execute("https://covid19indigenous.ca/feeds/content/" + listData[i].mp4Filename)
                break
            }
        }
    }

    internal class DownloadFileFromURL :
        AsyncTask<String?, String?, String?>() {
        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        override fun onPreExecute() {
            super.onPreExecute()
            //JColorChooser.showDialog(progress_bar_type)
        }

        /**
         * Downloading file in background thread
         */
        override fun doInBackground(vararg params: String?): String? {
            var count: Int
            try {
                val url = URL(params[0])
                val connection: URLConnection = url.openConnection()
                connection.connect()

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                val lenghtOfFile: Int = connection.contentLength

                // download the file
                val input: InputStream = BufferedInputStream(
                    url.openStream(),
                    8192
                )

                // Output stream
                val output: OutputStream = FileOutputStream(
                    Environment
                        .getExternalStorageDirectory().toString()
                            + "/2011.kml"
                )
                val data = ByteArray(1024)
                var total: Long = 0
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (total * 100 / lenghtOfFile).toInt())

                    // writing data to file
                    output.write(data, 0, count)
                }

                // flushing output
                output.flush()

                // closing streams
                output.close()
                input.close()
            } catch (e: java.lang.Exception) {
                Log.e("Error: ", e.message!!)
            }
            return null
        }

        /**
         * Updating progress bar
         */
        protected override fun onProgressUpdate(vararg values: String?) {
            // setting progress percentage
            //pDialog.setProgress(progress[0].toInt())
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        override fun onPostExecute(file_url: String?) {
            // dismiss the dialog after the file was downloaded
            //dismissDialog(progress_bar_type)
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CulResViewModel::class.java)
    }

}