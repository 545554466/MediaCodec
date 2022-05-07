package com.jiangjun.videodemo.recordscreentoupin

import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.jiangjun.videodemo.R
import com.jiangjun.videodemo.socket.SocketLiveClient

class RecordScreenTouPinActivity : AppCompatActivity(), SurfaceHolder.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_screen_tou_pin)
        val sfv: SurfaceView = findViewById(R.id.sfv)
        sfv.holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val surface: Surface = holder.surface
        val videoPlay = VideoPlay(surface)
        val client = SocketLiveClient(videoPlay, 8099)
        client.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
}