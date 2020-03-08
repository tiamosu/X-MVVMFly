package com.tiamosu.fly.utils

import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException

/**
 * @author weixia
 * @date 2018/12/21.
 */
object RxJavaHelper {
    private val TAG = RxJavaHelper::class.java.simpleName

    @JvmStatic
    fun setRxJavaErrorHandler() {
        if (RxJavaPlugins.getErrorHandler() != null || RxJavaPlugins.isLockdown()) {
            return
        }
        RxJavaPlugins.setErrorHandler(Consumer { throwable ->
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
            Log.e(TAG, "Undeliverable exception received, not sure what to do", e)
        })
    }
}
