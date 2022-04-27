package com.jiangjun.videodemo.widget

import android.content.Context
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.jiangjun.videodemo.camera.H264CEncoder

class LocalSurfaceView(context: Context?, attrs: AttributeSet?) : SurfaceView(context, attrs),
    SurfaceHolder.Callback, PreviewCallback {
    private var mCamera: Camera? = null
    private lateinit var size: Camera.Size
    private lateinit var buffer: ByteArray
    private var h264CEncoder: H264CEncoder? = null

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startPreview()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    private fun startPreview() {
        // 开启摄像头
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        val parameters: Camera.Parameters? = mCamera?.parameters
        size = parameters!!.previewSize
        mCamera?.apply {
            setPreviewDisplay(holder)
            setDisplayOrientation(90)
            buffer = ByteArray(size.width * size.height * 3 / 2)
            addCallbackBuffer(buffer)
            setPreviewCallbackWithBuffer(this@LocalSurfaceView)
            startPreview()
        }
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        if (null == h264CEncoder) {
            h264CEncoder = H264CEncoder(size.width, size.height)
            h264CEncoder?.initMediaCodec()

        }
        if (data != null) {
            h264CEncoder?.encodeFrame(data)
            mCamera?.addCallbackBuffer(data)
        }
    }
}