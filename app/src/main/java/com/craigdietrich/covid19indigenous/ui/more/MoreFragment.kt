package com.craigdietrich.covid19indigenous.ui.more

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.databinding.FragmentMoreBinding
import com.craigdietrich.covid19indigenous.isLandscape
import kotlinx.coroutines.runBlocking

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        Constant.changeStatusBar(
            isDark = false,
            context = context as Activity,
            color = if (resources.isLandscape) R.color.whiteText else R.color.colorPrimary
        )
        Constant.changeSystemNavBarColor(
            context = context as Activity,
            color = ContextCompat.getColor(requireContext(), R.color.whiteText)
        )
        return binding.root
    }

    fun checkData() {
        val sendResultsToServer = Constant.readSP(requireContext(), Constant.SEND_RESULTS_TO_SERVER)
        if (sendResultsToServer.isNotEmpty() && sendResultsToServer == "true") {
            binding.btnRestrictedResultSend.setBackgroundResource(R.drawable.bg_red_rounded)
            binding.txtRestrictedResultSend.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.white
                )
            )
            binding.btnRestrictedResultSend.tag = "true"
        } else {
            binding.btnRestrictedResultSend.setBackgroundResource(R.drawable.bg_white_rounded)
            binding.txtRestrictedResultSend.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blueText
                )
            )
            binding.btnRestrictedResultSend.tag = "false"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkData()

        binding.btnConversations.setOnClickListener {
            val options = NavOptions.Builder().apply {
                setEnterAnim(R.anim.slide_left)
                setExitAnim(R.anim.wait_anim)
                setPopEnterAnim(R.anim.wait_anim)
                setPopExitAnim(R.anim.slide_right)
            }.build()
            findNavController().navigate(R.id.navigation_culture, navOptions = options, args = null)
        }

        binding.btnPastSubmission.setOnClickListener {
            val options = NavOptions.Builder().apply {
                setEnterAnim(R.anim.slide_left)
                setExitAnim(R.anim.wait_anim)
                setPopEnterAnim(R.anim.wait_anim)
                setPopExitAnim(R.anim.slide_right)
            }.build()
            findNavController().navigate(R.id.navigation_past, navOptions = options, args = null)
        }

        binding.btnRestrictedResultSend.setOnClickListener {
            if (binding.btnRestrictedResultSend.tag == "true") {
                Constant.writeSP(requireContext(), Constant.SEND_RESULTS_TO_SERVER, "false")
            } else {
                Constant.writeSP(requireContext(), Constant.SEND_RESULTS_TO_SERVER, "true")
            }
            checkData()
        }

        binding.btnDeleteCode.setOnClickListener {
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