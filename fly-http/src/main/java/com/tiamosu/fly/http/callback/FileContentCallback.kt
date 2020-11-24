package com.tiamosu.fly.http.callback

import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 描述：返回文本内容数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class FileContentCallback : NoCacheResultCallback<String>() {

    @Throws(Throwable::class)
    final override fun convertResponse(body: ResponseBody): String? {
        val builder = StringBuilder()
        val inputStream = body.byteStream()
        val `in` = InputStreamReader(inputStream, "UTF-8")
        val reader = BufferedReader(`in`)
        reader.use {
            reader.forEachLine {
                builder.append(it)
            }
        }
        return builder.toString()
    }
}