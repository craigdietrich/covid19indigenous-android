package com.craigdietrich.covid19indigenous.ui.pastSubmissions

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_SEND_MULTIPLE
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.databinding.FragmentPastSubmissionsBinding
import com.craigdietrich.covid19indigenous.isLandscape
import kotlinx.coroutines.runBlocking
import java.io.File

class PastSubmissionFragment : Fragment() {

    private lateinit var binding: FragmentPastSubmissionsBinding
    private lateinit var pastAdapter: PastSubmissionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPastSubmissionsBinding.inflate(inflater, container, false)
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
        (requireActivity() as PastSubmissionActivity).supportActionBar?.title =
            getString(R.string.past_submissions)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runBlocking {
            PastSubmissions.resetData()
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                val data = PastSubmissions.ITEMS.sortedBy { it.modified }.toMutableList()
                pastAdapter = PastSubmissionAdapter(data, object : OnItemClickListener {
                    override fun onShare(file: File) {
                        try {
                            ShareCompat.IntentBuilder(requireContext()).apply {
                                setStream(FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file))
                                setType("*/*")
                            }.startChooser()
                        } catch (e: Exception) {
                            Log.e("onShare::", "${e.message}")
                        }
                    }

                    override fun onPreview(path: String) {
                        findNavController().navigate(
                            R.id.action_preview,
                            bundleOf("answers" to path)
                        )
                    }
                })
                (requireActivity() as PastSubmissionActivity).setOnSelectAllListener(object :
                    OnActionListener {
                    override fun onDelete() {
                        val selectedFiles = pastAdapter.getSelectedFiles()
                        selectedFiles.forEach {
                            val position = pastAdapter.values.indexOf(it)
                            if (position != -1) {
                                pastAdapter.values.removeAt(position)
                                pastAdapter.notifyItemRemoved(position)
                            }
                            it.file.delete()
                        }
                        pastAdapter.selectionEnable = !pastAdapter.selectionEnable
                        PastSubmissions.selectionEnable.postValue(false)
                    }

                    override fun onShare() {
                        try {
                            ShareCompat.IntentBuilder(requireContext()).apply {
                                pastAdapter.getSelectedFiles().forEach {
                                    addStream(FileProvider.getUriForFile(
                                        requireContext(),
                                        requireContext().packageName + ".provider",
                                        it.file,
                                        it.name,
                                    ))
                                }
                                setType("*/*")
                            }.startChooser()
                        } catch (e: Exception) {
                            Log.e("onShare::", "${e.message}")
                        }
                    }
                })
                adapter = pastAdapter
            }
        }
    }
}