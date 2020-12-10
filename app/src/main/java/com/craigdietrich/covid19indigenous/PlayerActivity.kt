package com.craigdietrich.covid19indigenous

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.craigdietrich.covid19indigenous.common.Constant
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity(), Player.EventListener {

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private var dashUrl = ""
    lateinit var mainHandler: Handler
    var isFull = false
    var isMute = false

    private val updateTextTask = object : Runnable {
        override fun run() {
            txtMin.text = Constant.millsToHS(simpleExoplayer.currentPosition)
            txtMinRem.text =
                Constant.millsToRemainingHS(simpleExoplayer.duration - simpleExoplayer.currentPosition)
            mainHandler.postDelayed(this, 1)

            seekBar.progress =
                ((seekBar.max * simpleExoplayer.currentPosition) / simpleExoplayer.duration).toInt()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        Constant.changeStatusBar(isDark = true, context = this, color = R.color.black)

        dashUrl = intent.getStringExtra("path")!!

        mainHandler = Handler(Looper.getMainLooper())

        initializePlayer()

        seekBar?.setOnSeekBarChangeListener(object :
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
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
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
        preparePlayer(dashUrl)
        exoplayerView.player = simpleExoplayer
        val firstItem: MediaItem = MediaItem.fromUri(dashUrl)
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

        imgPlay.setBackgroundResource(R.drawable.pause)
        imgVolume.setBackgroundResource(R.drawable.volume)
        imgFull.setBackgroundResource(R.drawable.full)

        imgPlay.setOnClickListener {
            if (simpleExoplayer.isPlaying) {
                simpleExoplayer.pause()
                imgPlay.setBackgroundResource(R.drawable.play)
            } else {
                simpleExoplayer.play()
                imgPlay.setBackgroundResource(R.drawable.pause)
            }
        }

        imgBackward.setOnClickListener {
            simpleExoplayer.seekTo(simpleExoplayer.currentPosition - 15000)
        }
        imgForward.setOnClickListener {
            simpleExoplayer.seekTo(simpleExoplayer.currentPosition + 15000)
        }

        var currentVolume = simpleExoplayer.volume

        llVolume.setOnClickListener {
            if (simpleExoplayer.volume == 0f) {
                simpleExoplayer.volume = currentVolume
                imgVolume.setBackgroundResource(R.drawable.volume)
            } else {
                currentVolume = simpleExoplayer.volume
                simpleExoplayer.volume = 0f
                imgVolume.setBackgroundResource(R.drawable.mute)
            }
        }

        imgClose.setOnClickListener {
            onStop()
            onBackPressed()
        }

        imgFull.setOnClickListener {
            if (isFull) {
                imgFull.setBackgroundResource(R.drawable.full)
                exoplayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            } else {
                imgFull.setBackgroundResource(R.drawable.exit)
                exoplayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
            isFull = !isFull
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
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

    override fun onPlayerError(error: ExoPlaybackException) {
        // handle error
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                progressBar.visibility = View.INVISIBLE
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