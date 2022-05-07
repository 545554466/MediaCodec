package com.jiangjun.videodemo.recordscreentoupin

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import com.jiangjun.videodemo.socket.SocketLiveClient

class VideoPlay( surface: Surface) : SocketLiveClient.SocketCallback {
    private var decoder: MediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
    private var width: Int = 1440
    private var height: Int = 2730
    private val TAG: String = "Record"
    init {
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mediaFormat.apply {
            setInteger(MediaFormat.KEY_BIT_RATE, width * height)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 30)
            setInteger(MediaFormat.KEY_FRAME_RATE, 20)
            setInteger(MediaFormat.KEY_COLOR_FORMAT, 1)
        }
        decoder.configure(mediaFormat, surface, null, 0)
        decoder.start()
    }

    override fun callBack(data: ByteArray?) {
        val index = decoder.dequeueInputBuffer(100000)
        if (index >= 0) {
            val inputBuffer = decoder.getInputBuffer(index)
            inputBuffer?.apply {
                clear()
                data?.size?.let { put(data, 0, it) }
            }
            data?.size?.let {
                decoder.queueInputBuffer(
                    index,
                    0,
                    it,
                    System.currentTimeMillis(),
                    0
                )
            }
        }

        val bufferInfo = MediaCodec.BufferInfo()
        var outPutIndex = decoder.dequeueOutputBuffer(bufferInfo, 100000)

        Log.i(TAG, "解码器后长度  : " + bufferInfo.size)
        while (outPutIndex >= 0) {
            decoder.releaseOutputBuffer(outPutIndex, true)
            outPutIndex = decoder.dequeueOutputBuffer(bufferInfo, 100000)
        }
    }
}