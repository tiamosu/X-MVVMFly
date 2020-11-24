package com.tiamosu.fly.http.callback

/**
 * 描述：返回字符串类型的数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class StringCallback : CacheResultCallback<String>() {

    final override fun convertResponse(string: String?): String? {
        return string
    }
}