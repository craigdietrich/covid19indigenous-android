package com.craigdietrich.covid19indigenous.ui.pastSubmissions

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.databinding.ActivityPastSubmissionBinding

class PastSubmissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPastSubmissionBinding
    private lateinit var onActionListener: OnActionListener

    private lateinit var navController: NavController

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPastSubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@PastSubmissionActivity,
                R.color.whiteText
            )
        )

        navController = findNavController(R.id.nav_past_container)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_preview -> {
                    menu?.findItem(R.id.share)?.setVisible(true)
                    menu?.findItem(R.id.delete)?.setVisible(false)
                }

                else -> {
                    menu?.findItem(R.id.share)?.setVisible(false)
                    menu?.findItem(R.id.delete)?.setVisible(false)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        PastSubmissions.selectionEnable.observe(this, ::selectionObserver)
    }

    override fun onStop() {
        PastSubmissions.selectionEnable.removeObserver(::selectionObserver)
        super.onStop()
    }

    private fun selectionObserver(value: Boolean) {
        menu?.findItem(R.id.share)?.setVisible(value)
        menu?.findItem(R.id.delete)?.setVisible(value)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.past_submission_menu, menu)
        menu?.findItem(R.id.share)?.setVisible(false)
        menu?.findItem(R.id.delete)?.setVisible(false)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            R.id.share -> {
                onActionListener.onShare()
                true
            }

            R.id.delete -> {
                onActionListener.onDelete()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setOnSelectAllListener(onActionListener: OnActionListener) {
        this.onActionListener = onActionListener
    }
}