package com.tiamosu.fly.http.callback

/**
 * 描述：下载进度回调（主线程，可以直接操作UI）
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
abstract class DownloadProgressCallBack<T> : CallBack<T>() {

    override fun onSuccess(response: T) {}

    abstract fun update(bytesRead: Long, contentLength: Long, done: Boolean)

    abstract fun onComplete(path: String?)

    override fun onCompleted() {}
}