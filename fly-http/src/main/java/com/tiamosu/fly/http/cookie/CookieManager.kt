package com.tiamosu.fly.http.cookie

import com.tiamosu.fly.http.cookie.store.MemoryCookieStore
import com.tiamosu.fly.http.cookie.store.PersistentCookieStore
import com.tiamosu.fly.utils.FlyUtils.getAppComponent

/**
 * @author tiamosu
 * @date 2020/2/24.
 */
object CookieManager {

    @JvmStatic
    val persistentCookieJar by lazy {
        CookieJarImpl(PersistentCookieStore())
    }

    @JvmStatic
    val memoryCookieJar by lazy {
        CookieJarImpl(MemoryCookieStore())
    }

    @JvmStatic
    fun clearSession() {
        val cookieJar = getAppComponent().okHttpClient().cookieJar()
        if (cookieJar is CookieJarImpl) {
            cookieJar.getCookieStore().removeAll()
        }
    }
}