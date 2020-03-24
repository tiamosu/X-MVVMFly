package com.tiamosu.fly.integration.handler

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.VisibleForTesting
import java.lang.ref.WeakReference
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * @author tiamosu
 * @date 2018/8/3.
 */
@Suppress("unused")
class WeakHandler {
    private val callback: Handler.Callback? //hard reference to Callback. We need to keep callback in memory
    private val execHandler: ExecHandler
    private val lock by lazy { ReentrantLock() }

    @VisibleForTesting
    private val chainedRef by lazy { ChainedRef(lock, null) }

    constructor() {
        callback = null
        execHandler = ExecHandler()
    }

    constructor(callback: Handler.Callback) {
        this.callback = callback
        execHandler = ExecHandler(WeakReference(callback))
    }

    constructor(looper: Looper) {
        callback = null
        execHandler = ExecHandler(looper)
    }

    constructor(looper: Looper, callback: Handler.Callback) {
        this.callback = callback
        execHandler = ExecHandler(looper, WeakReference(callback))
    }

    fun getLooper(): Looper {
        return execHandler.looper
    }

    fun post(r: Runnable): Boolean {
        return execHandler.post(wrapRunnable(r))
    }

    fun postAtTime(r: Runnable, uptimeMillis: Long): Boolean {
        return execHandler.postAtTime(wrapRunnable(r), uptimeMillis)
    }

    fun postAtTime(r: Runnable, token: Any?, uptimeMillis: Long): Boolean {
        return execHandler.postAtTime(wrapRunnable(r), token, uptimeMillis)
    }

    fun postDelayed(r: Runnable, delayMillis: Long): Boolean {
        return execHandler.postDelayed(wrapRunnable(r), delayMillis)
    }

    fun postAtFrontOfQueue(r: Runnable): Boolean {
        return execHandler.postAtFrontOfQueue(wrapRunnable(r))
    }

    fun removeCallbacks(r: Runnable) {
        val runnable = chainedRef.remove(r)
        if (runnable != null) {
            execHandler.removeCallbacks(runnable)
        }
    }

    fun removeCallbacks(r: Runnable, token: Any?) {
        val runnable = chainedRef.remove(r)
        if (runnable != null) {
            execHandler.removeCallbacks(runnable, token)
        }
    }

    fun sendMessage(msg: Message): Boolean {
        return execHandler.sendMessage(msg)
    }

    fun sendEmptyMessage(what: Int): Boolean {
        return execHandler.sendEmptyMessage(what)
    }

    fun sendEmptyMessageDelayed(what: Int, delayMillis: Long): Boolean {
        return execHandler.sendEmptyMessageDelayed(what, delayMillis)
    }

    fun sendEmptyMessageAtTime(what: Int, uptimeMillis: Long): Boolean {
        return execHandler.sendEmptyMessageAtTime(what, uptimeMillis)
    }

    fun sendMessageDelayed(msg: Message, delayMillis: Long): Boolean {
        return execHandler.sendMessageDelayed(msg, delayMillis)
    }

    fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean {
        return execHandler.sendMessageAtTime(msg, uptimeMillis)
    }

    fun sendMessageAtFrontOfQueue(msg: Message): Boolean {
        return execHandler.sendMessageAtFrontOfQueue(msg)
    }

    fun removeMessages(what: Int) {
        execHandler.removeMessages(what)
    }

    fun removeMessages(what: Int, `object`: Any?) {
        execHandler.removeMessages(what, `object`)
    }

    fun removeCallbacksAndMessages(token: Any?) {
        execHandler.removeCallbacksAndMessages(token)
    }

    fun hasMessages(what: Int): Boolean {
        return execHandler.hasMessages(what)
    }

    fun hasMessages(what: Int, `object`: Any?): Boolean {
        return execHandler.hasMessages(what, `object`)
    }

    private fun wrapRunnable(r: Runnable): WeakRunnable {
        val hardRef = ChainedRef(lock, r)
        chainedRef.insertAfter(hardRef)
        return hardRef.wrapper
    }

    private class ExecHandler : Handler {
        private val callback: WeakReference<Callback>?

        internal constructor() {
            callback = null
        }

        internal constructor(callback: WeakReference<Callback>) {
            this.callback = callback
        }

        internal constructor(looper: Looper) : super(looper) {
            callback = null
        }

        internal constructor(looper: Looper, callback: WeakReference<Callback>) : super(looper) {
            this.callback = callback
        }

        override fun handleMessage(msg: Message) {
            callback?.get()?.handleMessage(msg)
        }
    }

    private class WeakRunnable internal constructor(
        private val delegate: WeakReference<Runnable?>,
        private val reference: WeakReference<ChainedRef>
    ) : Runnable {

        override fun run() {
            reference.get()?.remove()
            delegate.get()?.run()
        }
    }

    private class ChainedRef(internal val lock: Lock, internal val runnable: Runnable?) {
        internal var next: ChainedRef? = null
        internal var prev: ChainedRef? = null
        internal val wrapper by lazy {
            WeakRunnable(WeakReference(runnable), WeakReference(this))
        }

        fun remove(): WeakRunnable {
            lock.lock()
            try {
                prev?.next = next
                next?.prev = prev
                prev = null
                next = null
            } finally {
                lock.unlock()
            }
            return wrapper
        }

        fun insertAfter(candidate: ChainedRef) {
            lock.lock()
            try {
                next?.prev = candidate
                candidate.next = this.next
                this.next = candidate
                candidate.prev = this
            } finally {
                lock.unlock()
            }
        }

        fun remove(obj: Runnable): WeakRunnable? {
            lock.lock()
            try {
                var curr = this.next // Skipping head
                while (curr != null) {
                    if (curr.runnable === obj) { // We do comparison exactly how Handler does inside
                        return curr.remove()
                    }
                    curr = curr.next
                }
            } finally {
                lock.unlock()
            }
            return null
        }
    }
}
