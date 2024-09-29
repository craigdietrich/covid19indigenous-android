package com.craigdietrich.covid19indigenous.ui.culture

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.craigdietrich.covid19indigenous.PlayerActivity
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.databinding.FragmentCulResBinding
import com.craigdietrich.covid19indigenous.isLandscape
import com.craigdietrich.covid19indigenous.loadFile
import com.craigdietrich.covid19indigenous.model.CultureVo
import com.craigdietrich.covid19indigenous.retrfit.GetApi
import com.craigdietrich.covid19indigenous.retrfit.RetrofitInstance
import com.craigdietrich.covid19indigenous.writeToFile
import com.dueeeke.tablayout.listener.OnTabSelectListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.FileWriter

class CulResFragment : Fragment(), CulResAdapter.ClickListener {

    private lateinit var binding: FragmentCulResBinding
    var jsonResponse = ""

    var culData = ArrayList<CultureVo>()
    var resData = ArrayList<CultureVo>()

    private lateinit var job: Job

    companion object {
        @JvmStatic
        fun newInstance() = CulResFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCulResBinding.inflate(inflater, container, false)
        Constant.changeStatusBar(
            isDark = false,
            context = context as Activity,
            color = R.color.whiteText
        )
        (requireActivity() as CulResActivity).supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.color.whiteText)
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

        val titles = arrayOf(getString(R.string.culture), getString(R.string.resilience))
        binding.tabAbout.setTabData(titles)
        binding.tabAbout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0) {
                    val adapter = CulResAdapter(context as Activity, culData)
                    adapter.setOnItemClickListener(this@CulResFragment)
                    binding.recyclerView.adapter = adapter
                } else {
                    val adapter = CulResAdapter(context as Activity, resData)
                    adapter.setOnItemClickListener(this@CulResFragment)
                    binding.recyclerView.adapter = adapter
                }
            }

            override fun onTabReselect(position: Int) {}
        })

        checkData()

        binding.txtDownload.setOnClickListener {
            if (Constant.isOnline(requireContext())) {
                try {
                    binding.txtProgress.visibility = View.VISIBLE
                    binding.txtProgress.text = getString(R.string.download_manifest)

                    val service = RetrofitInstance.getRetrofitInstance().create(GetApi::class.java)
                    val call = service.getCultureManifest(Constant.timeStamp())
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>
                        ) {
                            try {
                                jsonResponse = response.body()!!.string()

                                if (response.isSuccessful) {
                                    job = lifecycleScope.launch(Dispatchers.IO) {
                                        delay(1500L)
                                        writeFiles()
                                    }
                                } else {
                                    response.errorBody()?.charStream()?.readText()?.let {
                                        Constant.showAlert(
                                            context as AppCompatActivity,
                                            getString(R.string.error),
                                            it
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                binding.txtProgress.visibility = View.GONE
                                Log.e("error", e.toString())
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            binding.txtProgress.visibility = View.GONE
                            Log.e("res", t.toString())
                        }
                    })
                } catch (e: Exception) {
                    binding.txtProgress.visibility = View.GONE
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

        binding.txtFetch.setOnClickListener {
            resetData()
        }

        binding.txtCancel.setOnClickListener {
            try {
                job.cancel()
                lifecycleScope.cancel()

                resetData()
            } catch (e: java.lang.Exception) {
                Log.e("error", e.toString())
            }
        }
    }

    private fun checkData() {
        try {
            val file = Constant.manifestFile()
            if (file.exists()) {
                val json = Constant.stringFromFile(Constant.manifestFile())
                val type = object : TypeToken<List<CultureVo>>() {}.type
                val listData = Gson().fromJson<List<CultureVo>>(json, type)

                for (data in listData) {
                    val image = File(Constant.culturePath(), data.thumbnailFilename)
                    val video = File(Constant.culturePath(), data.mp4Filename)

                    if (image.exists() || video.exists()) {
                        when (data.category) {
                            "culture" -> culData.add(data)
                            else -> resData.add(data)
                        }
                    }
                }

                binding.tabAbout.currentTab = 0
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = CulResAdapter(context as Activity, culData)
                adapter.setOnItemClickListener(this@CulResFragment)
                binding.recyclerView.adapter = adapter

                binding.llList.visibility = View.VISIBLE
                binding.llDownload.visibility = View.GONE
            } else {
                binding.llList.visibility = View.GONE
                binding.llDownload.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun writeFiles() {
        try {
            if (Constant.manifestFile().exists()) {
                Constant.deleteCultureFiles(Constant.culturePath())
            }

            val writer = FileWriter(Constant.manifestFile(), true)
            writer.append(jsonResponse)
            writer.flush()
            writer.close()

            val type = object : TypeToken<List<CultureVo>>() {}.type
            val listData = Gson().fromJson<List<CultureVo>>(jsonResponse, type)
            val files = mutableListOf<String>()

            listData.forEach {
                if (it.thumbnailFilename.isNotEmpty()) files.add(it.thumbnailFilename)
                if (it.mp4Filename.isNotEmpty()) files.add(it.mp4Filename)
            }

            for (i in files.indices) {
                binding.txtProgress.text = "Downloading files ${(i + 1)}/${files.size}"
                binding.seekBar.progress = (i + 1) * 100 / files.size

                val url = "${Constant.BASE_MEDIA_URL}${files[i]}"
                url.loadFile()?.let { stream ->
                    val file = File(Constant.culturePath(), files[i])
                    stream.writeToFile(file)
                }

                if (files.size - 1 == i) {
                    binding.txtProgress.text = "Downloading completed"

                    delay(1500L)

                    requireActivity().runOnUiThread { checkData() }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetData() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.reset_data))
            .setMessage(getString(R.string.are_you_sure, getString(R.string.fetch_new_content).lowercase()))
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.cancel()

                runBlocking { Constant.deleteCultureFiles(Constant.culturePath()) }

                binding.txtProgress.visibility = View.GONE
                binding.seekBar.progress = 0

                culData = ArrayList()
                resData = ArrayList()

                checkData()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    override fun onItemClick(data: CultureVo) {
        val file = File(Constant.culturePath(), data.mp4Filename)
        if (file.exists()) {
            activity?.let {
                val intent = Intent(it, PlayerActivity::class.java).putExtra("path", file.path)
                it.startActivity(intent)
            }
        } else {
            context?.let {
                Constant.showAlert(
                    it, getString(R.string.file_not_found), getString(R.string.not_found_desc)
                )
            }
        }
    }

    override fun onLinkClick(uri: String) {
        var url = uri

        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://$url"

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context?.startActivity(browserIntent)
    }
}