package com.tiamosu.fly.integration.gson

import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken

/**
 * @author tiamosu
 * @date 2021/3/9.
 */
interface JsonCallback {
    /**
     * 类型解析异常
     *
     * @param typeToken             类型 Token
     * @param fieldName             字段名称（可能为空）
     * @param jsonToken             后台给定的类型
     */
    fun onTypeException(typeToken: TypeToken<*>?, fieldName: String?, jsonToken: JsonToken?)
}