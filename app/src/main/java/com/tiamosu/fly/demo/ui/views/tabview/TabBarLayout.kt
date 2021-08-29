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
    private var isFirstPageSelected = true

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

    private fun tabBarItemClick(
        barItem: TabBarItem,
        smoothScroll: Boolean,
        isViewPagerSelect: Boolean = false,
        isInitListener: Boolean = false,
    ) {
        curItemPos = barItem.tabPosition

        if (!isInitListener && (preItemPos == curItemPos)) {
            onItemSelectedListener?.onItemReselected?.invoke(curItemPos)
            return
        }
        val isItemSelected = onItemSelectedListener?.onItemSelectBefore?.invoke(curItemPos) ?: true
        if (isItemSelected) {
            barItem.isSelected = true

            if (!isInitListener) {
                tabBarItems.getOrNull(preItemPos)?.also { it.isSelected = false }

                if (!isViewPagerSelect) {
                    viewPager2?.setCurrentItem(curItemPos, smoothScroll)
                }
            }

            onItemSelectedListener?.onItemSelected?.invoke(curItemPos, preItemPos)
            onItemSelectedListener?.onItemUnselected?.invoke(preItemPos)
            preItemPos = curItemPos
        }
    }

    fun setViewPager2(viewPager2: ViewPager2) {
        this.viewPager2 = viewPager2
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (isFirstPageSelected) {
                    isFirstPageSelected = false
                    return
                }
                if (position != getItemPosition()) {
                    changeCurrentItem(position, isViewPagerSelect = true)
                }
            }
        })

        //若tbi_selected属性设定或者执行TabBarItem的setSelected，当前下标不为0时触发
        if (getItemPosition() != 0) {
            viewPager2.setCurrentItem(getItemPosition(), false)
        }
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
        changeCurrentItem(position, smoothScroll)
    }

    private fun changeCurrentItem(
        position: Int,
        smoothScroll: Boolean = this.smoothScroll,
        isViewPagerSelect: Boolean = false,
        isInitListener: Boolean = false,
    ) {
        (getChildAt(position) as? TabBarItem)?.let {
            tabBarItemClick(it, smoothScroll, isViewPagerSelect, isInitListener)
        }
    }

    fun setOnItemSelectedListener(
        listener: OnItemSelectedListener.() -> Unit = {}
    ) {
        onItemSelectedListener = OnItemSelectedListener().apply(listener)
        changeCurrentItem(getItemPosition(), isInitListener = true)
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
