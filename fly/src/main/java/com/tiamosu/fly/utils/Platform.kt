@file:JvmName("Platform")

package com.tiamosu.fly.utils

import com.blankj.utilcode.util.ThreadUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.schedulers.Schedulers

@Deprecated("Use launchMain(action)", ReplaceWith("launchMain(action)"))
fun postOnMain(action: Action) {
    launchMain(action)
}

@Deprecated("Use launch(scheduler, action)", ReplaceWith("launch(scheduler, action)"))
fun post(scheduler: Scheduler, action: Action) {
    launch(scheduler, action)
}

/**
 * 切换至主线程进行运行
 */
fun launchMain(action: Action) {
    if (ThreadUtils.isMainThread()) {
        action.run()
    } else {
        launch(AndroidSchedulers.mainThread(), action)
    }
}

/**
 * 切换至IO线程进行运行
 */
fun launchIO(action: Action) {
    launch(Schedulers.io(), action)
}

/**
 * 切换线程进行运行
 */
fun launch(scheduler: Scheduler, action: Action) {
    Completable.fromAction(action)
        .subscribeOn(scheduler)
        .subscribe()
}