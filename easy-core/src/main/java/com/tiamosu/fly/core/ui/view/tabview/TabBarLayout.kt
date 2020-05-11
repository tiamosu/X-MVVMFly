package com.tiamosu.fly.core.ui.view.tabview

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewGroup
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
    private val tabParams: LayoutParams
    private var smoothScroll = false
    private var isItemClick = false

    private var currentItemPosition = -1
    private val barItems = ArrayList<TabBarItem>()
    private var onItemSelectedListener: OnItemSelectedListener? = null

    init {
        orientation = HORIZONTAL
        tabParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        tabParams.weight = 1f
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val childCount = childCount
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView is TabBarItem) {
                addItem(childView, i)
            }
        }
    }

    private fun addItem(barItem: TabBarItem, tabPosition: Int) {
        barItem.setOnClickListener {
            isItemClick = true
            barItemClick(barItem, viewPager2)
            isItemClick = false
        }
        barItem.tabPosition = tabPosition
        barItem.layoutParams = tabParams
        barItems.add(barItem)
    }

    private fun barItemClick(barItem: TabBarItem?, viewPager2: ViewPager2? = null) {
        if (barItem !is TabBarItem) {
            return
        }
        if (currentItemPosition == -1) {
            currentItemPosition = 0
            return
        }
        val pos = barItem.tabPosition
        if (currentItemPosition == pos) {
            onItemSelectedListener?.onItemReselected(pos)
        } else {
            onItemSelectedListener?.onItemSelected(pos, currentItemPosition)
            barItem.isSelected = true
            onItemSelectedListener?.onItemUnselected(currentItemPosition)
            barItems[currentItemPosition].isSelected = false
            viewPager2?.setCurrentItem(pos, smoothScroll)
            currentItemPosition = pos
        }
    }

    fun setViewPager2(viewPager2: ViewPager2) {
        this.viewPager2 = viewPager2
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (!isItemClick) {
                    barItemClick(getChildAt(position) as? TabBarItem)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    fun setSmoothScroll(mSmoothScroll: Boolean) {
        this.smoothScroll = mSmoothScroll
    }

    fun setCurrentItem(position: Int) {
        if (position < 0 || barItems.size < position) {
            return
        }
        viewPager2?.setCurrentItem(position, smoothScroll)
        post { getChildAt(position).performClick() }
    }

    fun setItemSelected(index: Int) {
        if (index >= 0 && index < barItems.size) {
            barItems[index].isSelected = true
        }
    }

    fun getCurrentItemPosition(): Int {
        return if (currentItemPosition == -1) 0 else currentItemPosition
    }

    fun getBottomItem(index: Int): TabBarItem? {
        return if (barItems.size < index) null else barItems[index]
    }

    fun setOnItemSelectedListener(
            onItemSelected: (position: Int, prePosition: Int) -> Unit,
            onItemUnselected: (position: Int) -> Unit = {},
            onItemReselected: (position: Int) -> Unit = {}
    ) {
        this.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(position: Int, prePosition: Int) {
                onItemSelected(position, prePosition)
            }

            override fun onItemUnselected(position: Int) {
                onItemUnselected(position)
            }

            override fun onItemReselected(position: Int) {
                onItemReselected(position)
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
            putInt(INSTANCE_POSITION, currentItemPosition)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            currentItemPosition = state.getInt(INSTANCE_POSITION)
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    companion object {
        private const val INSTANCE_STATE = "TabBarLayout_instance_state"
        private const val INSTANCE_POSITION = "TabBarLayout_instance_position"
    }
}
