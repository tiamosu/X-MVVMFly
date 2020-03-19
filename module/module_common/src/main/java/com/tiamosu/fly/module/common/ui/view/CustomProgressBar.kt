package com.tiamosu.fly.module.common.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.blankj.utilcode.util.AdaptScreenUtils
import com.tiamosu.fly.module.common.R
import kotlin.math.max
import kotlin.math.min

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 未达到的进度条画笔
     */
    private lateinit var unreachedBarPaint: Paint

    /**
     * 未达到的进度条获取边界
     */
    private val unreachedBarRectF by lazy { RectF(0f, 0f, 0f, 0f) }

    /**
     * 未达到的进度条高度
     */
    private val unreachedBarHeight: Float
    private val unreachedBarHeightHalf: Float

    /**
     * 未达到的进度条的颜色
     */
    private val unreachedBarColor: Int

    /**
     * 以达到的进度条画笔
     */
    private lateinit var reachedBarPaint: Paint

    /**
     * 已达到的进度条获取边界
     */
    private val reachedBarRectF by lazy { RectF(0f, 0f, 0f, 0f) }

    /**
     * 已达到的进度条高度
     */
    private val reachedBarHeight: Float
    private val reachedBarHeightHalf: Float

    /**
     * 已达到的进度条的颜色
     */
    private val reachedBarColor: Int

    /**
     * 是否开启渐变
     */
    private var isLinearGradient = false

    /**
     * 进度条文本画笔
     */
    private lateinit var textPaint: Paint

    /**
     * 进度条文本字体大小
     */
    private val textSize: Float

    /**
     * 进度条文本字体颜色
     */
    private val textColor: Int

    /**
     * 进度条文本内容
     */
    private var currentProgressText: String? = null

    /**
     * 进度条文本单位
     */
    private var unitText: String? = null

    /**
     * 是否显示进度文本
     */
    private val isDrawText: Boolean

    /**
     * 进度值文本距离进度条的间隔
     */
    private val offset: Float

    /**
     * 进度值文本X轴位置
     */
    private var drawTextX: Float = 0f

    /**
     * 进度值文本y轴位置
     */
    private var drawTextY: Float = 0f

    /**
     * 进度条总值
     */
    var max = 100f
        set(maxProgress) {
            if (maxProgress > 0) {
                field = maxProgress
                invalidate()
            }
        }

    /**
     * 进度条当前值
     */
    var progress = 0f
        set(progress) {
            if (progress in 0.0..max.toDouble()) {
                field = progress
                invalidate()
            }
        }

    private var mDrawReachedBar = true
    private var mDrawUnreachedBar = true

    private var mColorAnimator: ValueAnimator? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)
        unreachedBarHeight = ta.getDimension(
            R.styleable.CustomProgressBar_tpb_unreachedBarHeight,
            AdaptScreenUtils.pt2Px(12f).toFloat()
        )
        unreachedBarHeightHalf = unreachedBarHeight / 2
        unreachedBarColor =
            ta.getColor(R.styleable.CustomProgressBar_tpb_unreachedBarColor, Color.RED)

        reachedBarHeight = ta.getDimension(
            R.styleable.CustomProgressBar_tpb_reachedBarHeight,
            AdaptScreenUtils.pt2Px(12f).toFloat()
        )
        reachedBarHeightHalf = reachedBarHeight / 2
        reachedBarColor = ta.getColor(R.styleable.CustomProgressBar_tpb_reachedBarColor, Color.RED)
        isLinearGradient =
            ta.getBoolean(R.styleable.CustomProgressBar_tpb_linearGradient, isLinearGradient)

        textSize = ta.getDimension(
            R.styleable.CustomProgressBar_tpb_textSize,
            AdaptScreenUtils.pt2Px(14f).toFloat()
        )
        textColor = ta.getColor(R.styleable.CustomProgressBar_tpb_textColor, Color.RED)
        unitText = ta.getString(R.styleable.CustomProgressBar_tpb_textUnit)
        unitText = if (TextUtils.isEmpty(unitText)) "" else unitText

        offset = ta.getDimension(
            R.styleable.CustomProgressBar_tpb_offset,
            AdaptScreenUtils.pt2Px(2f).toFloat()
        )
        isDrawText = ta.getBoolean(R.styleable.CustomProgressBar_tpb_isTextVisible, false)

        max = ta.getFloat(R.styleable.CustomProgressBar_tpb_maxProgress, max)
        progress = ta.getFloat(R.styleable.CustomProgressBar_tpb_currentProgress, progress)
        ta.recycle()

        initPaint()
    }

    private fun initPaint() {
        unreachedBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        unreachedBarPaint.color = unreachedBarColor

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = textSize
        textPaint.color = textColor

        reachedBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        if (isLinearGradient) {
            val colors = intArrayOf(
                Color.parseColor("#E3736C"), Color.parseColor("#DE655D"),
                Color.parseColor("#DC6158"), Color.parseColor("#DC5A52"),
                Color.parseColor("#DB5148"), Color.parseColor("#DA4840"),
                Color.parseColor("#D83D34"), Color.parseColor("#D3281E"),
                Color.parseColor("#D3281E"), Color.parseColor("#D3281E"),
                Color.parseColor("#D3281E"), Color.parseColor("#D33B33")
            )
            val shader = LinearGradient(
                0f, 0f, 0f, reachedBarHeight,
                colors, null, Shader.TileMode.CLAMP
            )
            reachedBarPaint.shader = shader
        } else {
            reachedBarPaint.color = reachedBarColor
        }
    }

    override fun getSuggestedMinimumWidth(): Int {
        return unreachedBarHeight.toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        val progressTextHeight = if (isDrawText) getTextHeight(textPaint) else 0f
        val maxHeight = max(progressTextHeight, max(reachedBarHeight, unreachedBarHeight))
        return maxHeight.toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false))
    }

    private fun measure(measureSpec: Int, isWidth: Boolean): Int {
        var result: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        val padding = if (isWidth) paddingLeft + paddingRight else paddingTop + paddingBottom
        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = if (isWidth) suggestedMinimumWidth else suggestedMinimumHeight
            result += padding
            if (mode == MeasureSpec.AT_MOST) {
                result = if (isWidth) {
                    max(result, size)
                } else {
                    min(result, size)
                }
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        drawText(canvas)

        if (isDrawText) {
            drawReachedBar(canvas)
            drawUnreachedBar(canvas)
        } else {
            drawUnreachedBar(canvas)
            drawReachedBar(canvas)
        }
    }

    private fun calculateDrawRectFWithProgressText() {
        currentProgressText = (progress * 100 / max).toInt().toString()
        currentProgressText = currentProgressText!! + unitText!!
        val isTextMax = textSize > reachedBarHeight
        val baseline = getTextHeight(textPaint)
        val drawTextWidth = textPaint.measureText(currentProgressText)
        drawTextY = if (isTextMax)
            baseline
        else
            reachedBarHeightHalf - (textPaint.descent() + textPaint.ascent()) / 2f

        if (progress == 0f) {
            mDrawReachedBar = false
            drawTextX = paddingLeft.toFloat()
        } else {
            mDrawReachedBar = true
            reachedBarRectF.left = paddingLeft.toFloat()
            reachedBarRectF.top = if (isTextMax) (baseline - reachedBarHeight) / 2f else 0f
            reachedBarRectF.right =
                (width - paddingLeft - paddingRight) / max * progress - offset + paddingLeft
            reachedBarRectF.bottom =
                if (isTextMax) (baseline + reachedBarHeight) / 2f else reachedBarHeight
            drawTextX = reachedBarRectF.right + offset
        }
        if (drawTextX + drawTextWidth >= width - paddingRight) {
            drawTextX = width.toFloat() - paddingRight.toFloat() - drawTextWidth
            reachedBarRectF.right = drawTextX - offset
        }

        val unreachedBarStart = drawTextX + drawTextWidth + offset
        if (unreachedBarStart >= width - paddingRight) {
            mDrawUnreachedBar = false
        } else {
            mDrawUnreachedBar = true
            unreachedBarRectF.left = unreachedBarStart
            unreachedBarRectF.top = if (isTextMax)
                (baseline - unreachedBarHeight) / 2f
            else
                (reachedBarHeight - unreachedBarHeight) / 2f
            unreachedBarRectF.right = (width - paddingRight).toFloat()
            unreachedBarRectF.bottom = if (isTextMax)
                (baseline + unreachedBarHeight) / 2f
            else
                (reachedBarHeight + unreachedBarHeight) / 2f
        }
    }

    private fun calculateDrawRectFWithoutProgressText() {
        reachedBarRectF.left = paddingLeft.toFloat()
        reachedBarRectF.top = 0f
        reachedBarRectF.right = (width - paddingLeft - paddingRight) / max * progress + paddingLeft
        reachedBarRectF.bottom = reachedBarHeight

        unreachedBarRectF.left = if (isDrawText) reachedBarRectF.right else paddingLeft.toFloat()
        unreachedBarRectF.top = (reachedBarHeight - unreachedBarHeight) / 2f
        unreachedBarRectF.right = (width - paddingRight).toFloat()
        unreachedBarRectF.bottom = (reachedBarHeight + unreachedBarHeight) / 2f
    }

    private fun drawText(canvas: Canvas) {
        if (isDrawText) {
            calculateDrawRectFWithProgressText()
            canvas.drawText(currentProgressText!!, drawTextX, drawTextY, textPaint)
        } else {
            calculateDrawRectFWithoutProgressText()
        }
    }

    private fun drawUnreachedBar(canvas: Canvas) {
        if (mDrawUnreachedBar) {
            canvas.drawRoundRect(
                unreachedBarRectF, unreachedBarHeightHalf,
                unreachedBarHeightHalf, unreachedBarPaint
            )
        }
    }

    private fun drawReachedBar(canvas: Canvas) {
        if (mDrawReachedBar) {
            canvas.drawRoundRect(
                reachedBarRectF, reachedBarHeightHalf,
                reachedBarHeightHalf, reachedBarPaint
            )
        }
    }

    private fun getTextHeight(paint: Paint): Float {
        return paint.textSize - paint.fontMetrics.descent - paddingTop.toFloat()
    }

    fun setReachedBarColor(progressColor: Int) {
        reachedBarPaint.color = progressColor
        invalidate()
    }

    fun setActiveReachedBarColor(progressColor: Int, activeProgressColor: Int) {
        mColorAnimator = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator.ofObject(TextArgbEvaluator(), progressColor, activeProgressColor)
        } else {
            ValueAnimator.ofArgb(progressColor, activeProgressColor)
        }
        mColorAnimator?.apply {
            duration = 2000//设置时长
            repeatCount = ValueAnimator.INFINITE//设置重复执行次数
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()//匀速插值器

            addUpdateListener { animation ->
                reachedBarPaint.color = animation.animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mColorAnimator?.cancel()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.apply {
            putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
            putFloat(INSTANCE_PROGRESS_MAX, max)
            putFloat(INSTANCE_PROGRESS_CURRENT, progress)
        }
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            max = state.getFloat(INSTANCE_PROGRESS_MAX)
            progress = state.getFloat(INSTANCE_PROGRESS_CURRENT)
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    companion object {
        private const val INSTANCE_STATE = "CustomProgressBar_saved_instance"
        private const val INSTANCE_PROGRESS_CURRENT = "CustomProgressBar_progress_current"
        private const val INSTANCE_PROGRESS_MAX = "CustomProgressBar_progress_max"
    }
}
