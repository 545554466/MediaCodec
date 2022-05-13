package com.jiangjun.videodemo.utils

import android.media.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.jiangjun.videodemo.multiplayervideo.accept.AcceptSocketServer
import com.jiangjun.videodemo.multiplayervideo.send.SendSocketClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object AudioRecordUtils {
    private const val TAG: String = "Record"

    //配置播放器
    // 音乐类型,扬声器播放
    private const val streamType: Int = AudioManager.STREAM_MUSIC

    //录音时采用的采样频率,所有播放时同样的采样频率
    private const val sampleRateInHz: Int = 44100

    //单声道,和录音时设置的一样
    private const val channelConfig: Int = AudioFormat.CHANNEL_OUT_MONO

    // 录音使用16bit,所有播放时同样采用该方式
    private const val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT

    //流模式
    private const val mode: Int = AudioTrack.MODE_STREAM

    //计算最小buffer大小
    private var minBufferSize: Int =
        AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
    private lateinit var mAudioTrack: AudioTrack

    private var isRecording = false

    fun initAudioTrack() {
        mAudioTrack =
            AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, minBufferSize, mode)
        mAudioTrack.setVolume(16f)
        mAudioTrack.play()
    }

    fun doPlay(b: ByteArray) {
        val ret = mAudioTrack.write(b, 1, b.size-1)
        Log.d(TAG, "doPlay: ret  ==============   $ret")
        when (ret) {
            AudioTrack.ERROR_INVALID_OPERATION, AudioTrack.ERROR_BAD_VALUE, AudioManager.ERROR_DEAD_OBJECT -> return
            else -> {
                Log.d(TAG, "doPlay: 成功")
            }
        }
    }

    fun startRecord(socket: SendSocketClient?) {
        val minBufferSize: Int =
            AudioTrack.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_IN_STEREO, audioFormat)

        val mAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRateInHz,
            AudioFormat.CHANNEL_IN_STEREO,
            audioFormat,
            minBufferSize
        )

        mAudioRecord.startRecording()
        val data = ByteArray(minBufferSize)
        isRecording = true
        GlobalScope.launch {
            while (isRecording) {
                Log.d(TAG, " handler.post: ---------------")
                mAudioRecord.read(data, 0, minBufferSize)
                socket?.sendData(data, 0)
            }
        }
    }

    fun startRecord(socket: AcceptSocketServer?) {
        val minBufferSize: Int =
            AudioTrack.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_IN_STEREO, audioFormat)
        val mAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRateInHz,
            AudioFormat.CHANNEL_IN_STEREO,
            audioFormat,
            minBufferSize
        )


        mAudioRecord.startRecording()
        val data = ByteArray(minBufferSize)
        isRecording = true

        GlobalScope.launch {
            while (isRecording) {
                mAudioRecord.read(data, 0, minBufferSize)
                socket?.sendData(data, 0)
            }
        }
    }
}