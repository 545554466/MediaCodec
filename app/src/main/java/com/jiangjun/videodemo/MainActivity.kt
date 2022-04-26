package com.jiangjun.videodemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.jiangjun.videodemo.recordscreen.RecordScreenActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
   private var btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         btn = findViewById(R.id.btn)
        initListener()
    }

    private fun initListener() {
        btn?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn -> recordScreen()
            else -> {
                Log.d("video", " ------- ")
            }
        }
    }

    private fun recordScreen() {
        startActivity(Intent(this,RecordScreenActivity::class.java))
    }
}