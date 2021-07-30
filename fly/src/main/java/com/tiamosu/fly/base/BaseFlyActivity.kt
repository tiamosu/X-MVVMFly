package com.tiamosu.fly.base

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.tiamosu.databinding.page.FlyDataBindingActivity
import com.tiamosu.fly.base.action.*
import com.tiamosu.fly.base.dialog.loading.FlyLoadingDialog
import com.tiamosu.fly.base.dialog.loading.Loader
import com.tiamosu.fly.base.dialog.loading.LoadingConfig
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onCreate] → [initParameters] → [initView] → [initEvent]
 * → [createObserver] → [onStart] → [onResume] → [doBusiness] → [onPause] → [onStop]
 * → [onDestroy]
 *
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : FlyDataBindingActivity(),
    ActivityAction, BundleAction, HandlerAction, KeyboardAction, NetAction {

    private val networkDelegate by lazy { NetworkDelegate() }

    //是否在页面可见时加载数据，防止多次加载数据
    private var isVisibleLoadData = true

    final override val bundle
        get() = intent?.extras

    final override fun getContext() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!onCreateInit(savedInstanceState)) {
            return
        }
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)

        initParameters(bundle)
        initView(rootView)
        initEvent()
        createObserver()
    }

    /**
     * 页面可见时加载
     */
    private fun onLazyLoad() {
        if (!isVisibleLoadData) return
        isVisibleLoadData = false
        doBusiness()
    }

    override fun onResume() {
        super.onResume()
        onLazyLoad()
    }

    override fun onPause() {
        super.onPause()
        //隐藏软键盘，避免内存泄漏
        hideKeyboard(getContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallbacks()
        isVisibleLoadData = true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //设置为当前的 Intent，避免 Activity 被杀死后重启 Intent 还是最原先的那个
        setIntent(intent)
    }

    override fun showLoading(config: LoadingConfig?) {
        val newConfig = config ?: LoadingConfig()
        val dialog = newConfig.dialog ?: FlyLoadingDialog(getContext())
        Loader.showLoading(newConfig.delayMillis, dialog)
    }

    override fun hideLoading() {
        Loader.hideLoading()
    }

    /**
     * 点击空白区域隐藏软键盘
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN
            && isShouldHideKeyboard(currentFocus, ev)
        ) {
            initSoftKeyboard()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideKeyboard(view: View?, event: MotionEvent): Boolean {
        if (view is EditText) {
            val l = intArrayOf(0, 0)
            view.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + view.height
            val right = left + view.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    /**
     * 初始化软键盘
     */
    protected open fun initSoftKeyboard() {
        //点击外部隐藏软键盘，提升用户体验
        hideKeyboard(getContext())
    }
}