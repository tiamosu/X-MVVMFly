package com.tiamosu.fly.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.widget.ContentFrameLayout
import com.tiamosu.fly.R
import com.tiamosu.fly.utils.FragmentUtils
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportActivity

/**
 * @author weixia
 * @date 2020/2/18.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseProxyActivity : SupportActivity(), IActivity {
    var mContentView: View? = null

    /**
     * @return 设置根Fragment
     */
    @NonNull
    protected abstract fun getRootFragment(): Class<out ISupportFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(intent.extras)
        setContentView()
        initView(savedInstanceState, null)
        initEvent()
        doBusiness()
    }

    @SuppressLint("RestrictedApi")
    private fun setContentView() {
        mContentView = ContentFrameLayout(this)
        mContentView!!.id = R.id.delegate_container
        if (getLayoutId() > 0) {
            View.inflate(this, getLayoutId(), (mContentView as ContentFrameLayout))
        }
        setContentView(mContentView)

        if (findFragment(getRootFragment()) == null) {
            loadRootFragment(R.id.delegate_container, FragmentUtils.newInstance(getRootFragment()))
        }
    }

    override fun getLayoutId(): Int {
        return 0
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(savedInstanceState: Bundle?, contentView: View?) {}
    override fun initEvent() {}
    override fun doBusiness() {}
}