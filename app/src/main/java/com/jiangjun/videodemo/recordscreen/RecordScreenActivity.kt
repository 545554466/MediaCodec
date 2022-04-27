package com.jiangjun.videodemo.recordscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jiangjun.videodemo.R

/**
 * 录屏
 */
class RecordScreenActivity : AppCompatActivity(), View.OnClickListener {
    private var mMediaProjectionManager: MediaProjectionManager? = null
    private var btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_screen)
        init()
        initListener()
    }

    private fun init() {
        btn = findViewById(R.id.btn)
        checkPermission()
    }

    private fun initListener() {
        btn?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn -> start()
            else -> {
                Log.d("video", " ------- ")
            }
        }
    }


    private fun start() {
        mMediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val captureIntent: Intent? = mMediaProjectionManager!!.createScreenCaptureIntent()


        startActivityForResult(captureIntent, 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK && requestCode != 1) return

        if (data != null) {
            //获取录屏工具
            var mMediaProjection: MediaProjection? =
                mMediaProjectionManager?.getMediaProjection(resultCode, data)
            var h264Encoder = mMediaProjection?.let { H264RSEncoder(it, 640, 1920) }

            h264Encoder?.start()
        }
    }

    fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), 1
            )
        }
        return false
    }
}