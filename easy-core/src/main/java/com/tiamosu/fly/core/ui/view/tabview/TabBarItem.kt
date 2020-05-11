package com.tiamosu.fly.core.ui.view.tabview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.AdaptScreenUtils
import com.tiamosu.fly.core.R

/**
 * @author tiamosu
 * @date 2020/5/11.
 */
class TabBarItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 普通状态时的图标
     */
    private var iconNormalResId = -1

    /**
     * 选中状态时的图标
     */
    private var iconSelectedResId = -1

    /**
     * 普通状态时的图标颜色
     */
    private var iconNormalColor = -1

    /**
     * 选中状态时的图标颜色
     */
    private var iconSelectedColor = -1

    /**
     * 图标控件宽度
     */
    private var iconWidth = 12

    /**
     * 图标控件高度
     */
    private var iconHeight = 12

    /**
     * 文本
     */
    private var text: String? = null

    /**
     * 文本字体大小
     */
    private var textSize = 12f

    /**
     * 普通状态时的文本颜色
     */
    private var textNormalColor = -1

    /**
     * 选中状态时的文本颜色
     */
    private var textSelectedColor = -1

    /**
     * 图标与文本间的距离
     */
    private var drawablePadding = 0

    /**
     * 是否选中状态
     */
    private var isItemSelected = false

    /**
     * 当前Tab选中的下标
     */
    var tabPosition = -1

    private var imgIcon: AppCompatImageView? = null
    private var textTv: AppCompatTextView? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TabBarItem)
        if (ta.hasValue(R.styleable.TabBarItem_tbi_iconNormal)) {
            iconNormalResId = ta.getResourceId(R.styleable.TabBarItem_tbi_iconNormal, -1)
            iconNormalColor = ta.getColor(R.styleable.TabBarItem_tbi_iconNormalColor, Color.GRAY)
        }
        if (ta.hasValue(R.styleable.TabBarItem_tbi_iconSelected)) {
            iconSelectedResId = ta.getResourceId(R.styleable.TabBarItem_tbi_iconSelected, -1)
            iconSelectedColor =
                ta.getColor(R.styleable.TabBarItem_tbi_iconSelectedColor, Color.WHITE)
        }
        iconWidth = ta.getDimensionPixelSize(
            R.styleable.TabBarItem_tbi_iconWidth,
            AdaptScreenUtils.pt2Px(12f)
        )
        iconHeight = ta.getDimensionPixelSize(
            R.styleable.TabBarItem_tbi_iconHeight,
            AdaptScreenUtils.pt2Px(12f)
        )

        if (ta.hasValue(R.styleable.TabBarItem_tbi_text)) {
            text = ta.getString(R.styleable.TabBarItem_tbi_text)
            textSize = ta.getDimensionPixelSize(
                R.styleable.TabBarItem_tbi_textSize,
                AdaptScreenUtils.pt2Px(12f)
            ).toFloat()
            textNormalColor = ta.getColor(R.styleable.TabBarItem_tbi_textNormalColor, Color.GRAY)
            textSelectedColor =
                ta.getColor(R.styleable.TabBarItem_tbi_textSelectedColor, Color.WHITE)
        }

        if (ta.hasValue(R.styleable.TabBarItem_tbi_text)
            && ta.hasValue(R.styleable.TabBarItem_tbi_iconNormal)
        ) {
            drawablePadding = ta.getDimensionPixelSize(
                R.styleable.TabBarItem_tbi_drawablePadding,
                AdaptScreenUtils.pt2Px(2f)
            )
        }
        isItemSelected = ta.getBoolean(R.styleable.TabBarItem_tbi_selected, false)
        ta.recycle()

        initView(context)
    }

    private fun initView(context: Context) {
        val containerLayout = LinearLayout(context)
        containerLayout.orientation = LinearLayout.VERTICAL
        containerLayout.gravity = Gravity.CENTER

        val containerParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        containerParams.gravity = Gravity.CENTER
        containerLayout.layoutParams = containerParams

        if (iconNormalResId != -1) {
            imgIcon = AppCompatImageView(context).also {
                it.setImageResource(iconNormalResId)
                val imaIconParams = LayoutParams(iconWidth, iconHeight)
                it.layoutParams = imaIconParams
                containerLayout.addView(it)
            }
        }

        if (text?.isNotEmpty() == true) {
            textTv = AppCompatTextView(context).also {
                it.text = text
                it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                if (textNormalColor != -1) {
                    it.setTextColor(textNormalColor)
                }
                val textParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                textParams.topMargin = drawablePadding
                it.layoutParams = textParams
                containerLayout.addView(it)
            }
        }

        addView(containerLayout)
        isSelected = isItemSelected
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        setSelectedStatus(selected)
    }

    fun setSelectedStatus(selected: Boolean) {
        textChange(selected)
        imgChange(selected)
    }

    private fun textChange(selected: Boolean) {
        if (selected) {
            textTv?.setTextColor(textSelectedColor)
        } else {
            textTv?.setTextColor(textNormalColor)
        }
    }

    private fun imgChange(selected: Boolean) {
        if (selected) {
            if (iconSelectedResId != -1) {
                imgIcon?.setImageResource(iconSelectedResId)
            }
            if (iconSelectedColor != -1) {
                imgIcon?.setColorFilter(iconSelectedColor)
            }
        } else {
            if (iconNormalResId != -1) {
                imgIcon?.setImageResource(iconNormalResId)
            }
            if (iconNormalColor != -1) {
                imgIcon?.setColorFilter(iconNormalColor)
            }
        }
    }
}
