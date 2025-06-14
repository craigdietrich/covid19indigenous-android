package com.craigdietrich.covid19indigenous

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.common.Constant.Companion.DoubleClickListener
import com.craigdietrich.covid19indigenous.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class PlayerActivity : AppCompatActivity(), Player.EventListener {

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private var vidUrl = ""
    lateinit var mainHandler: Handler
    private var isFull = false

    private lateinit var binding : ActivityPlayerBinding

    private val updateTextTask = object : Runnable {
        override fun run() {
            binding.txtMin.text = Constant.millsToHS(simpleExoplayer.currentPosition)
            binding.txtMinRem.text =
                Constant.millsToRemainingHS(simpleExoplayer.duration - simpleExoplayer.currentPosition)
            mainHandler.postDelayed(this, 1)

            binding.seekBar.progress =
                ((binding.seekBar.max * simpleExoplayer.currentPosition) / simpleExoplayer.duration).toInt()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { onStop() }
        })

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constant.changeStatusBar(isDark = true, context = this, color = R.color.black)

        vidUrl = intent.getStringExtra("path")!!

        mainHandler = Handler(Looper.getMainLooper())

        initializePlayer()

        binding.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                val offset = ((seek.progress.toDouble() / seek.max))
                simpleExoplayer.seekTo(((offset * simpleExoplayer.duration).toLong()))

            }
        })

        binding.llPlayer.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {}
            override fun onDoubleClick(v: View?) {
                setFullScreen()
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "exoplayer-sample")
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(this).build()
        preparePlayer(vidUrl)
        binding.exoplayerView.player = simpleExoplayer
        val firstItem: MediaItem = MediaItem.fromUri(vidUrl)
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.addMediaItem(firstItem)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.prepare()
        simpleExoplayer.addListener(this)
        mainHandler.post(updateTextTask)

        setCustomControl()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setCustomControl() {
        binding.imgPlay.setBackgroundResource(R.drawable.pause)
        binding.imgVolume.setBackgroundResource(R.drawable.volume)
        binding.imgFull.setBackgroundResource(R.drawable.full)

        binding.imgPlay.setOnClickListener {
            if (simpleExoplayer.isPlaying) {
                simpleExoplayer.pause()
                binding.imgPlay.setBackgroundResource(R.drawable.play)
            } else {
                simpleExoplayer.play()
                binding.imgPlay.setBackgroundResource(R.drawable.pause)
            }
        }

        binding.imgBackward.setOnClickListener {
            simpleExoplayer.seekTo(simpleExoplayer.currentPosition - 15000)
        }
        binding.imgForward.setOnClickListener {
            simpleExoplayer.seekTo(simpleExoplayer.currentPosition + 15000)
        }

        var currentVolume = simpleExoplayer.volume

        binding.llVolume.setOnClickListener {
            if (simpleExoplayer.volume == 0f) {
                simpleExoplayer.volume = currentVolume
                binding.imgVolume.setBackgroundResource(R.drawable.volume)
            } else {
                currentVolume = simpleExoplayer.volume
                simpleExoplayer.volume = 0f
                binding.imgVolume.setBackgroundResource(R.drawable.mute)
            }
        }

        binding.imgClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.imgFull.setOnClickListener {
            setFullScreen()
        }
    }

    private fun setFullScreen() {
        if (isFull) {
            binding.imgFull.setBackgroundResource(R.drawable.full)
            binding.exoplayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        } else {
            binding.imgFull.setBackgroundResource(R.drawable.exit)
            binding.exoplayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
        isFull = !isFull
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun preparePlayer(videoUrl: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri)
        simpleExoplayer.prepare(mediaSource)
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.release()
        mainHandler.removeCallbacks(updateTextTask)
    }

    override fun onPlayerError(error: PlaybackException) {
        // handle error
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                binding.progressBar.visibility = View.INVISIBLE
            }
            Player.STATE_ENDED -> {
                initializePlayer()
            }
            Player.STATE_IDLE -> {
                initializePlayer()
            }
        }
    }
}