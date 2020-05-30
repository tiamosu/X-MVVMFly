@file:JvmName("RxJavaHelper")

package com.tiamosu.fly.utils

import android.util.Log
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException

/**
 * 描述：设置 RxJava2 全局错误处理，防止抛异常
 *
 * @author tiamosu
 * @date 2020/3/18.
 */

@JvmOverloads
fun setRxJavaErrorHandler(handlerCallback: RxJavaErrorHandlerCallback? = null) {
    if (RxJavaPlugins.getErrorHandler() != null || RxJavaPlugins.isLockdown()) {
        return
    }
    RxJavaPlugins.setErrorHandler(Consumer { throwable ->
        if (handlerCallback?.onCallback(throwable) == true) {
            return@Consumer
        }
        var e = throwable
        if (e is UndeliverableException) {
            e = e.cause
        }
        if (e is SocketException || e is IOException) {
            // fine, irrelevant network problem or API that throws on cancellation
            return@Consumer
        }
        if (e is InterruptedException) {
            // fine, some blocking code was interrupted by a dispose call
            return@Consumer
        }
        if (e is NullPointerException || e is IllegalArgumentException) {
            // that's likely a bug in the application
            Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(
                Thread.currentThread(),
                e
            )
            return@Consumer
        }
        if (e is IllegalStateException) {
            // that's a bug in RxJava or in a custom operator
            Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(
                Thread.currentThread(),
                e
            )
            return@Consumer
        }
        Log.e("RxJavaHelper", "Undeliverable exception received, not sure what to do", e)
    })
}

fun interface RxJavaErrorHandlerCallback {
    fun onCallback(throwable: Throwable?): Boolean
}