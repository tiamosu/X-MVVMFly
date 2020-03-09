package com.tiamosu.fly.http.callback

/**
 * @author tiamosu
 * @date 2020/3/8.
 */
interface IGenericsSerializator {

    fun <T> transform(response: String, classOfT: Class<T>): T?
}