package com.jiangjun.videodemo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.jiangjun.videodemo.camera.CameraActivity
import com.jiangjun.videodemo.recordscreen.RecordScreenActivity
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
   private var btn1: Button? = null
   private var btn2: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        initListener()
    }

    private fun initListener() {
        btn1?.setOnClickListener(this)
        btn2?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn1 -> jump(RecordScreenActivity())
            R.id.btn2 -> jump(CameraActivity())
            else -> {
                Log.d("video", " ------- ")
            }
        }
    }

    private fun jump(classes : Activity) {
//        startActivity(Intent(this, RecordScreenActivity::class.java))
        startActivity(Intent(this, classes::class.java))
    }
}