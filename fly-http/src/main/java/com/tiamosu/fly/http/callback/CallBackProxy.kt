package com.tiamosu.fly.http.callback

import com.google.gson.internal.`$Gson$Types`
import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.model.ApiResult
import com.tiamosu.fly.http.utils.FlyHttpUtils.findNeedType
import com.tiamosu.fly.http.utils.FlyHttpUtils.getClass
import com.tiamosu.fly.http.utils.FlyHttpUtils.getParameterizedType
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 描述：提供回调代理
 * 主要用于可以自定义ApiResult
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
abstract class CallBackProxy<T : ApiResult<R>?, R>(private var callBack: CallBack<R>?) : IType<T> {

    //如果用户的信息是返回List需单独处理
    fun getCallBack(): CallBack<*>? {
        return callBack
    }

    //CallBack代理方式，获取需要解析的Type
    override fun getType(): Type? {
        var typeArguments: Type? = null
        if (callBack != null) {
            val rawType = callBack?.rawType //如果用户的信息是返回List需单独处理
            typeArguments = if (MutableList::class.java.isAssignableFrom(getClass(rawType, 0))
                || MutableMap::class.java.isAssignableFrom(getClass(rawType, 0))
            ) {
                callBack?.getType()
            } else if (CacheResult::class.java.isAssignableFrom(getClass(rawType, 0))
            ) {
                val type = callBack?.getType()
                getParameterizedType(type, 0)
            } else {
                val type = callBack?.getType()
                getClass(type, 0)
            }
        }
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