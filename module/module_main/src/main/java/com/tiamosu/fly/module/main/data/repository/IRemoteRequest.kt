package com.tiamosu.fly.module.main.data.repository

import com.tiamosu.fly.http.callback.Callback

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

    fun <T> downloadFile(callback: Callback<T>)
}