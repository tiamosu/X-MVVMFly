package com.tiamosu.fly.demo.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * @author tiamosu
 * @date 2020/7/18.
 */
class CustomFragmentStateAdapter(
    fragment: Fragment,
    private val viewPager2: ViewPager2,
    private var fragments: List<Fragment>
) : FragmentStateAdapter(fragment) {
    private val createdIds = arrayListOf<Long>()
    private val ids = arrayListOf<Long>()

    init {
        viewPager2.adapter = this
        updateDataSetChanged(fragments)
    }

    fun updateDataSetChanged(fragments: List<Fragment>) {
        this.fragments = fragments
        if (fragments.isNotEmpty()) {
            viewPager2.offscreenPageLimit = fragments.size
        }

        if (ids.isNotEmpty()) {
            ids.clear()
        }
        fragments.forEach {
            ids.add(it.hashCode().toLong())
        }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        val id = ids[position]
        if (!createdIds.contains(id)) {
            createdIds.add(id)
        }
        return fragments[position]
    }

    override fun getItemId(position: Int): Long {
        return ids[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return createdIds.contains(itemId)
    }
}