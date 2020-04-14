package com.tiamosu.fly.demo.data.bean

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class Friend {
    val data: List<FriendBean>? = null

    inner class FriendBean {
        val id = 0
        val link: String? = null
        val name: String? = null
        val order = 0
        val visible = 0

        override fun toString(): String {
            return "FriendBean(id=$id, link=$link, name=$name, order=$order, visible=$visible)"
        }
    }

    override fun toString(): String {
        return "Friend(data=$data)"
    }
}