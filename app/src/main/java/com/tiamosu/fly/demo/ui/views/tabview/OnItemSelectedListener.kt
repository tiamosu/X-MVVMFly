package com.tiamosu.fly.demo.ui.views.tabview

/**
 * @author tiamosu
 * @date 2020/5/11.
 */
interface OnItemSelectedListener {
    fun onItemSelected(position: Int, prePosition: Int)

    fun onItemUnselected(position: Int)

    fun onItemReselected(position: Int)
}