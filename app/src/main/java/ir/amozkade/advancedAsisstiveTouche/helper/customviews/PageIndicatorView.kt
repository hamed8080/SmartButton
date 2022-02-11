package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository

class PageIndicatorView constructor(
        context: Context,
        attributeSet: AttributeSet
) : View(context, attributeSet) {

    private var paint: Paint = Paint()
    private var selectedPaint: Paint = Paint()
    var circleWidth = 12F
    private val margin = 12F

    internal var count: Int = 5
    internal var selectedIndex: Int = 0
        set(value) {
            field = value
            start = (remainingSpace / 2) + circleWidth / 2
            invalidate()
        }

    fun init(settingRepository: SettingRepository) {
        val color = settingRepository.getCashedModel().panelButtonsColor
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = color
        paint.alpha = 80

        selectedPaint = Paint()
        selectedPaint.isAntiAlias = true
        selectedPaint.style = Paint.Style.FILL
        selectedPaint.strokeWidth = 2F
        selectedPaint.color = color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidate()
    }

    private var midY: Float = 0F
    private var remainingSpace = 0F
    private var start = 0F

    private fun calculateSizes() {
        midY = (height / 2).toFloat()
        remainingSpace = width - ((circleWidth * count) + (margin * count))
        start = (remainingSpace / 2) + circleWidth / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        calculateSizes()
        for (i in 0 until count) {
            if (selectedIndex == i) {
                canvas?.drawCircle(start, midY, circleWidth / 2, selectedPaint)
            } else {
                canvas?.drawCircle(start, midY, circleWidth / 2, paint)
            }
            start += (circleWidth + margin)
        }
    }
}