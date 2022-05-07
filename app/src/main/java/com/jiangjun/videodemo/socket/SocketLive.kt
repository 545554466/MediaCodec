package com.jiangjun.videodemo.socket

import android.media.MediaFormat
import android.media.projection.MediaProjection
import android.util.Log
import com.jiangjun.videodemo.recordscreen.H264RSEncoder
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress

class SocketLive(var mMediaProjection: MediaProjection) {
    private var webSocket: WebSocket? = null
    private val TAG: String = "Record"

    fun start() {
        val h264Encoder =
            H264RSEncoder(mMediaProjection, 1440, 2730, MediaFormat.MIMETYPE_VIDEO_AVC, this)
        h264Encoder.start()
        webSocketServer.start()
    }

    fun sendDara(b: ByteArray) {
        if (null != webSocket && webSocket!!.isOpen) {
            webSocket?.send(b)
        }
    }

    private val webSocketServer: WebSocketServer =
        object : WebSocketServer(InetSocketAddress(8099)) {
            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                Log.d(TAG, "onOpen =============")
                webSocket = conn
            }

            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose ============")
            }

            override fun onMessage(conn: WebSocket?, message: String?) {
                Log.d(TAG, "onMessage ===========")
            }

            override fun onError(conn: WebSocket?, ex: Exception?) {
                Log.d(TAG, "onError: ${ex.toString()} ================")
            }

            override fun onStart() {
                Log.d(TAG, "onStart ===============")
            }

        }


}