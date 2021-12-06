package com.tiamosu.fly.http.subscriber

import com.tiamosu.fly.utils.getAppComponent
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.disposables.DisposableHelper
import io.reactivex.rxjava3.internal.util.EndConsumerHelper
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import java.util.concurrent.atomic.AtomicReference

/**
 * 参考[io.reactivex.rxjava3.observers.DisposableObserver]
 *
 * @author tiamosu
 * @date 2020/3/11.
 */
abstract class BaseSubscriber<T : Any> : Disposable,
    ErrorHandleSubscriber<T>(getAppComponent().rxErrorHandler()) {

    private val upstream = AtomicReference<Disposable>()

    override fun onSubscribe(d: Disposable) {
        if (EndConsumerHelper.setOnce(upstream, d, javaClass)) {
            onStart(d)
        }
    }

    /**
     * Called once the single upstream Disposable is set via onSubscribe.
     */
    protected abstract fun onStart(disposable: Disposable)

    override fun isDisposed(): Boolean {
        return upstream.get() === DisposableHelper.DISPOSED
    }

    override fun dispose() {
        DisposableHelper.dispose(upstream)
    }
}