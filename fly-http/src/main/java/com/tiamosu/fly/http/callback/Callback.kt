package com.tiamosu.fly.http.callback

import com.tiamosu.fly.http.model.Progress

/**
 * 描述：回调的基类
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
interface Callback<T> {

    /**
     * 请求网络开始前，UI线程
     */
    fun onStart()

    /**
     * 对返回数据进行操作的回调，UI线程
     */
    fun onSuccess(t: T?)

    /**
     * 请求失败，响应错误，数据解析错误等，都会回调该方法，UI线程
     */
    fun onError(e: Throwable)

    /**
     * 请求网络结束后，UI线程
     */
    fun onFinish()

    /**
     * 上传过程中的进度回调，UI线程
     */
    fun uploadProgress(progress: Progress)

    /**
     * 下载过程中的进度回调，UI线程
     */
    fun downloadProgress(progress: Progress)
}