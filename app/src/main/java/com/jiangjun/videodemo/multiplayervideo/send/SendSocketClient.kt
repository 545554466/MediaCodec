package com.jiangjun.videodemo.multiplayervideo.send

import android.util.Log
import com.jiangjun.videodemo.utils.AudioRecordUtils
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

class SendSocketClient(var port: Int, var callback: SocketCallback) {
    private val TAG: String = "Record"
    private var mvSocketClient: MVWebSocketClient? = null


    fun start() {
        if (null == mvSocketClient) {
            val uri = URI("ws://10.10.64.156:$port")
            mvSocketClient = MVWebSocketClient(uri)
            mvSocketClient?.connect()
        }
    }

    fun sendData(b: ByteArray?, type: Int) {
        Log.d(TAG, "sendData type: ---------------     $type")
        b?.let {
            var newBuf = ByteArray((b.size) + 1)
            when (type) {
                0 -> newBuf[0] = 0
                1 -> newBuf[0] = 1
                else -> {

                }
            }
            System.arraycopy(b, 0, newBuf, 1, b.size)
            if (null != mvSocketClient && mvSocketClient!!.isOpen) {
                mvSocketClient!!.send(newBuf)
            }
        }
    }

    inner class MVWebSocketClient(serverUri: URI) : WebSocketClient(serverUri) {
        var i = 0
        override fun onOpen(handshakedata: ServerHandshake?) {
            Log.d(TAG, "Client   onOpen ==============")
        }

        override fun onMessage(message: String?) {
        }

        override fun onMessage(bytes: ByteBuffer?) {
            super.onMessage(bytes)
            if (i <= 1) {
                Log.d(TAG, "Client   onMessage =============")
                i++
            }
            val buf = ByteArray(bytes!!.remaining())
            bytes.get(buf)
            callback.callBack(buf)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            Log.d(TAG, "Client   onClose =============")
        }

        override fun onError(ex: Exception?) {
            Log.d(TAG, "Client   onError ========= ${ex.toString()}")
        }
    }

    interface SocketCallback {
        fun callBack(data: ByteArray?)
    }

}