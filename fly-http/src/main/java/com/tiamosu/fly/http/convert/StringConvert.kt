package com.tiamosu.fly.http.convert

import com.blankj.utilcode.util.CloseUtils
import okhttp3.ResponseBody

/**
 * 描述：字符串转换器
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
class StringConvert : Converter<String> {

    override fun convertResponse(body: ResponseBody): String? {
        val string = body.string()
        CloseUtils.closeIO(body)
        return string
    }
}