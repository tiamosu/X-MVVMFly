package com.tiamosu.fly.demo.data.repository

import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.request.base.ProgressRequestBody
import io.reactivex.rxjava3.disposables.Disposable
import java.io.File

/**
 * @author tiamosu
 * @date 2020/3/17.
 */
interface IRemoteRequest {

    fun <T> getFriend(callback: Callback<T>)

    fun <T> post(callback: Callback<T>)

    fun <T> put(callback: Callback<T>)

    fun <T> delete(callback: Callback<T>)

    fun <T> custom(callback: Callback<T>)

    fun <T> downloadFile(callback: Callback<T>): Disposable?

    fun <T> requestCache(callback: Callback<T>, cacheMode: CacheMode, cacheKey: String)

    fun <T> uploadFile(
        callback: Callback<T>,
        progressResponseCallBack: ProgressRequestBody.ProgressResponseCallBack,
        key: String,
        file: File
    )
}