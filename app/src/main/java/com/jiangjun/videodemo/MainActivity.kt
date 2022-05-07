package com.jiangjun.videodemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jiangjun.videodemo.camera.CameraActivity
import com.jiangjun.videodemo.recordscreen.RecordScreenActivity
import com.jiangjun.videodemo.recordscreentoupin.RecordScreenTouPinActivity
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var btn1: Button? = null
    private var btn1_1: Button? = null
    private var btn2: Button? = null
    private var btnIP: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn1 = findViewById(R.id.btn1)
        btn1_1 = findViewById(R.id.btn1_1)
        btn2 = findViewById(R.id.btn2)
        btnIP = findViewById(R.id.btnIP)
        initListener()
    }

    private fun initListener() {
        btn1?.setOnClickListener(this)
        btn1_1?.setOnClickListener(this)
        btn2?.setOnClickListener(this)
        btnIP?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn1 -> jump(RecordScreenActivity())
            R.id.btn1_1 -> jump(RecordScreenTouPinActivity())
            R.id.btn2 -> jump(CameraActivity())
            R.id.btnIP -> {
                val localHostIp = getLocalHostIp()
                Log.d("video", "ip地址为 =====   =========  ${getLocalHostIp()} ")
                Log.d("video", " 宽为=====   =========  {${getScreenWidth(this)}} ")
                Log.d("video", " 高为=====   =========  {${getScreenHeight(this)}} ")
            }
            else -> {
                Log.d("video", " ------- ")
            }
        }
    }

    private fun jump(classes: Activity) {
        startActivity(Intent(this, classes::class.java))
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

    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}