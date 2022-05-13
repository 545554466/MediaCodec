package com.jiangjun.videodemo.multiplayervideo.accept


import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.jiangjun.videodemo.multiplayervideo.send.SendSocketClient

class AcceptSurfaceView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs),
    SurfaceHolder.Callback, Camera.PreviewCallback {
    private lateinit var mCamera: Camera
    private lateinit var size: Camera.Size
    var mvEncodePushLive: AcceptEncodePushLive? = null

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
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        val parameters = mCamera.parameters
        size = parameters.previewSize

        mCamera.apply {
            setPreviewDisplay(holder)
            setDisplayOrientation(90)
            val byte = ByteArray(size.width * size.height * 3 / 2)
            addCallbackBuffer(byte)
            setPreviewCallbackWithBuffer(this@AcceptSurfaceView)
            startPreview()
        }
    }

    fun startCaptures(videoType: String, callBack: AcceptSocketServer.scocketCallBack) {
        mvEncodePushLive = AcceptEncodePushLive(
            videoType, size.width, size.height, callBack
        )
    }

    fun getSocket(): AcceptSocketServer?{
        return mvEncodePushLive?.getSocket()
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        if (null != mvEncodePushLive) {
            data?.let {
                mvEncodePushLive!!.encodeFrame(it)
            }
        }
        mCamera.addCallbackBuffer(data)
    }

}