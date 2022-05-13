@file:Suppress("NAME_SHADOWING")

package com.jiangjun.videodemo.utils

import android.util.Log


object YUVUtils {
    private val TAG: String = "Record"
    private var nv12: ByteArray? = null

    fun nv21toNV12(nv21: ByteArray): ByteArray {
        val nv21Size = nv21.size
        nv12 = ByteArray(nv21Size)
        val len = nv21Size * 2 / 3
        System.arraycopy(nv21, 0, nv12, 0, len)
        var i: Int = len
        while (i < nv21Size - 1) {
            nv12!![i] = nv21[i + 1]
            nv12!![i + 1] = nv21[i]
            i += 2
        }
        return nv12 as ByteArray
    }

    // data   nv21数据
    // output 重组后的数据放进这里
    //此方法是 把从摄像头的数据 90度旋转 转正
    fun portraitData2Raw(data: ByteArray, output: ByteArray, width: Int, height: Int) {
        val y_len = width * height
        val uvHeight = height shr 1

        var k = 0
        //j == 列数    i == 行数
        // 转换 Y 数据
        for (j in 0 until width) {
            for (i in height - 1 downTo 0) {
                output[k++] = data[width * i + j]
            }
        }


        var j = 0
        while (j < width) {
            for (i in uvHeight - 1 downTo 0) {
                output[k++] = data[y_len + width * i + j]
                output[k++] = data[y_len + width * i + j + 1]
            }
            j += 2
        }

    }
}