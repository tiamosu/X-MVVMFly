package com.tiamosu.fly.demo.data.repository

import com.tiamosu.fly.demo.data.api.APIs
import com.tiamosu.fly.demo.data.api.CustomApiService
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.cache.converter.SerializableDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.request.base.ProgressRequestBody
import java.io.File

/**
 * @author tiamosu
 * @date 2020/3/17.
 */
class DataRepository : IRemoteRequest {

    companion object {
        val instance: DataRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DataRepository()
        }
    }

    override fun <T> getFriend(callback: Callback<T>) {
        FlyHttp[APIs.FRIEND_JSON]
            .build()
            .execute(callback)
    }

    override fun <T> post(callback: Callback<T>) {
        FlyHttp.post(APIs.FRIEND_JSON)
            .params("1", "z============")
            .addParamsToUrl(false)
            .retryCount(0)
            .upObject("1233333333")
            .build()
            .execute(callback)
    }

    override fun <T> put(callback: Callback<T>) {
        FlyHttp.put(APIs.FRIEND_JSON)
            .retryCount(0)
            .build()
            .execute(callback)
    }

    override fun <T> delete(callback: Callback<T>) {
        FlyHttp.delete(APIs.FRIEND_JSON)
            .retryCount(0)
            .build()
            .execute(callback)
    }

    override fun <T> custom(callback: Callback<T>) {
        FlyHttp.custom(APIs.FRIEND_JSON).also {
            val observable = it.create<CustomApiService>()?.getFriend(it.url)
            it.apiCall(observable, callback)
        }
    }

    override fun <T> downloadFile(callback: Callback<T>) {
        FlyHttp.download(APIs.DOWNLOAD_FILE)
            .build()
            .execute(callback)
    }

    override fun <T> requestCache(callback: Callback<T>, cacheMode: CacheMode, cacheKey: String) {
        FlyHttp[APIs.FRIEND_JSON]
            .readTimeOut(30 * 1000) //测试局部读超时30s
            .cacheMode(cacheMode)
            .cacheKey(cacheKey) //缓存key
            .retryCount(3) //重试次数
            .cacheTime(5 * 60) //缓存时间300s，默认-1永久缓存  okHttp和自定义缓存都起作用
//            .okCache(Cache()) //okHttp缓存，模式为默认模式（CacheMode.DEFAULT）才生效
            .cacheDiskConverter(SerializableDiskConverter()) //默认使用的是 SerializableDiskConverter()
            .timeStamp(true)
            .build()
            .execute(callback)
    }

    override fun <T> uploadFile(
        callback: Callback<T>,
        progressResponseCallBack: ProgressRequestBody.ProgressResponseCallBack,
        key: String,
        file: File
    ) {
        FlyHttp.upload(APIs.UPLOAD_FILE)
            .params(key, file)
            .build()
            .execute(callback)
    }
}