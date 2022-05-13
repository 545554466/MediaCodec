package com.jiangjun.videodemo.socket

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer

class SocketLiveClient(var socketCallback: SocketCallback, var port: Int) {
    private val TAG: String = "Record"

    interface SocketCallback {
        fun callBack(data: ByteArray?)
    }

    fun start() {
        Log.d(TAG, "start: url  =====   ws://10.10.64.156:$port")
        //设置需要链接的服务器ip地址
        val uri = URI("ws://10.10.64.162:$port")
        val socketClient = SocketClient(uri)
        socketClient.connect()
    }

    inner class SocketClient(serverUri: URI?) : WebSocketClient(serverUri) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            Log.d(TAG, "Client   onOpen ==============")
        }

        override fun onMessage(message: String?) {
        }

        override fun onMessage(bytes: ByteBuffer?) {
            super.onMessage(bytes)
            val byteArray = bytes?.remaining()?.let { ByteArray(it) }
            bytes?.get(byteArray)
            socketCallback.callBack(byteArray)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            Log.d(TAG, "Client   onClose =============")
        }

        override fun onError(ex: Exception?) {
            Log.d(TAG, "Client   onError ========= ${ex.toString()}")
        }

    }
}