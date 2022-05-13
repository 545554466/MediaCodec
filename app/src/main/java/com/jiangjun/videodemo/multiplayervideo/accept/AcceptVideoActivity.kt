package com.jiangjun.videodemo.multiplayervideo.accept

import android.media.MediaFormat
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jiangjun.videodemo.R
import com.jiangjun.videodemo.utils.AudioRecordUtils
import com.jiangjun.videodemo.utils.ScreenUtils

class AcceptVideoActivity : AppCompatActivity(), AcceptSocketServer.scocketCallBack {

    private var surfaceView: SurfaceView? = null
    private var surfaceViewMV: AcceptSurfaceView? = null
    private var mvDecoderPullLive: AcceptDecoderPullLive? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multiplayer_accept_video_activity)
        initView()

    }

    private fun initView() {
        surfaceView = findViewById(R.id.surfaceView)
        surfaceViewMV = findViewById(R.id.MVSurfaceView)
        surfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                val surface: Surface = holder.surface
                mvDecoderPullLive = AcceptDecoderPullLive(
                    ScreenUtils.getScreenWidth(this@AcceptVideoActivity),
                    ScreenUtils.getScreenHeight(
                        this@AcceptVideoActivity
                    ),
                    surface
                )
            }
        })
    }

    override fun callBack(data: ByteArray) {
        if (data[0] == 1.toByte()) {
            if (null != mvDecoderPullLive) {
                mvDecoderPullLive?.decoderFrame(data)
            }
        } else {
            AudioRecordUtils.doPlay(data)
        }

    }

    fun start(view: View) {
        surfaceViewMV?.startCaptures(MediaFormat.MIMETYPE_VIDEO_AVC, this)
        AudioRecordUtils.let {
            it.initAudioTrack()
            it.startRecord(surfaceViewMV?.getSocket())
        }
    }

}