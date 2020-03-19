package com.tiamosu.fly.module.main.data.repository

import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.module.main.data.api.APIs
import com.tiamosu.fly.module.main.data.api.CustomApiService

/**
 * @author tiamosu
 * @date 2020/3/17.
 */
object HttpRequestManager : IRemoteRequest {

    override fun <T> getFriend(callback: Callback<T>) {
        FlyHttp[APIs.FRIEND_JSON]
            .build()
            .execute(callback)
    }

    override fun <T> post(callback: Callback<T>) {
        FlyHttp.post(APIs.POST)
            .retryCount(0)
            .build()
            .execute(callback)
    }

    override fun <T> put(callback: Callback<T>) {
        FlyHttp.put(APIs.PUT)
            .retryCount(0)
            .build()
            .execute(callback)
    }

    override fun <T> delete(callback: Callback<T>) {
        FlyHttp.delete(APIs.DELETE)
            .retryCount(0)
            .build()
            .execute(callback)
    }

    override fun <T> custom(callback: Callback<T>) {
        FlyHttp.custom(APIs.FRIEND_JSON).also {
            val observable = it.create(CustomApiService::class.java)?.getFriend(it.url)
            it.apiCall(observable, callback)
        }
    }
}