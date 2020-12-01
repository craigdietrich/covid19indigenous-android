package com.craigdietrich.covid19indigenous.ui.culture

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import com.craigdietrich.covid19indigenous.R
import kotlinx.android.synthetic.main.fragment_culture.*

class CultureFragment : Fragment() {

    companion object {
        fun newInstance() = CultureFragment()
    }

    private lateinit var viewModel: CultureViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_culture, container, false)

        val cultureButton = root.findViewById<TextView>(R.id.button6)
        val resilienceButton = root.findViewById<TextView>(R.id.button7)

        cultureButton.setTextColor(resources.getColor(R.color.colorPrimary))
        resilienceButton.setTextColor(Color.DKGRAY)



        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CultureViewModel::class.java)

    }

}