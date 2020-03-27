package com.tiamosu.fly.http.cookie

import android.text.TextUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.http.utils.dLog
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

/**
 * 描述：cookie存储器
 *
 * @author tiamosu
 * @date 2020/3/1.
 */
class PersistentCookieStore {
    private val cookies by lazy { HashMap<String, ConcurrentHashMap<String, Cookie>>() }
    private val cookiePrefs by lazy { Utils.getApp().getSharedPreferences(COOKIE_PREFS, 0) }

    init {
        val prefsMap = cookiePrefs.all
        for ((key, value) in prefsMap) {
            val cookieNames = TextUtils.split(value as? String, ",")
            for (name in cookieNames) {
                val encodedCookie = cookiePrefs.getString(name, null) ?: break
                val decodedCookie = decodeCookie(encodedCookie)
                if (decodedCookie != null) {
                    if (!cookies.containsKey(key)) {
                        cookies[key] = ConcurrentHashMap()
                    }
                    cookies[key]?.set(name, decodedCookie)
                }
            }
        }
    }

    protected fun getCookieToken(cookie: Cookie): String {
        return cookie.name + "@" + cookie.domain
    }

    fun add(url: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)
        // 添加 host key. 否则有可能抛空.
        val host = url.host
        if (!cookies.containsKey(host)) {
            cookies[host] = ConcurrentHashMap()
        }
        // 删除已经有的.
        if (cookies.containsKey(url.host)) {
            cookies[host]?.remove(name)
        }
        // 添加新的进去
        cookies[host]?.set(name, cookie)
        // 是否保存到 SP 中
        if (cookie.persistent) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putString(host, cookies[host]?.keys?.let { TextUtils.join(",", it) })
            prefsWriter.putString(name, encodeCookie(SerializableHttpCookie(cookie)))
            prefsWriter.apply()
        } else {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.remove(url.host)
            prefsWriter.remove(name)
            prefsWriter.apply()
        }
    }

    fun addCookies(cookies: List<Cookie>) {
        for (cookie in cookies) {
            val domain = cookie.domain
            var domainCookies = this.cookies[domain]
            if (domainCookies == null) {
                domainCookies = ConcurrentHashMap()
                this.cookies[domain] = domainCookies
            }
        }
    }

    operator fun get(url: HttpUrl): List<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(url.host)) cookies[url.host]?.values?.let { ret.addAll(it) }
        return ret
    }

    fun removeAll(): Boolean {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.clear()
        prefsWriter.apply()
        cookies.clear()
        return true
    }

    fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)
        val host = url.host
        return if (cookies.containsKey(url.host) && cookies[host]?.containsKey(name) == true) {
            cookies[host]?.remove(name)
            val prefsWriter = cookiePrefs.edit()
            if (cookiePrefs.contains(name)) {
                prefsWriter.remove(name)
            }
            prefsWriter.putString(
                host,
                cookies[host]?.keys?.let { TextUtils.join(",", it) }
            )
            prefsWriter.apply()
            true
        } else {
            false
        }
    }

    fun getCookies(): List<Cookie>? {
        val ret = ArrayList<Cookie>()
        for (key in cookies.keys) cookies[key]?.values?.let { ret.addAll(it) }
        return ret
    }

    /**
     * cookies to string
     */
    protected fun encodeCookie(cookie: SerializableHttpCookie?): String? {
        if (cookie == null) return null
        val os = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(cookie)
        } catch (e: IOException) {
            dLog("IOException in encodeCookie" + e.message)
            return null
        }
        return byteArrayToHexString(os.toByteArray())
    }

    /**
     * String to cookies
     */
    protected fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as? SerializableHttpCookie)?.getCookie()
        } catch (e: IOException) {
            dLog("IOException in decodeCookie" + e.message)
        } catch (e: ClassNotFoundException) {
            dLog("ClassNotFoundException in decodeCookie" + e.message)
        }
        return cookie
    }

    /**
     * byteArrayToHexString
     */
    protected fun byteArrayToHexString(bytes: ByteArray): String? {
        val builder = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v: Int = element.toInt() and 0xff
            if (v < 16) {
                builder.append('0')
            }
            builder.append(Integer.toHexString(v))
        }
        return builder.toString().toUpperCase(Locale.US)
    }

    /**
     * hexStringToByteArray
     */
    protected fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(
                hexString[i + 1],
                16
            )).toByte()
            i += 2
        }
        return data
    }

    companion object {
        private const val COOKIE_PREFS = "Cookies_Prefs"
    }
}
