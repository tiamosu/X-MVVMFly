package com.tiamosu.fly.module.common.base

import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.NonNull
import com.blankj.utilcode.util.KeyboardUtils
import com.tiamosu.fly.module.common.R
import com.tiamosu.fly.utils.checkArgument
import com.tiamosu.fly.utils.newInstance
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
abstract class ProxyActivity : BaseActivity() {

    /**
     * @return 设置根Fragment
     */
    @NonNull
    protected abstract fun getRootFragment(): Class<out ISupportFragment>

    override fun getLayoutId(): Int {
        return 0
    }

    override fun setContentView() {
        if (getLayoutId() <= 0) {
            rootView = FrameLayout(this)
            rootView!!.id = R.id.delegate_container
            setContentView(rootView)
        } else {
            super.setContentView()
        }

        loadRootFragment(R.id.delegate_container)
    }

    protected open fun loadRootFragment(containerId: Int) {
        if (getLayoutId() > 0 && containerId == R.id.delegate_container) {
            checkArgument(false, "you should override loadRootFragment(containerId)!")
        }
        if (findFragment(getRootFragment()) == null) {
            loadRootFragment(containerId, newInstance(getRootFragment()))
        }
    }

    override fun doBusiness() {}

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (isShouldHideKeyboard(view, ev)) {
                KeyboardUtils.hideSoftInput(this)
                view?.clearFocus()
            }
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
}