package com.tiamosu.fly.core.config

import android.content.Context
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener
import org.json.JSONException
import retrofit2.HttpException
import java.io.NotSerializableException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.net.ssl.SSLHandshakeException

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class ResponseErrorListenerImpl : ResponseErrorListener {

    override fun handleResponseError(context: Context, t: Throwable?) {
        //这里不光只能打印错误, 还可以根据不同的错误做出不同的逻辑处理
        //这里只是对几个常用错误进行简单的处理, 展示这个类的用法, 在实际开发中请您自行对更多错误进行更严谨的处理
        val msg = parseError(t)
        ToastUtils.showShort(msg)
    }

    companion object {
        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val REQUEST_TIMEOUT = 408
        private const val INTERNAL_SERVER_ERROR = 500
        private const val BAD_GATEWAY = 502
        private const val SERVICE_UNAVAILABLE = 503
        private const val GATEWAY_TIMEOUT = 504

        fun parseError(t: Throwable?): String {
            return if (t is HttpException) {
                convertStatusCode(t)
            } else if (t is JsonSyntaxException
                || t is JSONException
                || t is JsonParseException
                || t is ParseException
                || t is NotSerializableException
            ) {
                "请求失败~"
            } else if (t is ClassCastException) {
                "请求失败~"
            } else if (t is ConnectException) {
                "无法连接网络"
            } else if (t is SocketTimeoutException) {
                "请求网络超时"
            } else if (t is SSLHandshakeException) {
                "无法连接网络"
            } else if (t is UnknownHostException) {
                "无法连接网络"
            } else if (t is NullPointerException) {
                "请求失败~"
            } else if (t is RuntimeException) {
                t.message!!
            } else if (!NetworkUtils.isConnected()) {
                "无法连接网络"
            } else {
                "请求失败~"
            }
        }

        private fun convertStatusCode(httpException: HttpException): String {
            return when (httpException.code()) {
                UNAUTHORIZED, FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, GATEWAY_TIMEOUT,
                INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE,
                -> "连接服务器超时"
                else -> "连接服务器超时"
            }
        }
    }
}