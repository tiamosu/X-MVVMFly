package com.tiamosu.fly.base

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Lifecycle
import com.tiamosu.fly.base.action.*
import com.tiamosu.fly.base.dialog.BaseFlyDialog
import com.tiamosu.fly.base.dialog.loading.FlyLoadingDialog
import com.tiamosu.fly.base.dialog.loading.Loader
import com.tiamosu.fly.base.dialog.loading.LoadingConfig
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

    //是否在页面可见时加载数据，防止多次加载数据
    private var isVisibleLoadData = false

    //loading弹框
    private var loadingDialog: BaseFlyDialog? = null

    //loading弹框数量
    private var loadingDialogTotal = 0

    final override val bundle
        get() = intent?.extras

    final override fun getContext() = this

    var rootView: View? = null

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

    override fun setContentView() {
        if (getLayoutId() > 0) {
            rootView = View.inflate(getContext(), getLayoutId(), null)
            setContentView(rootView)
            initSoftKeyboard()
        }
    }

    override val loadingConfig by lazy { LoadingConfig() }

    override val createLoadingDialog by lazy { FlyLoadingDialog(getContext()) }

    override fun showLoading(config: LoadingConfig?) {
        loadingDialogTotal++
        val newConfig = config ?: loadingConfig
        val delayMillis = newConfig.delayMillis
        postDelayed({
            if (loadingDialogTotal <= 0 || isFinishing || isDestroyed || Loader.isShowing()) {
                return@postDelayed
            }
            val dialog = newConfig.dialog ?: loadingDialog ?: createLoadingDialog.also {
                loadingDialog = it
            }
            Loader.showLoading(newConfig.isDelayedShow, delayMillis, dialog)
        }, delayMillis)
    }

    override fun hideLoading() {
        if (loadingDialogTotal > 0) {
            loadingDialogTotal--
        }
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