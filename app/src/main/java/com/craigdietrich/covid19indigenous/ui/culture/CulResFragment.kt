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
import com.craigdietrich.covid19indigenous.retrfit.GetApi
import com.craigdietrich.covid19indigenous.retrfit.RetrofitInstance
import com.dueeeke.tablayout.listener.OnTabSelectListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_cul_res.*
import kotlinx.android.synthetic.main.fragment_cul_res.view.*
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class CulResFragment : Fragment(), CultureAdapter.ClickListener {

    private lateinit var viewModel: CulResViewModel
    private val writeRequestCode = 10111

    lateinit var listData: List<CultureVo>

    var culData = ArrayList<CultureVo>()
    var resData = ArrayList<CultureVo>()
    private var root: View? = null

    private var dir = File(
        Environment.getExternalStorageDirectory().absolutePath,
        "/Covid19Indigenous"
    )

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

        if (!dir.exists()) {
            dir.mkdir()
        }

        dir = File(dir, "/Content")
        if (!dir.exists()) {
            dir.mkdir()
        }


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
                    txtProgress.visibility = View.VISIBLE
                    txtProgress.text = getString(R.string.download_manifest)

                    val service: GetApi =
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
                            listData = response.body() as List<CultureVo>

                            checkData()

                            requestPermissions(
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                writeRequestCode
                            )
                        }

                        override fun onFailure(call: Call<List<CultureVo>>, t: Throwable) {
                            txtProgress.visibility = View.GONE
                            Log.e("res", t.toString())
                        }
                    })
                } catch (e: Exception) {
                    txtProgress.visibility = View.GONE
                    Log.e("error", e.toString())
                }
            } else {
                Constant.internetAlert(context as Activity)
            }
        }

        root!!.txtFetch.setOnClickListener {
            resetData()
        }

        root!!.txtCancel.setOnClickListener {

            Constant.myTask!!.cancel(true)
            resetData()

        }
        return root
    }

    private fun checkData() {

        val file = File(dir, "manifest.json")

        if (file.exists()) {

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
            }
            //val stringJson = Constant.readAsset(context as Activity, "manifest.json")
            val gson = GsonBuilder().create()
            listData = gson.fromJson(text.toString(), Array<CultureVo>::class.java).toList()

            for (i in listData.indices) {

                val image = File(dir, listData[i].thumbnailFilename)
                val video = File(dir, listData[i].mp4Filename)

                if (image.exists() || video.exists()) {
                    if (listData[i].category == "culture") {
                        culData.add(listData[i])
                    } else {
                        resData.add(listData[i])
                    }
                }
            }
            root!!.recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = CultureAdapter(context as Activity, culData)
            adapter.setOnItemClickListener(this@CulResFragment)
            root!!.recyclerView.adapter = adapter

            root!!.llList.visibility = View.VISIBLE
            root!!.llDownload.visibility = View.GONE
        } else {
            root!!.llList.visibility = View.GONE
            root!!.llDownload.visibility = View.VISIBLE
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
            Log.e("data", listData.toString())

            val gson = GsonBuilder().create()
            val responseJson = gson.toJsonTree(listData).asJsonArray

            try {
                val gpxfile = File(dir, "manifest.json")
                val writer = FileWriter(gpxfile)
                writer.append(responseJson.toString())
                writer.flush()
                writer.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            Constant.myTask = DownloadFileFromURL(
                data = listData,
                pos = 0,
                dir = dir,
                cContext = this,
                type = "image"
            ).execute() as DownloadFileFromURL?
        } catch (e: java.lang.Exception) {

        }
    }

    class DownloadFileFromURL internal constructor(
        private var data: List<CultureVo>,
        private var pos: Int,
        private var dir: File,
        private var cContext: CulResFragment,
        private var type: String
    ) : AsyncTask<String?, String?, String?>() {
        @SuppressLint("SetTextI18n")
        override fun onPreExecute() {
            super.onPreExecute()

            if (isCancelled) {
                return
            } else {
                cContext.txtProgress.visibility = View.VISIBLE
                if (type == "image") {
                    cContext.root!!.txtProgress.text =
                        "Downloading thumb " + (pos + 1) + "/" + data.size
                } else {
                    cContext.root!!.txtProgress.text =
                        "Downloading video " + (pos + 1) + "/" + data.size
                }

                cContext.root!!.seekBar.progress = (pos + 1) * 100 / data.size
            }
        }

        override fun doInBackground(vararg params: String?): String? {

            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val url = if (type == "image") {
                    URL(Constant.BASE_MEDIA_URL + data[pos].thumbnailFilename)
                } else {
                    URL(Constant.BASE_MEDIA_URL + data[pos].mp4Filename)
                }
                connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty(
                    "Cookie",
                    Constant.cookie
                )
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.responseCode
                        .toString() + " " + connection.responseMessage
                }

                val fileLength: Int = connection.contentLength

                // download the file
                input = connection.inputStream


                val file: File = if (type == "image") {
                    File(dir, data[pos].thumbnailFilename)
                } else {
                    File(dir, data[pos].mp4Filename)
                }

                if (file.exists()) {
                    return null
                }
                output = FileOutputStream(file)
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    // allow canceling with back button
                    if (isCancelled) {
                        input.close()
                        return null
                    }
                    total += count.toLong()

                    if (fileLength > 0) // only if total length is known
                        publishProgress((total * 100 / fileLength).toInt().toString())
                    output.write(data, 0, count)
                }
            } catch (e: java.lang.Exception) {
                return e.toString()
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (ignored: IOException) {
                }
                connection?.disconnect()
            }
            return null
        }

        override fun onProgressUpdate(vararg values: String?) {
            if (isCancelled) {
                return
            }
        }

        override fun onPostExecute(file_url: String?) {
            if (isCancelled) {
                return
            } else {
                if (pos != data.size - 1 || type == "image") {

                    if (type == "image") {
                        Constant.myTask = DownloadFileFromURL(
                            data = data,
                            pos = pos,
                            dir = dir,
                            cContext = cContext,
                            type = "video"
                        ).execute() as DownloadFileFromURL?
                    } else {
                        Constant.myTask = DownloadFileFromURL(
                            data = data,
                            pos = pos + 1,
                            dir = dir,
                            cContext = cContext,
                            type = "image"
                        ).execute() as DownloadFileFromURL?
                    }

                } else {
                    cContext.checkData()
                    cContext.root!!.llList.visibility = View.VISIBLE
                    cContext.root!!.llDownload.visibility = View.GONE
                }
            }
        }
    }

    private fun resetData() {
        runBlocking {
            Constant.deleteFiles(dir)
        }

        txtProgress.visibility = View.GONE
        seekBar.progress = 0
        checkData()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CulResViewModel::class.java)
    }

    override fun onItemClick(data: CultureVo) {

        val file = File(dir, data.mp4Filename)

        if (file.exists()) {
            activity?.let {
                val intent = Intent(it, PlayerActivity::class.java).putExtra("path", file.path)
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