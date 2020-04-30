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
    private val handlerCallback: Handler.Callback?//hard reference to Callback. We need to keep callback in memory
    private val exec: ExecHandler
    private val reentrantLock = ReentrantLock()

    @VisibleForTesting
    private val chainedRef = ChainedRef(reentrantLock, null)

    constructor() {
        handlerCallback = null
        exec = ExecHandler()
    }

    constructor(callback: Handler.Callback) {
        handlerCallback = callback
        exec = ExecHandler(WeakReference(callback))
    }

    constructor(looper: Looper) {
        handlerCallback = null
        exec = ExecHandler(looper)
    }

    constructor(looper: Looper, callback: Handler.Callback) {
        handlerCallback = callback
        exec = ExecHandler(looper, WeakReference(callback))
    }

    fun getLooper(): Looper {
        return exec.looper
    }

    fun post(r: Runnable): Boolean {
        return exec.post(wrapRunnable(r))
    }

    fun postAtTime(r: Runnable, uptimeMillis: Long): Boolean {
        return exec.postAtTime(wrapRunnable(r), uptimeMillis)
    }

    fun postAtTime(r: Runnable, token: Any?, uptimeMillis: Long): Boolean {
        return exec.postAtTime(wrapRunnable(r), token, uptimeMillis)
    }

    fun postDelayed(r: Runnable, delayMillis: Long): Boolean {
        return exec.postDelayed(wrapRunnable(r), delayMillis)
    }

    fun postAtFrontOfQueue(r: Runnable): Boolean {
        return exec.postAtFrontOfQueue(wrapRunnable(r))
    }

    fun removeCallbacks(r: Runnable) {
        val runnable = chainedRef.remove(r)
        if (runnable != null) {
            exec.removeCallbacks(runnable)
        }
    }

    fun removeCallbacks(r: Runnable, token: Any?) {
        val runnable = chainedRef.remove(r)
        if (runnable != null) {
            exec.removeCallbacks(runnable, token)
        }
    }

    fun sendMessage(msg: Message): Boolean {
        return exec.sendMessage(msg)
    }

    fun sendEmptyMessage(what: Int): Boolean {
        return exec.sendEmptyMessage(what)
    }

    fun sendEmptyMessageDelayed(what: Int, delayMillis: Long): Boolean {
        return exec.sendEmptyMessageDelayed(what, delayMillis)
    }

    fun sendEmptyMessageAtTime(what: Int, uptimeMillis: Long): Boolean {
        return exec.sendEmptyMessageAtTime(what, uptimeMillis)
    }

    fun sendMessageDelayed(msg: Message, delayMillis: Long): Boolean {
        return exec.sendMessageDelayed(msg, delayMillis)
    }

    fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean {
        return exec.sendMessageAtTime(msg, uptimeMillis)
    }

    fun sendMessageAtFrontOfQueue(msg: Message): Boolean {
        return exec.sendMessageAtFrontOfQueue(msg)
    }

    fun removeMessages(what: Int) {
        exec.removeMessages(what)
    }

    fun removeMessages(what: Int, `object`: Any?) {
        exec.removeMessages(what, `object`)
    }

    fun removeCallbacksAndMessages(token: Any?) {
        exec.removeCallbacksAndMessages(token)
    }

    fun hasMessages(what: Int): Boolean {
        return exec.hasMessages(what)
    }

    fun hasMessages(what: Int, `object`: Any?): Boolean {
        return exec.hasMessages(what, `object`)
    }

    private fun wrapRunnable(r: Runnable): WeakRunnable {
        val hardRef = ChainedRef(reentrantLock, r)
        chainedRef.insertAfter(hardRef)
        return hardRef.wrapper
    }

    private class ExecHandler : Handler {
        private val callback: WeakReference<Callback>?

        constructor() {
            callback = null
        }

        constructor(callback: WeakReference<Callback>) {
            this.callback = callback
        }

        constructor(looper: Looper) : super(looper) {
            callback = null
        }

        constructor(looper: Looper, callback: WeakReference<Callback>) : super(looper) {
            this.callback = callback
        }

        override fun handleMessage(msg: Message) {
            callback?.get()?.handleMessage(msg)
        }
    }

    private class WeakRunnable constructor(
        private val delegate: WeakReference<Runnable?>,
        private val reference: WeakReference<ChainedRef>
    ) : Runnable {

        override fun run() {
            val delegate = delegate.get()
            val reference = reference.get()
            reference?.remove()
            delegate?.run()
        }
    }

    private class ChainedRef(val lock: Lock, val runnable: Runnable?) {
        var next: ChainedRef? = null
        var prev: ChainedRef? = null
        val wrapper = WeakRunnable(WeakReference(runnable), WeakReference(this))

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
