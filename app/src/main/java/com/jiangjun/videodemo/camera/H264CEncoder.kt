package com.jiangjun.videodemo.camera

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import com.jiangjun.videodemo.utils.FileUtils
import java.nio.Buffer
import java.nio.ByteBuffer

class H264CEncoder(val width: Int, val height: Int) {
    private lateinit var mMediaCodec: MediaCodec

    //计算时间的下标
    private var index: Int = 0
    private val psTime: Int = 1000000


   public fun initMediaCodec() {
        mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        var mMediaFormat: MediaFormat =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mMediaFormat.apply {
            setInteger(MediaFormat.KEY_FRAME_RATE, 20)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 30)
            setInteger(MediaFormat.KEY_BIT_RATE, width * height)
            setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
            )
        }
        mMediaCodec.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mMediaCodec.start()

    }

    fun encodeFrame(input: ByteArray) {
        //获取到摄像头数据往GPU中输入
        val inputBufferIndex: Int = mMediaCodec.dequeueInputBuffer(10000)
        val buffInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
        if (inputBufferIndex >= 0) {
            mMediaCodec.getInputBuffer(inputBufferIndex).apply {
                this?.clear()
                this?.put(input)
            }
            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, input.size, computPts(), 0)
            index++
        }

        //把编码好的数据输出
        val ouPutBufferIndex: Int = mMediaCodec.dequeueOutputBuffer(buffInfo, 10000)
        if (ouPutBufferIndex >= 0) {
            val buffer: ByteBuffer? = mMediaCodec.getOutputBuffer(ouPutBufferIndex)
            val ba = ByteArray(buffInfo.size)
            buffer?.get(ba)
            FileUtils.writeBytes(ba)
            FileUtils.writeContent(ba)
            mMediaCodec.releaseOutputBuffer(ouPutBufferIndex, false)
        }
    }

    // 20 是设置的 1秒钟输出20帧
    // 1秒钟=1000 000微秒
    // 第一帧的时间就是 1000000 / 20  第二帧的时间就是 第一帧的时间就是*2  第三帧的时间就是 第一帧的时间就是*3
    private fun computPts(): Long = (1000000 / 20 * index).toLong()
}