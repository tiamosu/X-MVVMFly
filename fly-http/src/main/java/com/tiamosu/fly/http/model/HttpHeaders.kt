package com.tiamosu.fly.http.model

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.http.utils.escapeParams
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 描述：头部参数
 *
 * @author tiamosu
 * @date 2020/2/26.
 */
class HttpHeaders : Serializable {

    val headersMap: LinkedHashMap<String, String> = linkedMapOf()

    fun put(key: String?, value: String?) {
        if (key != null && value != null) {
            headersMap[key] = value
        }
    }

    fun put(headers: Map<String?, String?>?) {
        if (headers?.isNotEmpty() == true) {
            headersMap.putAll(escapeParams(headers))
        }
    }

    fun put(headers: HttpHeaders?) {
        if (headers?.headersMap?.isNotEmpty() == true) {
            val set: Set<Map.Entry<String, String>> = headers.headersMap.entries
            for ((key, value) in set) {
                headersMap.remove(key)
                headersMap[key] = value
            }
        }
    }

    fun isEmpty(): Boolean {
        return headersMap.isEmpty()
    }

    operator fun get(key: String): String? {
        return headersMap[key]
    }

    fun remove(key: String): String? {
        return headersMap.remove(key)
    }

    fun clear() {
        headersMap.clear()
    }

    val names: Set<String>
        get() = headersMap.keys

    fun toJSONString(): String {
        val jsonObject = JSONObject()
        try {
            for ((key, value) in headersMap) {
                jsonObject.put(key, value)
            }
        } catch (e: JSONException) {
            LogUtils.e(e)
        }
        return jsonObject.toString()
    }

    override fun toString(): String {
        return "HttpHeaders{headersMap=$headersMap}"
    }

    companion object {
        const val FORMAT_HTTP_DATA = "EEE, dd MMM y HH:mm:ss 'GMT'"
        val GMT_TIME_ZONE: TimeZone = TimeZone.getTimeZone("GMT")
        const val HEAD_KEY_RESPONSE_CODE = "ResponseCode"
        const val HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage"
        const val HEAD_KEY_ACCEPT = "Accept"
        const val HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding"
        const val HEAD_VALUE_ACCEPT_ENCODING = "gzip, deflate"
        const val HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language"
        const val HEAD_KEY_CONTENT_TYPE = "Content-Type"
        const val HEAD_KEY_CONTENT_LENGTH = "Content-Length"
        const val HEAD_KEY_CONTENT_ENCODING = "Content-Encoding"
        const val HEAD_KEY_CONTENT_DISPOSITION = "Content-Disposition"
        const val HEAD_KEY_CONTENT_RANGE = "Content-Range"
        const val HEAD_KEY_RANGE = "Range"
        const val HEAD_KEY_CACHE_CONTROL = "Cache-Control"
        const val HEAD_KEY_CONNECTION = "Connection"
        const val HEAD_VALUE_CONNECTION_KEEP_ALIVE = "keep-alive"
        const val HEAD_VALUE_CONNECTION_CLOSE = "close"
        const val HEAD_KEY_DATE = "Date"
        const val HEAD_KEY_EXPIRES = "Expires"
        const val HEAD_KEY_E_TAG = "ETag"
        const val HEAD_KEY_PRAGMA = "Pragma"
        const val HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since"
        const val HEAD_KEY_IF_NONE_MATCH = "If-None-Match"
        const val HEAD_KEY_LAST_MODIFIED = "Last-Modified"
        const val HEAD_KEY_LOCATION = "Location"
        const val HEAD_KEY_USER_AGENT = "User-Agent"
        const val HEAD_KEY_COOKIE = "Cookie"
        const val HEAD_KEY_COOKIE2 = "Cookie2"
        const val HEAD_KEY_SET_COOKIE = "Set-Cookie"
        const val HEAD_KEY_SET_COOKIE2 = "Set-Cookie2"

        /**
         * Accept-Language: zh-CN,zh;q=0.8
         */
        var acceptLanguage: String? = null
            get() {
                if (TextUtils.isEmpty(field)) {
                    val locale = Locale.getDefault()
                    val language = locale.language
                    val country = locale.country
                    val acceptLanguageBuilder = StringBuilder(language)
                    if (!TextUtils.isEmpty(country)) {
                        acceptLanguageBuilder
                            .append('-')
                            .append(country)
                            .append(',')
                            .append(language)
                            .append(";q=0.8")
                    }
                    field = acceptLanguageBuilder.toString()
                    return field
                }
                return field
            }

        /**
         * User-Agent: Mozilla/5.0 (Linux; U; Android 5.0.2; zh-cn; Redmi Note 3 Build/LRX22G)
         * AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Mobile Safari/537.36
         */
        @get:SuppressLint("PrivateApi")
        var userAgent: String? = null
            get() {
                if (TextUtils.isEmpty(field)) {
                    var webUserAgent: String? = null
                    try {
                        val sysResCls = Class.forName("com.android.internal.R\$string")
                        val webUserAgentField = sysResCls.getDeclaredField("web_user_agent")
                        val resId = webUserAgentField[null] as? Int
                        webUserAgent = if (resId is Int) Utils.getApp().getString(resId) else ""
                    } catch (e: Exception) { // We have nothing to do
                    }
                    if (TextUtils.isEmpty(webUserAgent)) {
                        webUserAgent =
                            "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 %sSafari/533.1"
                    }
                    val locale = Locale.getDefault()
                    val buffer = StringBuffer()
                    // Add version
                    val version = Build.VERSION.RELEASE
                    if (version.isNotEmpty()) {
                        buffer.append(version)
                    } else { // default to "1.0"
                        buffer.append("1.0")
                    }
                    buffer.append("; ")
                    val language = locale.language
                    buffer.append(language.toLowerCase(locale))
                    val country = locale.country
                    if (!TextUtils.isEmpty(country)) {
                        buffer.append("-")
                        buffer.append(country.toLowerCase(locale))
                    }
                    // add the model for the release build
                    if ("REL" == Build.VERSION.CODENAME) {
                        val model = URLEncoder.encode(DeviceUtils.getModel(), "UTF-8")
                        if (model.isNotEmpty()) {
                            buffer.append("; ")
                            buffer.append(model)
                        }
                    }
                    val id = Build.ID
                    if (id.isNotEmpty()) {
                        buffer.append(" Build/")
                        buffer.append(id)
                    }
                    field = String.format(webUserAgent!!, buffer, "Mobile ")
                    return field
                }
                return field
            }

        fun getDate(gmtTime: String?): Long {
            return try {
                parseGMTToMillis(gmtTime)
            } catch (e: ParseException) {
                0
            }
        }

        fun getDate(milliseconds: Long): String {
            return formatMillisToGMT(milliseconds)
        }

        fun getExpiration(expiresTime: String?): Long {
            return try {
                parseGMTToMillis(expiresTime)
            } catch (e: ParseException) {
                -1
            }
        }

        fun getLastModified(lastModified: String?): Long {
            return try {
                parseGMTToMillis(lastModified)
            } catch (e: ParseException) {
                0
            }
        }

        // first http1.1, second http1.0
        fun getCacheControl(cacheControl: String?, pragma: String): String {
            return cacheControl ?: pragma
        }

        @Throws(ParseException::class)
        fun parseGMTToMillis(gmtTime: String?): Long {
            if (TextUtils.isEmpty(gmtTime)) {
                return 0
            }
            val formatter = SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US)
            formatter.timeZone = GMT_TIME_ZONE
            val date = formatter.parse(gmtTime!!)
            return date?.time ?: 0L
        }

        fun formatMillisToGMT(milliseconds: Long): String {
            val date = Date(milliseconds)
            val simpleDateFormat = SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US)
            simpleDateFormat.timeZone = GMT_TIME_ZONE
            return simpleDateFormat.format(date)
        }
    }
}