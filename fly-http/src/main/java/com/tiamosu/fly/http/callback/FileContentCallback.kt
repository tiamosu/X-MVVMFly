package com.tiamosu.fly.http.callback

import com.tiamosu.fly.http.convert.FileContentConvert
import okhttp3.ResponseBody

/**
 * 描述：返回文本内容数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class FileContentCallback : AbsCallback<String>() {
    private val convert = FileContentConvert()

    @Throws(Throwable::class)
    override fun convertResponse(body: ResponseBody): String? {
        return convert.convertResponse(body)
    }
}