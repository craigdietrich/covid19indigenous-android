package com.craigdietrich.covid19indigenous.ui.culture

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.craigdietrich.covid19indigenous.R

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

        val rdCulture = root.findViewById<RadioButton>(R.id.rdCulture)
        val rg = root.findViewById<RadioGroup>(R.id.rg)

        rdCulture.isChecked = true
        rg.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId -> // checkedId is the RadioButton selected

            if (rdCulture.isChecked) {
                //webView.loadUrl("file:///android_asset/aboutProject.html")
            } else {
                //webView.loadUrl("file:///android_asset/aboutUs.html")
            }
        })

        // for change statusbar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = (context as Activity?)!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context as Activity, R.color.grayBg)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

            val decorView: View = window.decorView
            var systemUiVisibilityFlags = decorView.systemUiVisibility
            systemUiVisibilityFlags =
                systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decorView.systemUiVisibility = systemUiVisibilityFlags
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CulResViewModel::class.java)

    }

}