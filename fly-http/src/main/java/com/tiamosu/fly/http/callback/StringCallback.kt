package com.tiamosu.fly.http.callback

/**
 * 描述：返回字符串类型的数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class StringCallback : CacheResultCallback<String>() {

    override fun convertResponse(string: String?) = string
}