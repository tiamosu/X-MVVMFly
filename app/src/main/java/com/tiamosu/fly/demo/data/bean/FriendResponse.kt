package com.tiamosu.fly.demo.data.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
@Keep
@Parcelize
data class FriendResponse(
    val data: @RawValue List<FriendBean>
) : Parcelable {
    override fun toString(): String {
        return "Friend(data=$data)"
    }
}

@Keep
@Parcelize
data class FriendBean(
    val id: Int = 0,
    val link: String? = null,
    val name: String? = null,
    val order: Int = 0,
    val visible: Int = 0,
) : Parcelable {
    override fun toString(): String {
        return "FriendBean(id=$id, link=$link, name=$name, order=$order, visible=$visible)"
    }
}