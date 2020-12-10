package com.craigdietrich.covid19indigenous.ui.culture

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.craigdietrich.covid19indigenous.PlayerActivity
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.adapter.CultureAdapter
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.model.CultureVo
import com.dueeeke.tablayout.listener.OnTabSelectListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_cul_res.*
import kotlinx.android.synthetic.main.fragment_cul_res.view.*
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.util.*
import kotlin.collections.ArrayList


class CulResFragment : Fragment(), CultureAdapter.ClickListener {

    private lateinit var viewModel: CulResViewModel
    private val WRITE_REQUEST_CODE = 10111

    private var responseJson = ""
    lateinit var listData: List<CultureVo>

    var culData = ArrayList<CultureVo>()
    var resData = ArrayList<CultureVo>()

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_cul_res, container, false)

        Constant.changeStatusBar(
            isDark = true,
            context = context as Activity,
            color = R.color.grayBg
        )

        val titles = arrayOf(getString(R.string.culture), getString(R.string.resilience))
        root!!.tabAbout.setTabData(titles)
        root!!.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    val adapter = CultureAdapter(context as Activity, culData)
                    adapter.setOnItemClickListener(this@CulResFragment)
                    root!!.recyclerView.adapter = adapter
                } else {
                    val adapter = CultureAdapter(context as Activity, resData)
                    adapter.setOnItemClickListener(this@CulResFragment)
                    root!!.recyclerView.adapter = adapter
                }
            }

            override fun onTabReselect(position: Int) {}
        })

        checkData()

        root!!.txtDownload.setOnClickListener {
            if (Constant.isOnline(context as AppCompatActivity)) {

                try {

                    /*val service: GetApi =
                        RetrofitInstance.getRetrofitInstance().create(
                            GetApi::class.java
                        )
                    val call = service.getCultureManifest(Constant.TIME, Constant.cookie)
                    call.enqueue(object : Callback<List<CultureVo>> {
                        override fun onResponse(
                            call: Call<List<CultureVo>>,
                            response: retrofit2.Response<List<CultureVo>>
                        ) {
                            Log.e("res", response.body().toString())

                            val gson = GsonBuilder().create()

                            listData = response.body() as List<CultureVo>

                            *//*val data = gson.fromJson(responseJson, Array<CultureVo>::class.java).toList()

                            listData = data*//*

                            setData()

                        }

                        override fun onFailure(call: Call<List<CultureVo>>, t: Throwable) {
                            Log.e("res", t.toString())
                        }
                    })*/


                    /*val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

                    StrictMode.setThreadPolicy(policy)

                    val client: OkHttpClient = Constant.getUnsafeOkHttpClient().build()

                    val request: Request = Request.Builder()
                        .url(Constant.BASE_URL + Constant.CULTURE)
                        .method("GET", null)
                        .addHeader(
                            "Cookie",
                            Constant.cookie
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

                    setData()*/

                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }


                //offline
                val stringJson = Constant.readAsset(context as Activity, "manifest.json")
                val gson = GsonBuilder().create()
                listData = gson.fromJson(stringJson, Array<CultureVo>::class.java).toList()

                setData()

            } else {
                Constant.internetAlert(context as Activity)
            }
        }

        root!!.txtFetch.setOnClickListener {

        }
        return root
    }

    private fun checkData() {

        val dir = File(
            Environment.getExternalStorageDirectory(),
            "/Covid19Indigenous"
        )

        val file = File(dir, "manifest.json")

        if (file.exists()) {

            val stringJson = Constant.readAsset(context as Activity, "manifest.json")
            val gson = GsonBuilder().create()
            listData = gson.fromJson(stringJson, Array<CultureVo>::class.java).toList()

            for (i in listData.indices) {
                if (listData[i].category == "culture") {
                    culData.add(listData[i])
                } else {
                    resData.add(listData[i])
                }
            }
            root!!.recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = CultureAdapter(context as Activity, culData)
            adapter.setOnItemClickListener(this@CulResFragment)
            root!!.recyclerView.adapter = adapter

            root!!.llList.visibility = View.VISIBLE
            root!!.llDownload.visibility = View.GONE
        }
    }

    private fun setData() {

        for (i in listData.indices) {
            if (listData[i].category == "culture") {
                culData.add(listData[i])
            } else {
                resData.add(listData[i])
            }
        }
        root!!.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = CultureAdapter(context as Activity, culData)
        adapter.setOnItemClickListener(this@CulResFragment)
        root!!.recyclerView.adapter = adapter

        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_REQUEST_CODE
        )
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

        try {
            Log.e("data", listData.toString())

            val dir = File(
                Environment.getExternalStorageDirectory(),
                "/Covid19Indigenous"
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

            DownloadFileFromURL(
                data = listData,
                cookie = Constant.cookie,
                pos = 0,
                dir = dir,
                cContext = this,
                type = "image"
            ).execute()
        } catch (e: java.lang.Exception) {

        }
    }

    class DownloadFileFromURL internal constructor(
        private var data: List<CultureVo>,
        private var cookie: String,
        private var pos: Int,
        private var dir: File,
        private var cContext: CulResFragment,
        private var type: String
    ) : AsyncTask<String?, String?, String?>() {
        @SuppressLint("SetTextI18n")
        override fun onPreExecute() {
            super.onPreExecute()

            cContext.txtProgress.visibility = View.VISIBLE
            if (type == "image") {
                cContext.root!!.txtProgress.text =
                    "Downloading image " + (pos + 1) + "/" + data.size
            } else {
                cContext.root!!.txtProgress.text =
                    "Downloading video " + (pos + 1) + "/" + data.size
            }

            cContext.root!!.seekBar.progress = pos * 100 / data.size
        }

        override fun doInBackground(vararg params: String?): String? {
            var count: Int
            try {

                val url = if (type == "image") {
                    URL(Constant.BASE_MEDIA_URL + data[pos].thumbnailFilename)
                } else {
                    URL(Constant.BASE_MEDIA_URL + data[pos].mp4Filename)
                }

                val connection: URLConnection = url.openConnection()
                connection.setRequestProperty(
                    "Cookie",
                    cookie
                );
                connection.connect()

                val file: File = if (type == "image") {
                    File(dir, data[pos].thumbnailFilename)
                } else {
                    File(dir, data[pos].mp4Filename)
                }


                if (file.exists()) {
                    return null
                } else {
                    val lengthOfFile: Int = connection.contentLength
                    val input: InputStream = BufferedInputStream(
                        url.openStream(),
                        8192
                    )

                    val output: OutputStream = FileOutputStream(
                        file
                    )
                    val data = ByteArray(1024)
                    var total: Long = 0
                    while (input.read(data).also { count = it } != -1) {
                        total += count.toLong()
                        publishProgress("" + (total * 100 / lengthOfFile).toInt())
                        output.write(data, 0, count)
                    }
                    output.flush()
                    output.close()
                    input.close()
                }
            } catch (e: java.lang.Exception) {
                Log.e("Error: ", e.message!!)
            }
            return null
        }

        override fun onProgressUpdate(vararg values: String?) {
        }

        override fun onPostExecute(file_url: String?) {
            if (pos != data.size - 1 || type == "image") {

                if (type == "image") {
                    DownloadFileFromURL(
                        data = data,
                        cookie = cookie,
                        pos = pos,
                        dir = dir,
                        cContext = cContext,
                        type = "video"
                    ).execute()
                } else {
                    DownloadFileFromURL(
                        data = data,
                        cookie = cookie,
                        pos = pos + 1,
                        dir = dir,
                        cContext = cContext,
                        type = "image"
                    ).execute()
                }

            } else {
                cContext.root!!.llList.visibility = View.VISIBLE
                cContext.root!!.llDownload.visibility = View.GONE
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CulResViewModel::class.java)
    }

    override fun onItemClick(data: CultureVo) {

        val dir = File(
            Environment.getExternalStorageDirectory(),
            "/Covid19Indigenous"
        )
        if (!dir.exists()) {
            dir.mkdir()
        }

        val gpxfile = File(dir, data.mp4Filename)

        if (gpxfile.exists()) {
            activity?.let {
                val intent = Intent(it, PlayerActivity::class.java).putExtra("path", gpxfile.path)
                it.startActivity(intent)
            }
        } else {
            context?.let {
                Constant.showAlert(
                    it,
                    "File not found",
                    "This content has not been downloaded. Please refresh content and try again."
                )
            }
        }

    }

}