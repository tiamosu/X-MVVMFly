package com.tiamosu.fly.imageloader.glide

import android.content.Context
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.http.imageloader.ImageContextWrap

/**
 * @author tiamosu
 * @date 2021/10/28.
 */

/**
 * 获取Glide所需的上下文[Context]
 */
val ImageContextWrap.getGlideContext: Context?
    get() = when {
        context != null -> context
        activity != null -> activity
        view != null -> view?.context
        fragment != null -> fragment?.context
        fragmentActivity != null -> fragmentActivity
        else -> Utils.getApp()
    }

/**
 * 获取[GlideRequests]
 */
val ImageContextWrap.getGlideRequests: GlideRequests?
    get() = when {
        context != null -> context?.let { GlideFly.with(it) }
        activity != null -> activity?.let { GlideFly.with(it) }
        view != null -> view?.let { GlideFly.with(it) }
        fragment != null -> fragment?.let { GlideFly.with(it) }
        fragmentActivity != null -> fragmentActivity?.let { GlideFly.with(it) }
        else -> Utils.getApp()?.let { GlideFly.with(it) }
    }