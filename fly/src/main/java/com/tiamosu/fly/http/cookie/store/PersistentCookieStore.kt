package com.tiamosu.fly.http.cookie.store

import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.Utils
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * <pre>
 * OkHttpClient client = new OkHttpClient.Builder()
 * .cookieJar(new JavaNetCookieJar(new CookieManager(
 * new PersistentCookieStore(getApplicationContext()),
 * CookiePolicy.ACCEPT_ALL))
 * .build();
 *
</pre> *
 *
 *
 * from http://stackoverflow.com/questions/25461792/persistent-cookie-store-using-okhttp-2-on-android
 *
 *
 * <br></br>
 * A persistent cookie store which implements the Apache HttpClient CookieStore interface.
 * Cookies are stored and will persist on the user's device between application sessions since they
 * are serialized and stored in SharedPreferences. Instances of this class are
 * designed to be used with AsyncHttpClient#setCookieStore, but can also be used with a
 * regular old apache HttpClient/HttpContext if you prefer.
 */
@Suppress("unused")
class PersistentCookieStore : CookieStore {
    private val cookies: HashMap<String, ConcurrentHashMap<String, Cookie>>
    private val cookiePrefs: SharedPreferences

    /**
     * Construct a persistent cookie store.
     */
    init {
        cookiePrefs = Utils.getApp().getSharedPreferences(COOKIE_PREFS, 0)
        cookies = HashMap()

        // Load any previously stored cookies into the store
        val prefsMap = cookiePrefs.all
        for ((key, value) in prefsMap) {
            if (value != null && !(value as String).startsWith(COOKIE_NAME_PREFIX)) {
                val cookieNames = TextUtils.split(value, ",")
                for (name in cookieNames) {
                    val encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null)
                            ?: break
                    val decodedCookie = decodeCookie(encodedCookie)
                    decodedCookie?.apply {
                        if (!cookies.containsKey(key)) {
                            cookies[key] = ConcurrentHashMap()
                        }
                        val cookieConcurrentHashMap = cookies[key]
                        if (cookieConcurrentHashMap != null) {
                            cookieConcurrentHashMap[name] = decodedCookie
                        }
                    }
                }
            }
        }
    }

    private fun add(uri: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)
        if (cookie.persistent()) {
            if (!cookies.containsKey(uri.host())) {
                cookies[uri.host()] = ConcurrentHashMap()
            }
            val cookieConcurrentHashMap = cookies[uri.host()]
            if (cookieConcurrentHashMap != null) {
                cookieConcurrentHashMap[name] = cookie
            }
        } else {
            if (!cookies.containsKey(uri.host())) {
                return
            }
            val cookieConcurrentHashMap = cookies[uri.host()]
            cookieConcurrentHashMap?.remove(name)
        }

        // Save cookie into persistent store
        val prefsWriter = cookiePrefs.edit()
        val cookieConcurrentHashMap = cookies[uri.host()]
        if (cookieConcurrentHashMap != null) {
            prefsWriter.putString(uri.host(), TextUtils.join(",", cookieConcurrentHashMap.keys))
        }
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(SerializableHttpCookie(cookie)))
        prefsWriter.apply()
    }

    private fun getCookieToken(cookie: Cookie): String {
        return cookie.name() + cookie.domain()
    }

    override fun add(url: HttpUrl, cookies: MutableList<Cookie>) {
        for (cookie in cookies) {
            add(url, cookie)
        }
    }

    override operator fun get(url: HttpUrl): MutableList<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(url.host())) {
            val cookieConcurrentHashMap = this.cookies[url.host()] ?: return ArrayList()
            val cookies = cookieConcurrentHashMap.values
            for (cookie in cookies) {
                if (isCookieExpired(cookie)) {
                    remove(url, cookie)
                } else {
                    ret.add(cookie)
                }
            }
        }
        return ret
    }

    override fun removeAll(): Boolean {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.clear()
        prefsWriter.apply()
        cookies.clear()
        return true
    }

    override fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)
        val cookieConcurrentHashMap = cookies[url.host()] ?: return false
        return if (cookies.containsKey(url.host()) && cookieConcurrentHashMap.containsKey(name)) {
            cookieConcurrentHashMap.remove(name)

            val prefsWriter = cookiePrefs.edit()
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name)
            }
            prefsWriter.putString(url.host(), TextUtils.join(",", cookieConcurrentHashMap.keys))
            prefsWriter.apply()
            true
        } else {
            false
        }
    }

    override fun getCookies(): MutableList<Cookie> {
        val ret = ArrayList<Cookie>()
        for (key in cookies.keys) {
            val cookieConcurrentHashMap = cookies[key]
            if (cookieConcurrentHashMap != null) {
                ret.addAll(cookieConcurrentHashMap.values)
            }
        }
        return ret
    }

    private fun encodeCookie(cookie: SerializableHttpCookie?): String? {
        if (cookie == null) {
            return null
        }
        val bytes: ByteArray
        try {
            val os = ByteArrayOutputStream()
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(cookie)
            bytes = os.toByteArray()
            os.close()
            outputStream.close()
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e)
            return null
        }
        return byteArrayToHexString(bytes)
    }

    private fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as SerializableHttpCookie).getCookie()
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e)
        } catch (e: ClassNotFoundException) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e)
        }
        return cookie
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
     * large Base64 libraries. Can be overridden if you like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    private fun byteArrayToHexString(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v = element.toInt() and 0xff
            if (v < 16) {
                sb.append('0')
            }
            sb.append(Integer.toHexString(v))
        }
        return sb.toString().toUpperCase(Locale.US)
    }

    /**
     * Converts hex values from strings to byte arra
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    companion object {
        private const val LOG_TAG = "PersistentCookieStore"
        private const val COOKIE_PREFS = "CookiePrefsFile"
        private const val COOKIE_NAME_PREFIX = "cookie_"

        private fun isCookieExpired(cookie: Cookie): Boolean {
            return cookie.expiresAt() < System.currentTimeMillis()
        }
    }
}
