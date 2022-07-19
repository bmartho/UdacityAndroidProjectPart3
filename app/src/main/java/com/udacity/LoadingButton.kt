package com.udacity

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val downloadTextSize = 55.0f
    private var loadingAccelerateFactor = 1
    private var textHalfWidthSize = 0f
    private var widthSize = 0
    private var heightSize = 0
    private var finishAnimations = false

    private val downloadValueAnimator = ValueAnimator.ofInt(0, 100)
    private val archValueAnimator = ValueAnimator.ofInt(0, 360)

    var downloadProgress = 0f
    var archProgress = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = downloadTextSize
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    private val defaultColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private val loadingColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private val archColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> startAnimations()
            ButtonState.Completed -> {
                loadingAccelerateFactor = 1
                cancelAnimations()
                invalidate()
            }
        }
    }

    init {
        downloadValueAnimator.apply {
            duration = 4000
            interpolator = LinearInterpolator()
            repeatCount = INFINITE
            addUpdateListener { valueAnimator ->
                downloadProgress =
                    (valueAnimator.animatedValue as Int).toFloat() * loadingAccelerateFactor
                invalidate()

                if (downloadProgress >= 100 && finishAnimations) {
                    setState(ButtonState.Completed)
                }
            }
        }

        archValueAnimator.apply {
            duration = 4000
            interpolator = LinearInterpolator()
            repeatCount = INFINITE
            addUpdateListener { valueAnimator ->
                archProgress =
                    (valueAnimator.animatedValue as Int).toFloat() * loadingAccelerateFactor
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawText(canvas)
        drawArch(canvas)
    }

    private fun drawArch(canvas: Canvas) {
        if (buttonState == ButtonState.Loading) {
            paint.color = archColor
            canvas.drawArc(
                ((widthSize / 2) + textHalfWidthSize + 30f),
                (heightSize / 2) - (downloadTextSize / 2),
                ((widthSize / 2) + textHalfWidthSize + 80f),
                (heightSize / 2) + (downloadTextSize / 2),
                0f,
                archProgress,
                true,
                paint
            )
        }
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(defaultColor)
        if (buttonState == ButtonState.Loading) {
            paint.color = loadingColor
            canvas.drawRect(0f, 0f, (width * (downloadProgress / 100)), height.toFloat(), paint)
        }
    }

    private fun drawText(canvas: Canvas) {
        val label = if (buttonState == ButtonState.Loading) {
            resources.getString(R.string.button_loading)
        } else {
            resources.getString(R.string.button_name)
        }
        paint.color = ResourcesCompat.getColor(resources, R.color.white, null)

        canvas.drawText(
            label,
            (widthSize / 2).toFloat(),
            (heightSize / 2) + (downloadTextSize / 3),
            paint
        )

        textHalfWidthSize = paint.measureText(label) / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun startAnimations() {
        downloadValueAnimator.start()
        archValueAnimator.start()
    }

    private fun cancelAnimations() {
        downloadValueAnimator.cancel()
        archValueAnimator.cancel()
    }

    fun complete() {
        loadingAccelerateFactor = 4
        finishAnimations = true
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    override fun performClick(): Boolean {
        if (buttonState != ButtonState.Completed) {
            return false
        }

        return super.performClick()
    }
}