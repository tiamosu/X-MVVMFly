package com.tiamosu.fly.http.model

import android.os.SystemClock
import java.io.Serializable
import java.util.*

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class Progress : Serializable {
    var filePath: String? = null                                //保存文件地址
    var fileName: String? = null                                //保存的文件名
    var fraction = 0F                                           //下载的进度，0-1 = 0f
    var totalSize = -1L                                         //总字节长度, byte
    var currentSize = 0L                                        //本次下载的大小, byte

    @Transient
    var speed = 0L                                              //网速，byte/s

    @Transient
    private var tempSize = 0L                                   //每一小段时间间隔的网络流量

    @Transient
    private var lastRefreshTime: Long =
        SystemClock.elapsedRealtime()                           //最后一次刷新的时间

    @Transient
    private val speedBuffer: MutableList<Long>                  //网速做平滑的缓存，避免抖动过快

    init {
        speedBuffer = ArrayList()
    }

    /**
     * 平滑网速，避免抖动过大
     */
    private fun bufferSpeed(speed: Long): Long {
        speedBuffer.add(speed)
        if (speedBuffer.size > 10) {
            speedBuffer.removeAt(0)
        }
        var sum: Long = 0
        for (speedTemp in speedBuffer) {
            sum += speedTemp
        }
        return sum / speedBuffer.size
    }

    fun interface Action {
        fun call(progress: Progress)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        return super.equals(other)
    }

    override fun toString(): String {
        return "Progress(filePath=$filePath, fileName=$fileName, fraction=$fraction, " +
                "totalSize=$totalSize, currentSize=$currentSize, speed=$speed)"
    }

    override fun hashCode(): Int {
        return 0
    }

    companion object {

        fun changeProgress(
            progress: Progress,
            writeSize: Long,
            action: Action?
        ): Progress {
            return changeProgress(
                progress,
                writeSize,
                progress.totalSize,
                action
            )
        }

        fun changeProgress(
            progress: Progress,
            writeSize: Long,
            totalSize: Long,
            action: Action?
        ): Progress {
            progress.totalSize = totalSize
            progress.currentSize += writeSize
            progress.tempSize += writeSize
            val currentTime = SystemClock.elapsedRealtime()
            val isNotify = currentTime - progress.lastRefreshTime >= 100
            if (isNotify || progress.currentSize == totalSize) {
                var diffTime = currentTime - progress.lastRefreshTime
                if (diffTime == 0L) diffTime = 1
                progress.fraction = progress.currentSize * 1.0f / totalSize
                progress.speed = progress.bufferSpeed(progress.tempSize * 1000 / diffTime)
                progress.lastRefreshTime = currentTime
                progress.tempSize = 0
                action?.call(progress)
            }
            return progress
        }
    }
}
