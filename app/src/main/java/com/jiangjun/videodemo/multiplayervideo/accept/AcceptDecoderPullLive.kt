package com.jiangjun.videodemo.multiplayervideo.accept

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer

class AcceptDecoderPullLive(width: Int, height: Int, surface: Surface) {
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
        val index: Int = mMediaCodec.dequeueInputBuffer(100000)
        if (index >= 0) {
            val inputBuffer: ByteBuffer? = mMediaCodec.getInputBuffer(index)
            inputBuffer?.clear()
            inputBuffer?.put(data, 1, data.size-1)
            mMediaCodec.queueInputBuffer(index, 0, data.size, System.currentTimeMillis(), 0)
        }

        val bufferInfo = MediaCodec.BufferInfo()
        var outputBufferIndex: Int = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100000)
        while (outputBufferIndex >= 0) {
            mMediaCodec.releaseOutputBuffer(outputBufferIndex, true)
            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0)
        }
    }
}