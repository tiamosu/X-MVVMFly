package com.tiamosu.fly.demo.ui.views.tabview

/**
 * @author tiamosu
 * @date 2020/5/11.
 */
class OnItemSelectedListener {
    internal var onItemSelectBefore: ((position: Int) -> Boolean)? = null
    internal var onItemSelected: ((position: Int, prePosition: Int) -> Unit)? = null
    internal var onItemUnselected: ((position: Int) -> Unit)? = null
    internal var onItemReselected: ((position: Int) -> Unit)? = null

    fun onItemSelectBefore(onItemSelectBefore: (position: Int) -> Boolean) {
        this.onItemSelectBefore = onItemSelectBefore
    }

    fun onItemSelected(onItemSelected: (position: Int, prePosition: Int) -> Unit) {
        this.onItemSelected = onItemSelected
    }

    fun onItemUnselected(onItemUnselected: (prePosition: Int) -> Unit) {
        this.onItemUnselected = onItemUnselected
    }

    fun onItemReselected(onItemReselected: (position: Int) -> Unit) {
        this.onItemReselected = onItemReselected
    }
}