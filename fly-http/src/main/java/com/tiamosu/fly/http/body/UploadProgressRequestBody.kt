package com.tiamosu.fly.http.body

import com.tiamosu.fly.http.utils.FlyHttpLog
import com.tiamosu.fly.http.utils.FlyHttpLog.e
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * 描述：上传请求体
 * 1.具有上传进度回调通知功能
 * 2.防止频繁回调，上层无用的刷新
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
class UploadProgressRequestBody(
    private val delegate: RequestBody,
    private val progressCallBack: ProgressResponseCallBack?
) : RequestBody() {

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     */
    override fun contentLength(): Long {
        return try {
            delegate.contentLength()
        } catch (e: IOException) {
            e(e.message)
            -1L
        }
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink)
        val bufferedSink: BufferedSink = Okio.buffer(countingSink)
        delegate.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    private inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
        private var bytesWritten = 0L
        private var contentLength = 0L      //总字节长度，避免多次调用contentLength()方法
        private var lastRefreshUiTime = 0L  //最后一次刷新的时间

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            if (contentLength <= 0) contentLength = contentLength() //获得contentLength的值，后续不再调用
            //增加当前写入的字节数
            bytesWritten += byteCount

            val curTime = System.currentTimeMillis()
            //每100毫秒刷新一次数据,防止频繁无用的刷新
            val done = bytesWritten == contentLength
            if (curTime - lastRefreshUiTime >= 100 || done) {
                progressCallBack?.onResponseProgress(bytesWritten, contentLength, done)
                lastRefreshUiTime = System.currentTimeMillis()
            }
            FlyHttpLog.i("bytesWritten=$bytesWritten ,totalBytesCount=$contentLength")
        }
    }
}