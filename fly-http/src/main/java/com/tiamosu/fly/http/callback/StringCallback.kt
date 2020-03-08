package com.tiamosu.fly.http.callback

import com.tiamosu.fly.http.convert.StringConvert
import okhttp3.Response

/**
 * 描述：返回字符串类型的数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class StringCallback : AbsCallback<String>() {
    private val convert: StringConvert = StringConvert()

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): String? {
        return convert.convertResponse(response)
    }
}