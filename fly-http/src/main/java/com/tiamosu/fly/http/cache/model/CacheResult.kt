package com.tiamosu.fly.http.cache.model

import java.io.Serializable

/**
 * 描述：缓存对象
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
class CacheResult<T> : Serializable {
    var isFromCache = false
    var data: T? = null

    constructor()

    constructor(isFromCache: Boolean) {
        this.isFromCache = isFromCache
    }

    constructor(isFromCache: Boolean, data: T) {
        this.isFromCache = isFromCache
        this.data = data
    }

    override fun toString(): String {
        return "CacheResult(isFromCache=$isFromCache, data=$data)"
    }
}