package com.tiamosu.fly.http.model

import android.content.ContentValues
import android.database.Cursor
import android.os.SystemClock
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.http.utils.FlyIOUtils.toByteArray
import com.tiamosu.fly.http.utils.FlyIOUtils.toObject
import java.io.Serializable
import java.util.*

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class Progress : Serializable {
    var tag: String? = null                                     //下载的标识键
    var url: String? = null                                     //网址
    var folder: String? = null                                  //保存文件夹
    var filePath: String? = null                                //保存文件地址
    var fileName: String? = null                                //保存的文件名
    var fraction = 0F                                           //下载的进度，0-1 = 0f
    var totalSize: Long                                         //总字节长度, byte
    var currentSize = 0L                                        //本次下载的大小, byte

    @Transient
    var speed = 0L                                              //网速，byte/s
    var status = 0                                              //当前状态 = 0
    var priority: Int                                           //任务优先级
    var date: Long                                              //创建时间
    var request: BaseRequest<*, out BaseRequest<*, *>>? = null  //网络请求
    var extra1: Serializable? = null                            //额外的数据
    var extra2: Serializable? = null                            //额外的数据
    var extra3: Serializable? = null                            //额外的数据
    var exception: Throwable? = null                            //当前进度出现的异常

    @Transient
    private var tempSize = 0L                                   //每一小段时间间隔的网络流量

    @Transient
    private var lastRefreshTime: Long                           //最后一次刷新的时间

    @Transient
    private val speedBuffer: MutableList<Long>                  //网速做平滑的缓存，避免抖动过快

    init {
        lastRefreshTime = SystemClock.elapsedRealtime()
        totalSize = -1
        priority = Priority.DEFAULT
        date = System.currentTimeMillis()
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

    /**
     * 转换进度信息
     */
    fun from(progress: Progress) {
        totalSize = progress.totalSize
        currentSize = progress.currentSize
        fraction = progress.fraction
        speed = progress.speed
        lastRefreshTime = progress.lastRefreshTime
        tempSize = progress.tempSize
    }

    interface Action {
        fun call(progress: Progress)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val progress = other as Progress
        return if (tag != null) tag == progress.tag else progress.tag == null
    }

    override fun hashCode(): Int {
        return tag?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Progress(tag=$tag, url=$url, folder=$folder, filePath=$filePath, " +
                "fileName=$fileName, fraction=$fraction, totalSize=$totalSize, " +
                "currentSize=$currentSize, speed=$speed, status=$status, priority=$priority)"
    }

    companion object {
        const val NONE = 0 //无状态
        const val WAITING = 1 //等待
        const val LOADING = 2 //下载中
        const val PAUSE = 3 //暂停
        const val ERROR = 4 //错误
        const val FINISH = 5 //完成
        const val TAG = "tag"
        const val URL = "url"
        const val FOLDER = "folder"
        const val FILE_PATH = "filePath"
        const val FILE_NAME = "fileName"
        const val FRACTION = "fraction"
        const val TOTAL_SIZE = "totalSize"
        const val CURRENT_SIZE = "currentSize"
        const val STATUS = "status"
        const val PRIORITY = "priority"
        const val DATE = "date"
        const val REQUEST = "request"
        const val EXTRA1 = "extra1"
        const val EXTRA2 = "extra2"
        const val EXTRA3 = "extra3"

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
            val isNotify = currentTime - progress.lastRefreshTime >= 300
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

        fun buildContentValues(progress: Progress): ContentValues {
            val values = ContentValues()
            values.put(TAG, progress.tag)
            values.put(URL, progress.url)
            values.put(FOLDER, progress.folder)
            values.put(FILE_PATH, progress.filePath)
            values.put(FILE_NAME, progress.fileName)
            values.put(FRACTION, progress.fraction)
            values.put(TOTAL_SIZE, progress.totalSize)
            values.put(
                CURRENT_SIZE,
                progress.currentSize
            )
            values.put(STATUS, progress.status)
            values.put(PRIORITY, progress.priority)
            values.put(DATE, progress.date)
            values.put(
                REQUEST,
                toByteArray(progress.request)
            )
            values.put(
                EXTRA1,
                toByteArray(progress.extra1)
            )
            values.put(
                EXTRA2,
                toByteArray(progress.extra2)
            )
            values.put(
                EXTRA3,
                toByteArray(progress.extra3)
            )
            return values
        }

        fun buildUpdateContentValues(progress: Progress): ContentValues {
            val values = ContentValues()
            values.put(FRACTION, progress.fraction)
            values.put(TOTAL_SIZE, progress.totalSize)
            values.put(
                CURRENT_SIZE,
                progress.currentSize
            )
            values.put(STATUS, progress.status)
            values.put(PRIORITY, progress.priority)
            values.put(DATE, progress.date)
            return values
        }

        fun parseCursorToBean(cursor: Cursor): Progress {
            val progress = Progress()
            progress.tag = cursor.getString(cursor.getColumnIndex(TAG))
            progress.url = cursor.getString(cursor.getColumnIndex(URL))
            progress.folder = cursor.getString(cursor.getColumnIndex(FOLDER))
            progress.filePath = cursor.getString(cursor.getColumnIndex(FILE_PATH))
            progress.fileName = cursor.getString(cursor.getColumnIndex(FILE_NAME))
            progress.fraction = cursor.getFloat(cursor.getColumnIndex(FRACTION))
            progress.totalSize = cursor.getLong(cursor.getColumnIndex(TOTAL_SIZE))
            progress.currentSize = cursor.getLong(cursor.getColumnIndex(CURRENT_SIZE))
            progress.status = cursor.getInt(cursor.getColumnIndex(STATUS))
            progress.priority = cursor.getInt(cursor.getColumnIndex(PRIORITY))
            progress.date = cursor.getLong(cursor.getColumnIndex(DATE))
            progress.request =
                toObject(cursor.getBlob(cursor.getColumnIndex(REQUEST))) as BaseRequest<*, out BaseRequest<*, *>>?
            progress.extra1 =
                toObject(cursor.getBlob(cursor.getColumnIndex(EXTRA1))) as Serializable?
            progress.extra2 =
                toObject(cursor.getBlob(cursor.getColumnIndex(EXTRA2))) as Serializable?
            progress.extra3 =
                toObject(cursor.getBlob(cursor.getColumnIndex(EXTRA3))) as Serializable?
            return progress
        }
    }
}