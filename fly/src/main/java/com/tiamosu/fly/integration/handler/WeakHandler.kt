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
    private val mCallback: Handler.Callback?//hard reference to Callback. We need to keep callback in memory
    private val mExec: ExecHandler
    private val mLock = ReentrantLock()

    @VisibleForTesting
    private val mChainedRef = ChainedRef(mLock, null)

    constructor() {
        mCallback = null
        mExec = ExecHandler()
    }

    constructor(callback: Handler.Callback) {
        mCallback = callback
        mExec = ExecHandler(WeakReference(callback))
    }

    constructor(looper: Looper) {
        mCallback = null
        mExec = ExecHandler(looper)
    }

    constructor(looper: Looper, callback: Handler.Callback) {
        mCallback = callback
        mExec = ExecHandler(looper, WeakReference(callback))
    }

    fun getLooper(): Looper {
        return mExec.looper
    }

    fun post(r: Runnable): Boolean {
        return mExec.post(wrapRunnable(r))
    }

    fun postAtTime(r: Runnable, uptimeMillis: Long): Boolean {
        return mExec.postAtTime(wrapRunnable(r), uptimeMillis)
    }

    fun postAtTime(r: Runnable, token: Any?, uptimeMillis: Long): Boolean {
        return mExec.postAtTime(wrapRunnable(r), token, uptimeMillis)
    }

    fun postDelayed(r: Runnable, delayMillis: Long): Boolean {
        return mExec.postDelayed(wrapRunnable(r), delayMillis)
    }

    fun postAtFrontOfQueue(r: Runnable): Boolean {
        return mExec.postAtFrontOfQueue(wrapRunnable(r))
    }

    fun removeCallbacks(r: Runnable) {
        val runnable = mChainedRef.remove(r)
        if (runnable != null) {
            mExec.removeCallbacks(runnable)
        }
    }

    fun removeCallbacks(r: Runnable, token: Any?) {
        val runnable = mChainedRef.remove(r)
        if (runnable != null) {
            mExec.removeCallbacks(runnable, token)
        }
    }

    fun sendMessage(msg: Message): Boolean {
        return mExec.sendMessage(msg)
    }

    fun sendEmptyMessage(what: Int): Boolean {
        return mExec.sendEmptyMessage(what)
    }

    fun sendEmptyMessageDelayed(what: Int, delayMillis: Long): Boolean {
        return mExec.sendEmptyMessageDelayed(what, delayMillis)
    }

    fun sendEmptyMessageAtTime(what: Int, uptimeMillis: Long): Boolean {
        return mExec.sendEmptyMessageAtTime(what, uptimeMillis)
    }

    fun sendMessageDelayed(msg: Message, delayMillis: Long): Boolean {
        return mExec.sendMessageDelayed(msg, delayMillis)
    }

    fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean {
        return mExec.sendMessageAtTime(msg, uptimeMillis)
    }

    fun sendMessageAtFrontOfQueue(msg: Message): Boolean {
        return mExec.sendMessageAtFrontOfQueue(msg)
    }

    fun removeMessages(what: Int) {
        mExec.removeMessages(what)
    }

    fun removeMessages(what: Int, `object`: Any?) {
        mExec.removeMessages(what, `object`)
    }

    fun removeCallbacksAndMessages(token: Any?) {
        mExec.removeCallbacksAndMessages(token)
    }

    fun hasMessages(what: Int): Boolean {
        return mExec.hasMessages(what)
    }

    fun hasMessages(what: Int, `object`: Any?): Boolean {
        return mExec.hasMessages(what, `object`)
    }

    private fun wrapRunnable(r: Runnable): WeakRunnable {
        val hardRef = ChainedRef(mLock, r)
        mChainedRef.insertAfter(hardRef)
        return hardRef.wrapper
    }

    private class ExecHandler : Handler {
        private val mCallback: WeakReference<Callback>?

        internal constructor() {
            mCallback = null
        }

        internal constructor(callback: WeakReference<Callback>) {
            mCallback = callback
        }

        internal constructor(looper: Looper) : super(looper) {
            mCallback = null
        }

        internal constructor(looper: Looper, callback: WeakReference<Callback>) : super(looper) {
            mCallback = callback
        }

        override fun handleMessage(msg: Message) {
            mCallback?.get()?.handleMessage(msg)
        }
    }

    private class WeakRunnable internal constructor(
        private val mDelegate: WeakReference<Runnable?>,
        private val mReference: WeakReference<ChainedRef>
    ) : Runnable {

        override fun run() {
            val delegate = mDelegate.get()
            val reference = mReference.get()
            reference?.remove()
            delegate?.run()
        }
    }

    private class ChainedRef(internal val lock: Lock, internal val runnable: Runnable?) {
        internal var next: ChainedRef? = null
        internal var prev: ChainedRef? = null
        internal val wrapper: WeakRunnable =
            WeakRunnable(WeakReference(runnable), WeakReference(this))

        fun remove(): WeakRunnable {
            lock.lock()
            try {
                prev?.let {
                    it.next = next
                }
                next?.let {
                    it.prev = prev
                }
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
                next?.let {
                    it.prev = candidate
                }
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
