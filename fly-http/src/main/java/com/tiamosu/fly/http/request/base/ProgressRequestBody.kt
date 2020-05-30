package com.tiamosu.fly.http.request.base

import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.http.utils.eLog
import com.tiamosu.fly.utils.postOnMain
import io.reactivex.rxjava3.functions.Action
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
class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val callback: Callback<*>?
) : RequestBody() {
    private var progressCallBack: ProgressResponseCallBack? = null

    fun setProgressCallback(progressCallBack: ProgressResponseCallBack?) {
        this.progressCallBack = progressCallBack
    }

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     */
    override fun contentLength(): Long {
        return try {
            requestBody.contentLength()
        } catch (e: IOException) {
            eLog(e.message)
            -1L
        }
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink)
        val bufferedSink: BufferedSink = countingSink.buffer()
        requestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    private inner class CountingSink(sink: Sink) : ForwardingSink(sink) {
        private var progress: Progress = Progress()

        init {
            progress.totalSize = contentLength()
        }

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            Progress.changeProgress(progress, byteCount, Progress.Action { progress ->
                postOnMain(Action {
                    progressCallBack?.onResponseProgress(progress)
                    callback?.uploadProgress(progress)
                })
            })
        }
    }

    fun interface ProgressResponseCallBack {

        /**
         * 回调进度
         */
        fun onResponseProgress(progress: Progress)
    }
}