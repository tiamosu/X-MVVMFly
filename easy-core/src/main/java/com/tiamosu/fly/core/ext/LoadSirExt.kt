package com.tiamosu.fly.core.ext

import android.view.View
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.ui.loadsir.CustomCallback
import com.tiamosu.fly.core.ui.loadsir.EmptyCallback
import com.tiamosu.fly.core.ui.loadsir.ErrorCallback
import com.tiamosu.fly.core.ui.loadsir.LoadingCallback

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
fun BaseFragment.loadServiceInit(
    view: View,
    onRetry: () -> Unit
): LoadService<*> {
    return LoadSir.Builder()
        .addCallback(EmptyCallback())
        .addCallback(ErrorCallback())
        .addCallback(LoadingCallback())
        .addCallback(CustomCallback())
        .setDefaultCallback(SuccessCallback::class.java)
        .build()
        .register<Any>(view, {
            //点击触发重试操作
            onRetry.invoke()
        }).also {
            this.loadService = it
        }
}

inline fun <reified T : Callback> LoadService<*>.showCallback() {
    return showCallback(T::class.java)
}