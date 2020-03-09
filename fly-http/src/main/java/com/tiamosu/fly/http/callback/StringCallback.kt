package com.tiamosu.fly.http.callback

import com.tiamosu.fly.http.convert.StringConvert
import okhttp3.ResponseBody

/**
 * 描述：返回字符串类型的数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class StringCallback : AbsCallback<String>() {
    private val convert = StringConvert()

    @Throws(Throwable::class)
    override fun convertResponse(body: ResponseBody): String? {
        return convert.convertResponse(body)
    }
}