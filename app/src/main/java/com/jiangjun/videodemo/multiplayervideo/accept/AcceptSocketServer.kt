package com.jiangjun.videodemo.multiplayervideo.accept

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class AcceptSocketServer(var callBack: scocketCallBack) {
    private val TAG: String = "Record"
    private var webSocket: WebSocket? = null

    fun start() {
        mvSocketServer.start()
    }

    fun sendData(b: ByteArray, type: Int) {
        val newBuf: ByteArray
        b.let {
            newBuf = ByteArray((b.size) + 1)
            when (type) {
                0 -> newBuf[0] = 0
                1 -> newBuf[0] = 1
                else -> {

                }
            }
            System.arraycopy(b, 0, newBuf, 1, b.size)

        }
        if (null != webSocket && webSocket!!.isOpen) {
            webSocket!!.send(newBuf)
        }
    }

    private val mvSocketServer: WebSocketServer =
        object : WebSocketServer(InetSocketAddress(7003)) {
            var i = 0
            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                Log.d(TAG, "onOpen Server=============")
                webSocket = conn
            }

            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose Server============")
            }

            override fun onMessage(conn: WebSocket?, message: String?) {
                Log.d(TAG, "onMessage Server===========")
            }

            override fun onMessage(conn: WebSocket?, bytes: ByteBuffer?) {
                super.onMessage(conn, bytes)
                if (i <= 1) {
                    Log.d(TAG, "onMessage Server=============")
                    i++
                }

                val buf = ByteArray(bytes!!.remaining())
                bytes.get(buf)
                callBack.callBack(buf)
            }

            override fun onError(conn: WebSocket?, ex: Exception?) {
                Log.d(TAG, "onError  Server: ${ex.toString()} ================")
            }

            override fun onStart() {
                Log.d(TAG, "onStart Server===============")
            }

        }

    interface scocketCallBack {
        fun callBack(data: ByteArray)
    }
}