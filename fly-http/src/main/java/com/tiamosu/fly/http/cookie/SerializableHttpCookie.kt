package com.tiamosu.fly.http.cookie

import okhttp3.Cookie
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 描述：对存储的cookie进行序列化
 *
 * @author tiamosu
 * @date 2020/3/1.
 */
class SerializableHttpCookie(@field:Transient private val cookie: Cookie) : Serializable {
    @Transient
    private var clientCookie: Cookie? = null

    fun getCookie(): Cookie {
        return if (clientCookie != null) clientCookie!! else cookie
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie.name)
        out.writeObject(cookie.value)
        out.writeLong(cookie.expiresAt)
        out.writeObject(cookie.domain)
        out.writeObject(cookie.path)
        out.writeBoolean(cookie.secure)
        out.writeBoolean(cookie.httpOnly)
        out.writeBoolean(cookie.hostOnly)
        out.writeBoolean(cookie.persistent)
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
}