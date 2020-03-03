package com.tiamosu.fly.http.model

/**
 * 描述：提供的默认的标注返回api
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
class ApiResult<T> {
    var code = 0
    var msg: String? = null
    var data: T? = null

    val isOk: Boolean
        get() = code == 0

    override fun toString(): String {
        return "ApiResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}'
    }
}