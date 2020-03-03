package com.tiamosu.fly.http.callback

import com.tiamosu.fly.http.exception.ApiException
import com.tiamosu.fly.http.utils.FlyHttpUtils.findNeedClass
import com.tiamosu.fly.http.utils.FlyHttpUtils.findRawType
import java.lang.reflect.Type

/**
 * 描述：网络请求回调
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
abstract class CallBack<T> : IType<T> {

    abstract fun onStart()

    abstract fun onCompleted()

    abstract fun onError(e: ApiException?)

    abstract fun onSuccess(t: T)

    //获取需要解析的泛型T类型
    override fun getType(): Type? {
        return findNeedClass(javaClass) //获取需要解析的泛型T类型
    }

    //获取需要解析的泛型T raw类型
    val rawType: Type?
        get() = findRawType(javaClass) //获取需要解析的泛型T raw类型
}