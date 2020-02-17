package com.tiamosu.fly.http.cookie

import androidx.annotation.NonNull
import com.tiamosu.fly.http.cookie.store.CookieStore
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieJarImpl(@NonNull private val cookieStore: CookieStore) : CookieJar {

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        cookieStore.add(url, cookies)
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url]
    }
}
