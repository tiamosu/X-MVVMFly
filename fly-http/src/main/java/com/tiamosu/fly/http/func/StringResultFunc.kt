package com.tiamosu.fly.http.func

import io.reactivex.rxjava3.functions.Function
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/12.
 */
class StringResultFunc : Function<ResponseBody, String> {

    @Throws(Exception::class)
    override fun apply(responseBody: ResponseBody) = responseBody.string()
}