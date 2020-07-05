package com.tiamosu.fly.callback

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.internal.SafeIterableMap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 描述：专为 Event LiveData 改造的底层 LiveData 支持
 *
 * @author tiamosu
 * @date 2020/6/13.
 */
abstract class EventBaseLiveData<T> {
    private val dataLock = Any()

    @SuppressLint("RestrictedApi")
    private val observers = SafeIterableMap<EventObserver<T>, ObserverWrapper>()

    private var activeCount = 0

    @Volatile
    private var data: Any? = null

    @Volatile
    var pendingData = NOT_SET
    private var version = 0

    private var dispatchingValue = false
    private var dispatchInvalidated = false

    @Suppress("UNCHECKED_CAST")
    private val postValueRunnable by lazy {
        Runnable {
            var newValue: Any
            synchronized(dataLock) {
                newValue = pendingData
                pendingData = NOT_SET
            }
            setValue(newValue as? Event<T>)
        }
    }

    /**
     * Creates a LiveData initialized with the given `value`.
     *
     * @param value initial value
     */
    constructor(value: Event<T>) {
        data = value
        version = START_VERSION + 1
    }

    /**
     * Creates a LiveData with no value assigned to it.
     */
    constructor() {
        data = NOT_SET
        version = START_VERSION
    }

    @Suppress("UNCHECKED_CAST")
    private fun considerNotify(observer: ObserverWrapper) {
        if (!observer.active) {
            return
        }
        // Check latest state b4 dispatch. Maybe it changed state but we didn't get the event yet.
        //
        // we still first check observer.active to keep it as the entrance for events. So even if
        // the observer moved to an active state, if we've not received that event, we better not
        // notify for a more predictable notification order.
        if (!observer.shouldBeActive()) {
            observer.activeStateChanged(false)
            return
        }
        if (observer.lastVersion >= version) {
            return
        }
        observer.lastVersion = version
        (data as? Event<T>)?.getContent()?.apply {
            observer.eventObserver.onReceived(this)
        }
    }

    /* synthetic access */
    @SuppressLint("RestrictedApi")
    @Suppress("UNCHECKED_CAST")
    open fun dispatchingValue(wrapper: ObserverWrapper?) {
        if (dispatchingValue) {
            dispatchInvalidated = true
            return
        }
        dispatchingValue = true
        var initiator = wrapper
        do {
            dispatchInvalidated = false
            if (initiator != null) {
                considerNotify(initiator)
                initiator = null
            } else {
                val iterator: Iterator<Map.Entry<EventObserver<T>, ObserverWrapper>> =
                    observers.iteratorWithAdditions()
                while (iterator.hasNext()) {
                    considerNotify(iterator.next().value)
                    if (dispatchInvalidated) {
                        break
                    }
                }
            }
        } while (dispatchInvalidated)
        dispatchingValue = false
    }

    /**
     * Adds the given observer to the observers list within the lifespan of the given
     * owner. The events are dispatched on the main thread. If LiveData already has data
     * set, it will be delivered to the observer.
     *
     *
     * The observer will only receive events if the owner is in [Lifecycle.State.STARTED]
     * or [Lifecycle.State.RESUMED] state (active).
     *
     *
     * If the owner moves to the [Lifecycle.State.DESTROYED] state, the observer will
     * automatically be removed.
     *
     *
     * When data changes while the `owner` is not active, it will not receive any updates.
     * If it becomes active again, it will receive the last available data automatically.
     *
     *
     * LiveData keeps a strong reference to the observer and the owner as long as the
     * given LifecycleOwner is not destroyed. When it is destroyed, LiveData removes references to
     * the observer &amp; the owner.
     *
     *
     * If the given owner is already in [Lifecycle.State.DESTROYED] state, LiveData
     * ignores the call.
     *
     *
     * If the given owner, observer tuple is already in the list, the call is ignored.
     * If the observer is already in the list with another owner, LiveData throws an
     * [IllegalArgumentException].
     *
     * @param owner         The LifecycleOwner which controls the observer
     * @param eventObserver The observer that will receive the events
     */
    @SuppressLint("RestrictedApi")
    @MainThread
    open fun observe(owner: LifecycleOwner, eventObserver: EventObserver<T>) {
        assertMainThread("observe")
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        val wrapper = LifecycleBoundObserver(owner, eventObserver)
        val existing: ObserverWrapper? = observers.putIfAbsent(eventObserver, wrapper)
        if (existing != null && !existing.isAttachedTo(owner)) {
            throw IllegalArgumentException(
                "Cannot add the same observer with different lifecycles"
            )
        }
        if (existing != null) {
            return
        }
        owner.lifecycle.addObserver(wrapper)
    }

    /**
     * Adds the given observer to the observers list. This call is similar to
     * [EventBaseLiveData.observe] with a LifecycleOwner, which
     * is always active. This means that the given observer will receive all events and will never
     * be automatically removed. You should manually call [removeObserver] to stop
     * observing this LiveData.
     * While LiveData has one of such observers, it will be considered
     * as active.
     *
     *
     * If the observer was already added with an owner to this LiveData, LiveData throws an
     * [IllegalArgumentException].
     *
     * @param eventObserver The observer that will receive the events
     */
    @SuppressLint("RestrictedApi")
    @MainThread
    open fun observeForever(eventObserver: EventObserver<T>) {
        assertMainThread("observeForever")
        val wrapper = AlwaysActiveObserver(eventObserver)
        val existing: ObserverWrapper? = observers.putIfAbsent(eventObserver, wrapper)
        if (existing is LifecycleBoundObserver) {
            throw IllegalArgumentException(
                "Cannot add the same observer with different lifecycles"
            )
        }
        if (existing != null) {
            return
        }
        wrapper.activeStateChanged(true)
    }

    /**
     * Removes the given observer from the observers list.
     *
     * @param eventObserver The Observer to receive events.
     */
    @SuppressLint("RestrictedApi")
    @MainThread
    open fun removeObserver(eventObserver: EventObserver<T>) {
        assertMainThread("removeObserver")
        observers.remove(eventObserver)?.apply {
            detachObserver()
            activeStateChanged(false)
        }
    }

    /**
     * Removes all observers that are tied to the given [LifecycleOwner].
     *
     * @param owner The `LifecycleOwner` scope for the observers to be removed.
     */
    @MainThread
    open fun removeObservers(owner: LifecycleOwner) {
        assertMainThread("removeObservers")
        for ((key, value) in observers) {
            if (value.isAttachedTo(owner)) {
                removeObserver(key)
            }
        }
    }

    /**
     * Posts a task to a main thread to set the given value. So if you have a following code
     * executed in the main thread:
     * <pre class="prettyprint">
     * liveData.postValue("a");
     * liveData.setValue("b");
    </pre> *
     * The value "b" would be set at first and later the main thread would override it with
     * the value "a".
     *
     *
     * If you called this method multiple times before a main thread executed a posted task, only
     * the last value would be dispatched.
     *
     * @param value The new value
     */
    @SuppressLint("RestrictedApi")
    protected open fun postValue(value: Event<T>) {
        var postTask: Boolean
        synchronized(dataLock) {
            postTask = pendingData === NOT_SET
            pendingData = value
        }
        if (!postTask) {
            return
        }
        ArchTaskExecutor.getInstance().postToMainThread(postValueRunnable)
    }

    /**
     * Sets the value. If there are active observers, the value will be dispatched to them.
     *
     *
     * This method must be called from the main thread. If you need set a value from a background
     * thread, you can use [.postValue]
     *
     * @param value The new value
     */
    @MainThread
    protected open fun setValue(value: Event<T>?) {
        assertMainThread("setValue")
        version++
        data = value
        dispatchingValue(null)
    }

    /**
     * Returns the current value.
     * Note that calling this method on a background thread does not guarantee that the latest
     * value set will be received.
     *
     * @return the current value
     */
    @Suppress("UNCHECKED_CAST")
    open fun getValue(): Event<T>? {
        return if (data !== NOT_SET) {
            data as? Event<T>
        } else null
    }

    open fun getVersion(): Int {
        return version
    }

    /**
     * Called when the number of active observers change to 1 from 0.
     *
     *
     * This callback can be used to know that this LiveData is being used thus should be kept
     * up to date.
     */
    protected open fun onActive() {}

    /**
     * Called when the number of active observers change from 1 to 0.
     *
     *
     * This does not mean that there are no observers left, there may still be observers but their
     * lifecycle states aren't [Lifecycle.State.STARTED] or [Lifecycle.State.RESUMED]
     * (like an Activity in the back stack).
     *
     *
     * You can check if there are observers via [.hasObservers].
     */
    protected open fun onInactive() {}

    /**
     * Returns true if this LiveData has observers.
     *
     * @return true if this LiveData has observers
     */
    @SuppressLint("RestrictedApi")
    open fun hasObservers(): Boolean {
        return observers.size() > 0
    }

    /**
     * Returns true if this LiveData has active observers.
     *
     * @return true if this LiveData has active observers
     */
    open fun hasActiveObservers(): Boolean {
        return activeCount > 0
    }

    internal inner class LifecycleBoundObserver(
        private val owner: LifecycleOwner,
        eventObserver: EventObserver<T>
    ) :
        ObserverWrapper(eventObserver), LifecycleEventObserver {
        override fun shouldBeActive(): Boolean {
            return owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        }

        override fun onStateChanged(
            source: LifecycleOwner,
            event: Lifecycle.Event
        ) {
            if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                removeObserver(eventObserver)
                return
            }
            activeStateChanged(shouldBeActive())
        }

        override fun isAttachedTo(owner: LifecycleOwner): Boolean {
            return this.owner === owner
        }

        override fun detachObserver() {
            owner.lifecycle.removeObserver(this)
        }
    }

    abstract inner class ObserverWrapper(val eventObserver: EventObserver<T>) {
        var active = false
        var lastVersion = START_VERSION

        abstract fun shouldBeActive(): Boolean

        open fun isAttachedTo(owner: LifecycleOwner) = false

        open fun detachObserver() {}

        fun activeStateChanged(newActive: Boolean) {
            if (newActive == active) {
                return
            }
            // immediately set active state, so we'd never dispatch anything to inactive
            // owner
            active = newActive
            val wasInactive = activeCount == 0
            activeCount += if (active) 1 else -1
            if (wasInactive && active) {
                onActive()
            }
            if (activeCount == 0 && !active) {
                onInactive()
            }
            if (active) {
                dispatchingValue(this)
            }
        }
    }

    private inner class AlwaysActiveObserver(eventObserver: EventObserver<T>) :
        ObserverWrapper(eventObserver) {
        override fun shouldBeActive(): Boolean {
            return true
        }
    }

    companion object {
        const val START_VERSION = -1
        val NOT_SET = Any()

        @SuppressLint("RestrictedApi")
        fun assertMainThread(methodName: String) {
            if (!ArchTaskExecutor.getInstance().isMainThread) {
                throw IllegalStateException(
                    "Cannot invoke $methodName on a background thread"
                )
            }
        }
    }
}