package com.tiamosu.fly.http.callback

import com.blankj.utilcode.util.CloseUtils
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType

/**
 * @author tiamosu
 * @date 2020/3/8.
 */
abstract class GenericsCallback<T>(private val genericsSerializator: IGenericsSerializator) :
    Callback<T> {

    @Suppress("UNCHECKED_CAST")
    override fun convertResponse(body: ResponseBody): T? {
        var type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            type = type.actualTypeArguments[0]
        }
        var result: T? = null
        if (type is Class<*>) {
            val string = body.string()
            result = when (type) {
                String::class.java -> string as? T
                else -> genericsSerializator.transform(string, type) as? T
            }
        }
        CloseUtils.closeIO(body)
        return result
    }
}
