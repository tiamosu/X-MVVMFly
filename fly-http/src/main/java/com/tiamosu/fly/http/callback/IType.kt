package com.tiamosu.fly.http.callback

import java.lang.reflect.Type

/**
 * @author tiamosu
 * @date 2020/3/2.
 */
interface IType<T> {

    fun getType(): Type?
}