package com.tiamosu.fly.http.cookie.store

import okhttp3.Cookie
import okhttp3.HttpUrl

interface CookieStore {

    fun add(url: HttpUrl, cookies: MutableList<Cookie>)

    operator fun get(url: HttpUrl): MutableList<Cookie>

    fun getCookies(): MutableList<Cookie>

    fun remove(url: HttpUrl, cookie: Cookie): Boolean

    fun removeAll(): Boolean
}
