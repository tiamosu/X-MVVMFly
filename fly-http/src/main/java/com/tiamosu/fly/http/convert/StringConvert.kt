package com.tiamosu.fly.http.convert

import okhttp3.Response

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class StringConvert : Converter<String> {

    override fun convertResponse(response: Response): String? {
        val body = response.body() ?: return null
        return body.string()
    }
}