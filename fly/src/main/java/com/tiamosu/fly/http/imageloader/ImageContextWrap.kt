package com.tiamosu.fly.http.imageloader

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author tiamosu
 * @date 2021/10/28.
 */

val View.imgCtxWrap: ImageContextWrap
    get() = ImageContextWrap(this)

val Context.imgCtxWrap: ImageContextWrap
    get() = ImageContextWrap(this)

val Fragment.imgCtxWrap: ImageContextWrap
    get() = ImageContextWrap(this)

val Activity.imgCtxWrap: ImageContextWrap
    get() = ImageContextWrap(this)

val FragmentActivity.imgCtxWrap: ImageContextWrap
    get() = ImageContextWrap(this)

class ImageContextWrap {
    var view: View? = null
        private set
    var context: Context? = null
        private set
    var fragment: Fragment? = null
        private set
    var activity: Activity? = null
        private set
    var fragmentActivity: FragmentActivity? = null
        private set

    constructor(view: View) {
        this.view = view
    }

    constructor(context: Context) {
        this.context = context
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
    }

    constructor(activity: Activity) {
        this.activity = activity
    }

    constructor(fragmentActivity: FragmentActivity) {
        this.fragmentActivity = fragmentActivity
    }
}