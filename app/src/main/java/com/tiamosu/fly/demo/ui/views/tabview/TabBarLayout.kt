package com.tiamosu.fly.demo.ui.views.tabview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.viewpager2.widget.ViewPager2
import java.util.*

/**
 * @author tiamosu
 * @date 2020/5/11.
 */
@Suppress("unused")
class TabBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private var viewPager2: ViewPager2? = null
    private var smoothScroll = false

    private var preItemPos = -1
    private var curItemPos = -1
    private val tabBarItems = ArrayList<TabBarItem>()
    private var onItemSelectedListener: OnItemSelectedListener? = null

    private val tabBarParams by lazy {
        LayoutParams(-1, -1).apply { weight = 1f }
    }

    init {
        orientation = HORIZONTAL
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0 until childCount) {
            (getChildAt(i) as? TabBarItem)?.let { addTabBarItem(it, i) }
        }
    }

    private fun addTabBarItem(tabBarItem: TabBarItem, tabBarIndex: Int) {
        if (tabBarItem.isSelected) {
            curItemPos = tabBarIndex

            if (preItemPos == -1) {
                preItemPos = tabBarIndex
            }
        }
        tabBarItem.tabPosition = tabBarIndex
        tabBarItem.layoutParams = tabBarParams
        tabBarItems.add(tabBarItem)

        tabBarItem.setOnClickListener {
            tabBarItemClick(tabBarItem, smoothScroll)
        }
    }

    private fun tabBarItemClick(barItem: TabBarItem, smoothScroll: Boolean) {
        curItemPos = barItem.tabPosition

        if (preItemPos == curItemPos) {
            onItemSelectedListener?.onItemReselected?.invoke(curItemPos)
            return
        }
        val isItemSelected = onItemSelectedListener?.onItemSelectBefore?.invoke(curItemPos) ?: true
        if (isItemSelected) {
            barItem.isSelected = true
            tabBarItems.getOrNull(preItemPos)?.also { it.isSelected = false }
            viewPager2?.setCurrentItem(curItemPos, smoothScroll)

            onItemSelectedListener?.onItemSelected?.invoke(curItemPos, preItemPos)
            onItemSelectedListener?.onItemUnselected?.invoke(preItemPos)
            preItemPos = curItemPos
        }
    }

    fun setViewPager2(viewPager2: ViewPager2) {
        this.viewPager2 = viewPager2
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == curItemPos) return
                setCurrentItem(position, smoothScroll)
            }
        })
    }

    fun setTabBarItem(views: ArrayList<TabBarItem>) {
        views.forEachIndexed { index, tabBarItem ->
            addTabBarItem(tabBarItem, index)
        }
    }

    fun setSmoothScroll(smoothScroll: Boolean) {
        this.smoothScroll = smoothScroll
    }

    fun setCurrentItem(position: Int, smoothScroll: Boolean = this.smoothScroll) {
        (getChildAt(position) as? TabBarItem)?.let {
            tabBarItemClick(it, smoothScroll)
        }
    }

    fun setOnItemSelectedListener(
        onItemSelectedListener: OnItemSelectedListener.() -> Unit = {}
    ) {
        this.onItemSelectedListener = OnItemSelectedListener().apply(onItemSelectedListener)
    }

    fun getItemPosition(): Int {
        if (curItemPos == -1) {
            curItemPos = preItemPos
        }
        return if (curItemPos == -1) 0 else curItemPos
    }

    fun getTabBarItem(index: Int): TabBarItem? {
        return tabBarItems.getOrNull(index)
    }
}
