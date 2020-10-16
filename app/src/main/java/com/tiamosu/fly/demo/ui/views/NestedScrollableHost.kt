package com.tiamosu.fly.demo.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewParent
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.sign

/**
 * 描述：此类用于解决 ViewPager2  嵌套 ViewPager2 或者 RecyclerView 等相互嵌套的冲突问题
 *
 * @author tiamosu
 * @date 2020/10/16.
 */
class NestedScrollableHost : FrameLayout {
    private var touchSlop = 0
    private var initialX = 0f
    private var initialY = 0f
    private var findViewCacheMap: HashMap<Int, View>? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val configuration = ViewConfiguration.get(context)
        this.touchSlop = configuration.scaledTouchSlop
    }

    private fun getParentViewPager(): ViewPager2? {
        var viewParent: ViewParent? = this.parent
        if (viewParent !is View) {
            viewParent = null
        }
        var v = viewParent as? View
        while (v != null && v !is ViewPager2) {
            viewParent = v.parent
            if (viewParent !is View) {
                viewParent = null
            }
            v = viewParent as? View
        }
        var v2 = v
        if (v !is ViewPager2) {
            v2 = null
        }
        return v2 as? ViewPager2
    }

    private fun getChild(): View? {
        return if (this.childCount > 0) getChildAt(0) else null
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -sign(delta).toInt()
        val view: View?
        var isCanChildScroll = false
        when (orientation) {
            0 -> {
                view = getChild()
                isCanChildScroll =
                    view?.canScrollHorizontally(direction) ?: false
            }
            1 -> {
                view = getChild()
                isCanChildScroll = view?.canScrollVertically(direction) ?: false
            }
        }
        return isCanChildScroll
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        val viewPager2 = getParentViewPager()
        if (viewPager2 != null) {
            val orientation = viewPager2.orientation
            if (canChildScroll(orientation, -1.0f) || canChildScroll(orientation, 1.0f)) {
                if (e.action == 0) {
                    initialX = e.x
                    initialY = e.y
                    this.parent.requestDisallowInterceptTouchEvent(true)
                } else if (e.action == 2) {
                    val dx = e.x - initialX
                    val dy = e.y - initialY
                    val isVpHorizontal = orientation == 0
                    val scaledDx = abs(dx) * if (isVpHorizontal) 0.5f else 1.0f
                    val scaledDy = abs(dy) * if (isVpHorizontal) 1.0f else 0.5f
                    if (scaledDx > touchSlop.toFloat() || scaledDy > touchSlop.toFloat()) {
                        when {
                            isVpHorizontal == scaledDy > scaledDx -> {
                                this.parent.requestDisallowInterceptTouchEvent(false)
                            }
                            canChildScroll(orientation, if (isVpHorizontal) dx else dy) -> {
                                this.parent.requestDisallowInterceptTouchEvent(true)
                            }
                            else -> {
                                this.parent.requestDisallowInterceptTouchEvent(false)
                            }
                        }
                    }
                }
            }
        }
    }

    fun findCachedViewById(var1: Int): View? {
        if (findViewCacheMap == null) {
            findViewCacheMap = HashMap()
        }
        var var2: View? = findViewCacheMap?.get(var1)
        if (var2 == null) {
            var2 = findViewById(var1)
            findViewCacheMap?.put(var1, var2)
        }
        return var2
    }

    fun clearFindViewByIdCache() {
        findViewCacheMap?.clear()
    }
}