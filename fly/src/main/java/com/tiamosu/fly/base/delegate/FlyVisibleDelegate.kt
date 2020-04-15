package com.tiamosu.fly.base.delegate

import androidx.fragment.app.Fragment
import com.tiamosu.fly.base.FlySupportFragment

/**
 * @author tiamosu
 * @date 2020/4/14.
 */
class FlyVisibleDelegate(private val fragment: FlySupportFragment) {
    private var isViewCreated = false           //布局是否创建完成
    private var currentVisibleState = false     //当前可见状态
    private var isFirstVisible = true           //是否第一次可见

    fun onCreateView() {
        isViewCreated = true
    }

    fun onViewCreated() {
        if (isFragmentVisible(fragment)) {
            // 可见状态,进行事件分发
            dispatchUserVisibleHint(true)
        }
    }

    /**
     * 修改 Fragment 的可见性 setUserVisibleHint 被调用有两种情况：
     * 1）在切换 tab 的时候，会先于所有 fragment 的其他生命周期，先调用这个函数
     * 2) 对于之前已经调用过 setUserVisibleHint 方法的 fragment 后，让 fragment 从可见到不可见之间状态的变化
     */
    fun setUserVisibleHint(isVisibleToUser: Boolean) {
        // 对于情况 1）不予处理，用 isViewCreated 进行判断，如果 isViewCreated = false，说明它没有被创建
        if (isViewCreated) {
            // 对于情况 2），需要分情况考虑，如果是不可见 -> 可见 2.1
            // 如果是可见 -> 不可见 2.2
            // 对于 2.1）我们需要如何判断呢？首先必须是可见的（isVisibleToUser 为 true）
            // 而且只有当可见状态进行改变的时候才需要切换，否则会出现反复调用的情况
            // 从而导致事件分发带来的多次更新
            if (isVisibleToUser && !currentVisibleState) {
                // 从不可见 -> 可见
                dispatchUserVisibleHint(true)
            } else if (!isVisibleToUser && currentVisibleState) {
                dispatchUserVisibleHint(false)
            }
        }
    }

    /**
     * 用 FragmentTransaction 来控制 fragment 的 hide 和 show 时，
     * 那么这个方法就会被调用。每当你对某个 Fragment 使用 hide 或者是 show 的时候，那么这个 Fragment 就会自动调用这个方法。
     */
    fun onHiddenChanged(hidden: Boolean) {
        // 这里的可见返回为false
        if (hidden) {
            dispatchUserVisibleHint(false)
        } else {
            dispatchUserVisibleHint(true)
        }
    }

    /**
     * 在滑动或者跳转的过程中，第一次创建 fragment 的时候均会调用 onResume 方法
     */
    fun onResume() {
        // 如果不是第一次可见
        if (!isFirstVisible) {
            // 如果此时进行 Activity 跳转,会将所有的缓存的 fragment 进行 onResume 生命周期的重复
            // 只需要对可见的 fragment 进行加载,
            if (isFragmentVisible(fragment) && !currentVisibleState) {
                dispatchUserVisibleHint(true)
            }
        }
    }

    /**
     * 只有当当前页面由可见状态转变到不可见状态时才需要调用 dispatchUserVisibleHint currentVisibleState &&
     * getUserVisibleHint() 能够限定是当前可见的 Fragment 当前 Fragment 包含子 Fragment 的时候
     * dispatchUserVisibleHint 内部本身就会通知子 Fragment 不可见 子 fragment 走到这里的时候自身又会调用一遍
     */
    @Suppress("DEPRECATION")
    fun onPause() {
        if (currentVisibleState && fragment.userVisibleHint) {
            dispatchUserVisibleHint(false)
        }
    }

    fun onDestroyView() {
        isViewCreated = false
    }

    fun isSupportVisible(): Boolean {
        return currentVisibleState
    }

    /**
     * 统一处理用户可见事件分发
     */
    private fun dispatchUserVisibleHint(isVisible: Boolean) {
        // 首先考虑一下 fragment 嵌套 fragment 的情况(只考虑2层嵌套)
        if (isVisible && isParentInvisible) {
            // 父 Fragment 此时不可见,直接 return 不做处理
            return
        }
        // 为了代码严谨,如果当前状态与需要设置的状态本来就一致了,就不处理了
        if (currentVisibleState == isVisible) {
            return
        }
        currentVisibleState = isVisible
        if (isVisible) {
            if (isFirstVisible) {
                isFirstVisible = false
                // 第一次可见，进行全局初始化
                fragment.onFlyLazyInitView()
            }
            fragment.onFlySupportVisible()
            // 分发事件给内嵌的 Fragment
            dispatchChildVisibleState(true)
        } else {
            fragment.onFlySupportInvisible()
            dispatchChildVisibleState(false)
        }
    }

    private val isParentInvisible: Boolean
        get() {
            val parentFragment = fragment.parentFragment
            if (parentFragment is FlySupportFragment) {
                return !parentFragment.isFlySupportVisible()
            }
            return false
        }

    /**
     * 在双重 ViewPager 嵌套的情况下，第一次滑到 Fragment 嵌套 ViewPager(fragment)的场景的时候
     * 此时只会加载外层 Fragment 的数据，而不会加载内嵌 viewPager 中的 fragment 的数据，因此，我们
     * 需要在此增加一个当外层 Fragment 可见的时候，分发可见事件给自己内嵌的所有 Fragment 显示
     */
    private fun dispatchChildVisibleState(visible: Boolean) {
        val fragmentManager =
            fragment.childFragmentManager
        val fragments =
            fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is FlySupportFragment && isFragmentVisible(fragment)) {
                fragment.visibleDelegate.dispatchUserVisibleHint(visible)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isFragmentVisible(fragment: Fragment): Boolean {
        return !fragment.isHidden && fragment.userVisibleHint
    }
}