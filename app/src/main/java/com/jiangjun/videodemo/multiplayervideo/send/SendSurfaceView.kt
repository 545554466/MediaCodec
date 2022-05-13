package com.jiangjun.videodemo.multiplayervideo.send


import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity

class SendSurfaceView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs),
    SurfaceHolder.Callback, Camera.PreviewCallback {
    private lateinit var mCamera: Camera
    private lateinit var size: Camera.Size
    var mvEncodePushLive: SendEncodePushLive? = null

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
            setPreviewCallbackWithBuffer(this@SendSurfaceView)
            startPreview()
        }
    }

    fun startCaptures(videoType: String, callback: SendSocketClient.SocketCallback) {

        mvEncodePushLive = SendEncodePushLive(
            videoType, size.width, size.height,callback
        )
    }

    fun getSocket():SendSocketClient?{
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