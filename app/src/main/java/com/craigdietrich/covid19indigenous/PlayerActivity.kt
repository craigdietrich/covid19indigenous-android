package com.craigdietrich.covid19indigenous

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.craigdietrich.covid19indigenous.common.Constant
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity(), Player.EventListener {

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private var dashUrl = ""
    //private val urlList = listOf(mp4Url to "default", dashUrl to "dash")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Constant.changeStatusBar(isDark = true, context = this, color = R.color.black)

        setContentView(R.layout.activity_player)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        dashUrl = intent.getStringExtra("path")!!
        initializePlayer()
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
        simpleExoplayer.play()
        simpleExoplayer.addListener(this)
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
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        // handle error
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            progressBar.visibility = View.INVISIBLE
    }
}