package com.tiamosu.fly.http.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * 描述：cookie管理器
 *
 * @author tiamosu
 * @date 2020/3/1.
 */
class CookieManger() : CookieJar {

    private val cookieStore: PersistentCookieStore by lazy { PersistentCookieStore() }

    fun addCookies(cookies: List<Cookie>) {
        cookieStore.addCookies(cookies)
    }

    fun saveFromResponse(url: HttpUrl, cookie: Cookie?) {
        if (cookie != null) {
            cookieStore.add(url, cookie)
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>?) {
        if (cookies != null && cookies.isNotEmpty()) {
            for (item in cookies) {
                cookieStore.add(url, item)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url]
    }

    fun remove(url: HttpUrl, cookie: Cookie) {
        cookieStore.remove(url, cookie)
    }

    fun removeAll() {
        cookieStore.removeAll()
    }
}