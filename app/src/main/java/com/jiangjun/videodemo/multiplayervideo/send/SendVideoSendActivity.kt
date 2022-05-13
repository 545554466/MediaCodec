package com.jiangjun.videodemo.multiplayervideo.send

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

class SendVideoSendActivity : AppCompatActivity(), SendSocketClient.SocketCallback {

    private var surfaceView: SurfaceView? = null
    private var surfaceViewMV: SendSurfaceView? = null
    private var mvDecoderPullLive: SendDecoderPullLive? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multiplayer_send_video_activity)
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
                mvDecoderPullLive = SendDecoderPullLive(
                    ScreenUtils.getScreenWidth(this@SendVideoSendActivity),
                    ScreenUtils.getScreenHeight(
                        this@SendVideoSendActivity
                    ),
                    surface
                )
            }
        })
    }

    fun connectH264(view: View) {
        surfaceViewMV?.startCaptures(MediaFormat.MIMETYPE_VIDEO_AVC, this)

        AudioRecordUtils.let {
            it.initAudioTrack()
            it.startRecord(surfaceViewMV?.getSocket())
        }
    }

    fun connectH265(view: View) {
        surfaceViewMV?.startCaptures(MediaFormat.MIMETYPE_VIDEO_HEVC, this)
    }

    override fun callBack(data: ByteArray?) {
        if (data!![0] == 1.toByte()) {
            if (mvDecoderPullLive != null) {
                mvDecoderPullLive?.decoderFrame(data)
            }
        } else {
            AudioRecordUtils.doPlay(data)
        }

    }
}