package com.tiamosu.fly.base.action

import android.app.Activity
import android.view.View
import android.view.Window
import com.blankj.utilcode.util.KeyboardUtils

/**
 * @author tiamosu
 * @date 2021/2/24.
 */
internal interface KeyboardAction {

    /**
     * 显示软键盘
     */
    fun showKeyboard() {
        KeyboardUtils.showSoftInput()
    }

    /**
     * 显示软键盘
     */
    fun showKeyboard(activity: Activity) {
        KeyboardUtils.showSoftInput(activity)
    }

    /**
     * 显示软键盘
     */
    fun showKeyboard(view: View) {
        KeyboardUtils.showSoftInput(view)
    }

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(view: View) {
        KeyboardUtils.hideSoftInput(view)
    }

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(activity: Activity) {
        KeyboardUtils.hideSoftInput(activity)
    }

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(window: Window) {
        KeyboardUtils.hideSoftInput(window)
    }

    /**
     * 切换软键盘
     */
    fun toggleSoftInput() {
        KeyboardUtils.toggleSoftInput()
    }
}