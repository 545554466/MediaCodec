package com.jiangjun.videodemo.utils

import android.hardware.display.DisplayManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.projection.MediaProjection
import android.view.Surface
import java.nio.ByteBuffer

/**
 *编码 工具类
 **/
class H264Encoder(mMediaProjection: MediaProjection, width: Int, height: Int) : Thread() {
    // mMediaProjection 数据源在mMediaProjection拿
    // 创建编码器
    private var mediaCodec: MediaCodec =
        MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)

    init {
        val mediaFormat: MediaFormat =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)

        mediaFormat.apply {
            //帧率 1秒多少帧
            setInteger(MediaFormat.KEY_FRAME_RATE, 20)
            //I帧的间隔  30帧一个I帧
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 30)
            //码率 码率越高画面越清晰
            setInteger(MediaFormat.KEY_BIT_RATE, width * height)
            //设置数据的来源，我们的数据是在SurFace中来的 所以设置COLOR_FormatSurface
            setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )
        }

        // 配置  编码时不需要传参数二，参数三是加密，参数四一定要 解码传0， 编码传1
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val surface: Surface = mediaCodec.createInputSurface()

        //绑定 创建虚拟的显示器
        // 参数一  名称  随便起但是要唯一
        // 参数二三  输出的宽高
        // 参数四  一个dpi显示多少像素
        // 参数五 ？
        // 参数六  surface
        mMediaProjection.createVirtualDisplay(
            "糖糖糖", width, height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null
        )
    }

    override fun run() {
        //开启编码器
        mediaCodec.start()
        // 获取编码好的数据
        val info: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
        while (true) {
            // 获取输出缓存 要传入个buffer 保存数据  10000等待的时长
            val outIndex: Int = mediaCodec.dequeueOutputBuffer(info, 10000)
            if (outIndex >= 0) {
                //从输出队列中拿到编码好的数据
                val byteBuffer: ByteBuffer? = mediaCodec.getOutputBuffer(outIndex)
                //把byteBuffer 放到数组中
                val ba = ByteArray(info.size)
                //将容器的byteBuffer  内部的数据 转移到 byte[]中
                byteBuffer?.get(ba)
                FileUtils.writeBytes(ba)
                FileUtils.writeContent(ba)
                // 如果之前配置了surface 则传true， 我们解码没有配置surface 上面的只是虚拟的并不是我们自己创建的所以传false
                mediaCodec.releaseOutputBuffer(outIndex,false)
            }
        }
    }
}