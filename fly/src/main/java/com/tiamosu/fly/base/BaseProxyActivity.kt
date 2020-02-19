package com.tiamosu.fly.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.widget.ContentFrameLayout
import com.tiamosu.fly.R
import com.tiamosu.fly.utils.FragmentUtils
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseProxyActivity : BaseFlyActivity() {

    /**
     * @return 设置根Fragment
     */
    @NonNull
    protected abstract fun getRootFragment(): Class<out ISupportFragment>

    @SuppressLint("RestrictedApi")
    override fun setContentView() {
        rootView = ContentFrameLayout(this)
        rootView!!.id = R.id.delegate_container
        if (getLayoutId() > 0) {
            View.inflate(this, getLayoutId(), (rootView as ContentFrameLayout))
        }
        setContentView(rootView)

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