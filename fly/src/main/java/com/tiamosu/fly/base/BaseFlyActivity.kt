package com.tiamosu.fly.base

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Lifecycle
import com.tiamosu.fly.base.action.*
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.fragmentation.FlySupportActivity
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onCreate] → [initParameters] → [initView] → [initEvent]
 * → [createObserver] → [doBusiness] → [onStart] → [onResume] → [onPause]
 * → [onStop] → [onDestroy]
 *
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : FlySupportActivity(),
    ActivityAction, BundleAction, HandlerAction, KeyboardAction, NetAction {

    private val networkDelegate by lazy { NetworkDelegate() }
    var rootView: View? = null

    //是否在页面可见时加载数据，防止多次加载数据
    private var isVisibleLoadData = false

    final override val bundle
        get() = intent?.extras

    final override fun getContext() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!onCreateInit(savedInstanceState)) {
            return
        }
        initParameters(bundle)
        setContentView()
        initView(rootView)
        createObserver()
        initEvent()
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
        tryLoadData(true)
    }

    override fun setContentView() {
        if (getLayoutId() > 0) {
            rootView = View.inflate(getContext(), getLayoutId(), null)
            setContentView(rootView)
            initSoftKeyboard()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
        tryLoadData(false)
    }

    override fun onPause() {
        super.onPause()
        //隐藏软键盘，避免内存泄漏
        hideKeyboard(getContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        isVisibleLoadData = false
        removeCallbacks()
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

    private fun tryLoadData(isCreate: Boolean) {
        if (isCreate) {
            doBusiness()
        } else if (isNeedReload()) {
            if (isVisibleLoadData) {
                doBusiness()
            }
            isVisibleLoadData = true
        }
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