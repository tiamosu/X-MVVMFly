package com.tiamosu.fly.http.callback

import com.google.gson.internal.`$Gson$Types`
import com.tiamosu.fly.http.model.ApiResult
import com.tiamosu.fly.http.utils.FlyHttpUtils.findNeedType
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 描述：提供Clazz回调代理
 * 主要用于可以自定义ApiResult
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
abstract class CallClazzProxy<T : ApiResult<R>, R>(private val callType: Type?) : IType<T> {

    override fun getType(): Type? { //CallClazz代理方式，获取需要解析的Type
        var typeArguments: Type? = callType
        if (typeArguments == null) {
            typeArguments = ResponseBody::class.java
        }
        var rawType = findNeedType(javaClass)
        if (rawType is ParameterizedType) {
            rawType = rawType.rawType
        }
        return `$Gson$Types`.newParameterizedTypeWithOwner(
            null,
            rawType,
            typeArguments
        )
    }
}