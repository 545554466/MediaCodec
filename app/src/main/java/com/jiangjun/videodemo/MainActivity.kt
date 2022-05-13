package com.jiangjun.videodemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jiangjun.videodemo.camera.CameraActivity
import com.jiangjun.videodemo.multiplayervideo.accept.AcceptVideoActivity
import com.jiangjun.videodemo.multiplayervideo.send.SendVideoSendActivity
import com.jiangjun.videodemo.recordscreen.RecordScreenActivity
import com.jiangjun.videodemo.recordscreentoupin.RecordScreenTouPinActivity
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var btn1: Button? = null
    private var btn1_1: Button? = null
    private var btn2: Button? = null
    private var btn3: Button? = null
    private var btn4: Button? = null
    private var btnIP: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn1 = findViewById(R.id.btn1)
        btn1_1 = findViewById(R.id.btn1_1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btnIP = findViewById(R.id.btnIP)
        initListener()
        checkPermission()
    }

    private fun initListener() {
        btn1?.setOnClickListener(this)
        btn1_1?.setOnClickListener(this)
        btn2?.setOnClickListener(this)
        btn3?.setOnClickListener(this)
        btn4?.setOnClickListener(this)
        btnIP?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn1 -> jump(RecordScreenActivity())
            R.id.btn1_1 -> jump(RecordScreenTouPinActivity())
            R.id.btn2 -> jump(CameraActivity())
            R.id.btn3 -> jump(SendVideoSendActivity())
            R.id.btn4 -> jump(AcceptVideoActivity())
            R.id.btnIP -> {
                val localHostIp = getLocalHostIp()
                btnIP?.text = localHostIp
                Log.d("video", "ip地址为 =====   =========  ${getLocalHostIp()} ")
            }
            else -> {
                Log.d("video", " ------- ")
            }
        }
    }

    private fun jump(classes: Activity) {
        startActivity(Intent(this, classes::class.java))
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ), 1
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK && requestCode != 1) return

        if (data != null) {

        }
    }

    fun getLocalHostIp(): String? {
        try {
            val en = NetworkInterface
                .getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val ipAddr = intf.inetAddresses
                while (ipAddr
                        .hasMoreElements()
                ) {
                    val inetAddress = ipAddr.nextElement()
                    // ipv4地址
                    if (!inetAddress.isLoopbackAddress
                        && inetAddress is Inet4Address
                    ) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: Exception) {
        }

        return null
    }


}