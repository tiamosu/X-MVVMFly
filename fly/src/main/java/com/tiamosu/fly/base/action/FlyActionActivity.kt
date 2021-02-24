package com.tiamosu.fly.base.action

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Lifecycle
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.fragmentation.FlySupportActivity
import java.util.*
import kotlin.math.pow

/**
 * @author tiamosu
 * @date 2021/2/24.
 */
open class FlyActionActivity : FlySupportActivity(),
    ActivityAction, BundleAction, HandlerAction, KeyboardAction {

    /** Activity 回调集合  */
    private var activityCallbacks: SparseArray<OnActivityCallback>? = null

    final override val bundle
        get() = intent?.extras

    final override fun getContext() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSoftKeyboard()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //设置为当前的 Intent，避免 Activity 被杀死后重启 Intent 还是最原先的那个
        setIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallbacks()
    }

    override fun finish() {
        //隐藏软键盘，避免内存泄漏
        currentFocus?.let { it -> hideKeyboard(it) }
        super.finish()
    }

    /**
     * 初始化软键盘
     */
    protected open fun initSoftKeyboard() {
        //点击外部隐藏软键盘，提升用户体验
        getContentView()?.clickNoRepeat {
            //隐藏软键盘，避免内存泄漏
            currentFocus?.let { it1 -> hideKeyboard(it1) }
        }
    }

    /**
     * 和 setContentView 对应的方法
     */
    fun getContentView(): ViewGroup? {
        return findViewById(Window.ID_ANDROID_CONTENT)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            //这个 Fragment 必须是 FlyActionFragment 的子类，并且处于可见状态
            if (fragment !is FlyActionFragment ||
                fragment.lifecycle.currentState != Lifecycle.State.RESUMED
            ) {
                continue
            }
            //将按键事件派发给 Fragment 进行处理
            if (fragment.dispatchKeyEvent(event)) {
                //如果 Fragment 拦截了这个事件，那么就不交给 Activity 处理
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    /**
     * startActivityForResult 方法优化
     */
    fun startActivityForResult(clazz: Class<out Activity>, callback: OnActivityCallback) {
        startActivityForResult(Intent(this, clazz), null, callback)
    }

    fun startActivityForResult(intent: Intent, callback: OnActivityCallback) {
        startActivityForResult(intent, null, callback)
    }

    @Suppress("DEPRECATION")
    fun startActivityForResult(
        intent: Intent,
        options: Bundle?,
        callback: OnActivityCallback
    ) {
        if (activityCallbacks == null) {
            activityCallbacks = SparseArray(1)
        }
        // 请求码必须在 2 的 16 次方以内
        val requestCode = Random().nextInt(2.0.pow(16.0).toInt())
        activityCallbacks?.put(requestCode, callback)
        startActivityForResult(intent, requestCode, options)
    }

    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        //隐藏软键盘，避免内存泄漏
        currentFocus?.let { it -> hideKeyboard(it) }
        // 查看源码得知 startActivity 最终也会调用 startActivityForResult
        super.startActivityForResult(intent, requestCode, options)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var callback: OnActivityCallback?
        if (activityCallbacks?.get(requestCode).also { callback = it } != null) {
            callback?.onActivityResult(resultCode, data)
            activityCallbacks?.remove(requestCode)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    interface OnActivityCallback {

        /**
         * 结果回调
         *
         * @param resultCode        结果码
         * @param data              数据
         */
        fun onActivityResult(resultCode: Int, data: Intent?)
    }
}