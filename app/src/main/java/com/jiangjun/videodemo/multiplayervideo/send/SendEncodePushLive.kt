package com.jiangjun.videodemo.multiplayervideo.send

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import com.jiangjun.videodemo.utils.YUVUtils
import java.nio.ByteBuffer
import kotlin.experimental.and

// type 码流
class SendEncodePushLive(
    var videoType: String,
    var width: Int,
    var height: Int,
    var callback: SendSocketClient.SocketCallback
) {
    private val TAG: String = "Record"
    private var mMediaCodec: MediaCodec? = null
    private var yuvByte: ByteArray? = null
    private var mvSocketClient: SendSocketClient? = null
    private var frameIndex: Long = 0

    private val H264_I = 5
    private val H264_CONFIGURATION_PICTURE = 7

    private val H265_I = 19
    private val H265_CONFIGURATION_PICTURE = 32

    private lateinit var H264_SPS_BYTE: ByteArray
    private lateinit var H265_VPS_BYTE: ByteArray

    fun getSocket(): SendSocketClient? {
        return mvSocketClient
    }

    init {
        mMediaCodec = MediaCodec.createEncoderByType(videoType)
        var mediaFormat: MediaFormat =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, height, width)
        mediaFormat.apply {
            setInteger(MediaFormat.KEY_BIT_RATE, width * height)
            setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
            )
            setInteger(MediaFormat.KEY_FRAME_RATE, 15)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
        }
        mMediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mMediaCodec?.start()
        yuvByte = ByteArray(width * height * 3 / 2)
        mvSocketClient = SendSocketClient(7003, callback)
        mvSocketClient?.start()
    }

    fun encodeFrame(b: ByteArray) {
        val nv12 = YUVUtils.nv21toNV12(b)

        yuvByte?.let { yuv ->
            YUVUtils.portraitData2Raw(nv12, yuv, width, height)
            val inputBufferIndex: Int = mMediaCodec!!.dequeueInputBuffer(100000)
            if (inputBufferIndex >= 0) {
                val byteBuffers: Array<ByteBuffer> = mMediaCodec!!.inputBuffers
                val inputBuffer = byteBuffers[inputBufferIndex]
                inputBuffer.clear()
                //            原始数据1   压缩2
                inputBuffer.put(yuvByte)
                //dsp芯片解码
                val presentationTimeUs = computePresentationTime(frameIndex)
                mMediaCodec!!.queueInputBuffer(
                    inputBufferIndex,
                    0,
                    yuvByte!!.size,
                    presentationTimeUs,
                    0
                )
                frameIndex++
            }
        }


        val bufferInfo = MediaCodec.BufferInfo()
        val outputBufferIndex: Int = mMediaCodec!!.dequeueOutputBuffer(bufferInfo, 100000)
        if (outputBufferIndex >= 0) {
            val outputBuffer: ByteBuffer? = mMediaCodec!!.getOutputBuffer(outputBufferIndex)
            dealFrame(outputBuffer, bufferInfo)
            mMediaCodec!!.releaseOutputBuffer(outputBufferIndex, false)
        }
    }

    fun dealFrame(bb: ByteBuffer?, info: MediaCodec.BufferInfo) {
        // 分隔符有两种 00 00 00 01 和 00 00 01
        var offset = 4
        if (bb?.get(2)?.toInt() == 0x01) {
            offset = 3
        }
        // 当 值为 H264_CONFIGURATION_PICTURE 或者 H265_CONFIGURATION_PICTURE 就代表是配置帧，因为配置帧只会编码一次
        //所以需要保存好，当有I帧时，手动在I帧前塞入配置帧，以确保播放正常播放画面；
        when (getPictureType(bb, offset)) {
            H264_CONFIGURATION_PICTURE -> {
                H264_SPS_BYTE = ByteArray(info.size)
                bb?.get(H264_SPS_BYTE)
            }
            H264_I -> {
                val byte = ByteArray(info.size)
                bb?.get(byte)
                var newByte = ByteArray(byte.size + H264_SPS_BYTE.size)
                System.arraycopy(H264_SPS_BYTE, 0, newByte, 0, H264_SPS_BYTE.size)
                System.arraycopy(byte, 0, newByte, H264_SPS_BYTE.size, byte.size)
                mvSocketClient?.sendData(newByte,1)
            }
            H265_CONFIGURATION_PICTURE -> {
                H265_VPS_BYTE = ByteArray(info.size)
                bb?.get(H265_VPS_BYTE)
            }
            H265_I -> {
                val byte = ByteArray(info.size)
                bb?.get(byte)
                var newByte = ByteArray(byte.size + H265_VPS_BYTE.size)
                System.arraycopy(H265_VPS_BYTE, 0, newByte, 0, H265_VPS_BYTE.size)
                System.arraycopy(byte, 0, newByte, H265_VPS_BYTE.size, byte.size)
                mvSocketClient?.sendData(newByte,1)
            }
            else -> {
                // 非I帧与非配置帧，走这里
                val byte = ByteArray(info.size)
                bb?.get(byte)
                mvSocketClient?.sendData(byte,1)
            }
        }
    }

    fun getPictureType(bb: ByteBuffer?, offset: Int): Int {
        //获取帧类型，h264 h265 帧类型不同所以需要区分
        if (null != bb) {
            if (videoType == MediaFormat.MIMETYPE_VIDEO_AVC) {
                //h264 获取帧类型
                return ((bb.get(offset)) and 0x1f).toInt()
            } else if (videoType == MediaFormat.MIMETYPE_VIDEO_HEVC) {
                //h265 获取帧类型
                return ((bb.get(offset)) and 0x7e.shr(1)).toInt()
            }
        }
        return 0
    }

    private fun computePresentationTime(frameIndex: Long): Long {
        return frameIndex * 1000000 / 15
    }
}