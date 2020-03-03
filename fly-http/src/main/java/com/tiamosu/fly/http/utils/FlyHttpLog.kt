package com.tiamosu.fly.http.utils

import android.text.TextUtils
import android.util.Log

/**
 * @author tiamosu
 * @date 2020/3/1.
 */
object FlyHttpLog {
    var customTagPrefix = "FlyHttp_"
    var allowD = true
    var allowE = true
    var allowI = true
    var allowV = true
    var allowW = true
    var allowWtf = true
    var customLogger: CustomLogger? = null

    private val callerStackTraceElement: StackTraceElement by lazy {
        Thread.currentThread().stackTrace[4]
    }

    private fun generateTag(caller: StackTraceElement): String {
        var tag = "%s.%s(L:%d)"
        var callerClazzName = caller.className
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        tag = String.format(
            tag,
            callerClazzName,
            caller.methodName,
            caller.lineNumber
        )
        tag = if (TextUtils.isEmpty(customTagPrefix)) tag else "$customTagPrefix:$tag"
        return tag
    }

    @JvmStatic
    fun d(content: String?) {
        if (!allowD) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.d(tag, content)
        } else {
            Log.d(tag, content ?: "")
        }
    }

    @JvmStatic
    fun d(content: String?, tr: Throwable?) {
        if (!allowD) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.d(tag, content, tr)
        } else {
            Log.d(tag, content, tr)
        }
    }

    @JvmStatic
    fun e(content: String?) {
        if (!allowE) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.e(tag, content)
        } else {
            Log.e(tag, content ?: "")
        }
    }

    @JvmStatic
    fun e(e: Exception) {
        if (!allowE) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.e(tag, e.message, e)
        } else {
            Log.e(tag, e.message, e)
        }
    }

    @JvmStatic
    fun e(content: String?, tr: Throwable?) {
        if (!allowE) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.e(tag, content, tr)
        } else {
            Log.e(tag, content, tr)
        }
    }

    @JvmStatic
    fun i(content: String?) {
        if (!allowI) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.i(tag, content)
        } else {
            Log.i(tag, content ?: "")
        }
    }

    @JvmStatic
    fun i(content: String?, tr: Throwable?) {
        if (!allowI) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.i(tag, content, tr)
        } else {
            Log.i(tag, content, tr)
        }
    }

    @JvmStatic
    fun v(content: String?) {
        if (!allowV) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.v(tag, content)
        } else {
            Log.v(tag, content ?: "")
        }
    }

    @JvmStatic
    fun v(content: String?, tr: Throwable?) {
        if (!allowV) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.v(tag, content, tr)
        } else {
            Log.v(tag, content, tr)
        }
    }

    @JvmStatic
    fun w(content: String?) {
        if (!allowW) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.w(tag, content)
        } else {
            Log.w(tag, content ?: "")
        }
    }

    @JvmStatic
    fun w(content: String?, tr: Throwable?) {
        if (!allowW) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.w(tag, content, tr)
        } else {
            Log.w(tag, content, tr)
        }
    }

    @JvmStatic
    fun w(tr: Throwable?) {
        if (!allowW) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.w(tag, tr)
        } else {
            Log.w(tag, tr)
        }
    }

    @JvmStatic
    fun wtf(content: String?) {
        if (!allowWtf) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.wtf(tag, content)
        } else {
            Log.wtf(tag, content)
        }
    }

    @JvmStatic
    fun wtf(content: String?, tr: Throwable) {
        if (!allowWtf) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.wtf(tag, content, tr)
        } else {
            Log.wtf(tag, content, tr)
        }
    }

    @JvmStatic
    fun wtf(tr: Throwable) {
        if (!allowWtf) return
        val caller = callerStackTraceElement
        val tag = generateTag(caller)
        if (customLogger != null) {
            customLogger!!.wtf(tag, tr)
        } else {
            Log.wtf(tag, tr)
        }
    }

    interface CustomLogger {
        fun d(tag: String?, content: String?)
        fun d(tag: String?, content: String?, tr: Throwable?)
        fun e(tag: String?, content: String?)
        fun e(tag: String?, content: String?, tr: Throwable?)
        fun i(tag: String?, content: String?)
        fun i(tag: String?, content: String?, tr: Throwable?)
        fun v(tag: String?, content: String?)
        fun v(tag: String?, content: String?, tr: Throwable?)
        fun w(tag: String?, content: String?)
        fun w(tag: String?, content: String?, tr: Throwable?)
        fun w(tag: String?, tr: Throwable?)
        fun wtf(tag: String?, content: String?)
        fun wtf(tag: String?, content: String?, tr: Throwable?)
        fun wtf(tag: String?, tr: Throwable?)
    }
}