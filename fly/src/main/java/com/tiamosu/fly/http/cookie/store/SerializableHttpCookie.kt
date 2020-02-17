package com.tiamosu.fly.http.cookie.store

import okhttp3.Cookie
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * from http://stackoverflow.com/questions/25461792/persistent-cookie-store-using-okhttp-2-on-android
 * and<br></br>
 * http://www.geebr.com/post/okHttp3%E4%B9%8BCookies%E7%AE%A1%E7%90%86%E5%8F%8A%E6%8C%81%E4%B9%85%E5%8C%96
 */
@Suppress("unused")
class SerializableHttpCookie(@field:Transient private val cookie: Cookie) : Serializable {
    @Transient
    private var clientCookie: Cookie? = null

    fun getCookie(): Cookie {
        return if (clientCookie != null) clientCookie!! else cookie
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie.name())
        out.writeObject(cookie.value())
        out.writeLong(cookie.expiresAt())
        out.writeObject(cookie.domain())
        out.writeObject(cookie.path())
        out.writeBoolean(cookie.secure())
        out.writeBoolean(cookie.httpOnly())
        out.writeBoolean(cookie.hostOnly())
        out.writeBoolean(cookie.persistent())
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        val name = `in`.readObject() as String
        val value = `in`.readObject() as String
        val expiresAt = `in`.readLong()
        val domain = `in`.readObject() as String
        val path = `in`.readObject() as String
        val secure = `in`.readBoolean()
        val httpOnly = `in`.readBoolean()
        val hostOnly = `in`.readBoolean()
        var builder = Cookie.Builder()
        builder = builder.name(name)
        builder = builder.value(value)
        builder = builder.expiresAt(expiresAt)
        builder = if (hostOnly) builder.hostOnlyDomain(domain) else builder.domain(domain)
        builder = builder.path(path)
        builder = if (secure) builder.secure() else builder
        builder = if (httpOnly) builder.httpOnly() else builder
        clientCookie = builder.build()
    }

    companion object {
        private const val serialVersionUID = 6374381323722046732L
    }
}