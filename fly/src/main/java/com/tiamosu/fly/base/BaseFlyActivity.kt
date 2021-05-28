package com.tiamosu.fly.base

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Lifecycle
import com.tiamosu.databinding.page.FlyDataBindingActivity
import com.tiamosu.fly.base.action.*
import com.tiamosu.fly.base.dialog.loading.FlyLoadingDialog
import com.tiamosu.fly.base.dialog.loading.Loader
import com.tiamosu.fly.base.dialog.loading.LoadingConfig
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onCreate] → [initParameters] → [initView] → [createObserver]
 * → [onStart] → [onResume] → [initEvent] → [doBusiness] → [onPause] → [onStop] → [onDestroy]
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

        initSoftKeyboard()
        initParameters(bundle)
        initView(rootView)
        createObserver()
    }

    /**
     * 页面可见时加载
     */
    private fun onLazyLoad() {
        if (!isVisibleLoadData) return
        isVisibleLoadData = false
        initEvent()
        doBusiness()
    }

    override fun onResume() {
        super.onResume()
        onLazyLoad()
        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
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

    override fun finish() {
        //隐藏软键盘，避免内存泄漏
        hideKeyboard(getContext())
        super.finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //设置为当前的 Intent，避免 Activity 被杀死后重启 Intent 还是最原先的那个
        setIntent(intent)
    }

    override val loadingConfig by lazy { LoadingConfig() }

    override fun showLoading(config: LoadingConfig?) {
        val newConfig = config ?: loadingConfig
        val dialog = newConfig.dialog ?: FlyLoadingDialog(getContext())
        Loader.showLoading(newConfig.delayMillis, dialog)
    }

    override fun hideLoading() {
        Loader.hideLoading()
    }

    /**
     * 初始化软键盘
     */
    protected open fun initSoftKeyboard() {
        //点击外部隐藏软键盘，提升用户体验
        getContentView()?.clickNoRepeat {
            //隐藏软键盘，避免内存泄漏
            hideKeyboard(getContext())
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
            //这个 Fragment 必须是 BaseFlyFragment 的子类，并且处于可见状态
            if (fragment !is BaseFlyFragment ||
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
}