package com.jiangjun.videodemo.multiplayervideo.send

import android.media.MediaCodec
import android.media.MediaFormat
import android.view.Surface

class SendDecoderPullLive(width: Int, height: Int, surface: Surface) {
    private var mMediaCodec: MediaCodec =
        MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)

    init {
        val mediaFormat =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mediaFormat.apply {
            setInteger(MediaFormat.KEY_BIT_RATE, width * height)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 30)
            setInteger(MediaFormat.KEY_FRAME_RATE, 20)
            setInteger(MediaFormat.KEY_COLOR_FORMAT, 1)
        }

        mMediaCodec.configure(mediaFormat, surface, null, 0)
        mMediaCodec.start()
    }

    fun decoderFrame(data: ByteArray) {
        val inputIndex = mMediaCodec.dequeueInputBuffer(100000)
        while (inputIndex >= 0) {
            val inputBuffer = mMediaCodec.getInputBuffer(inputIndex)
            inputBuffer?.clear()
            inputBuffer?.put(data,1,data.size-1)
            mMediaCodec.queueInputBuffer(inputIndex,0,data.size,System.currentTimeMillis(),0)
        }

        val bufferInfo = MediaCodec.BufferInfo()
        var outputIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100000)
        while (outputIndex >= 0) {
            mMediaCodec.releaseOutputBuffer(outputIndex, false)
            outputIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100000)
        }
    }
}