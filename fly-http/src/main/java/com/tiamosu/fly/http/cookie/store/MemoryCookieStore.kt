package com.tiamosu.fly.http.cookie.store

import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.*

class MemoryCookieStore : CookieStore {
    private val allCookies = HashMap<String, MutableList<Cookie>>()

    override fun add(url: HttpUrl, cookies: MutableList<Cookie>) {
        val oldCookies = allCookies[url.host()]
        if (oldCookies == null) {
            allCookies[url.host()] = cookies
        }
        oldCookies?.apply {
            val itNew = cookies.iterator()
            val itOld = iterator()
            while (itNew.hasNext()) {
                val va = itNew.next().name()
                while (itOld.hasNext()) {
                    val v = itOld.next().name()
                    if (va == v) {
                        itOld.remove()
                    }
                }
            }
            addAll(cookies)
        }
    }

    override operator fun get(url: HttpUrl): MutableList<Cookie> {
        var cookies = allCookies[url.host()]
        if (cookies == null) {
            cookies = ArrayList()
            allCookies[url.host()] = cookies
        }
        return cookies
    }

    override fun removeAll(): Boolean {
        allCookies.clear()
        return true
    }

    override fun getCookies(): MutableList<Cookie> {
        val cookies = ArrayList<Cookie>()
        val httpUrls = allCookies.keys
        for (url in httpUrls) {
            val list = allCookies[url]
            if (list != null) {
                cookies.addAll(list)
            }
        }
        return cookies
    }

    override fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val cookies = allCookies[url.host()]
        return cookies?.remove(cookie) ?: false
    }
}
