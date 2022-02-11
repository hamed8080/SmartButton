package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ir.amozkade.advancedAsisstiveTouche.R
import ir.mobitrain.applicationcore.helper.CommonHelpers
import ir.mobitrain.applicationcore.helper.Converters
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class StepperView : View {

    interface StepperDelegate {
        fun onNewValue(value: Int)
    }

    private lateinit var textCenterPaint: Paint
    private lateinit var oval: RectF
    private lateinit var bgOval: RectF
    private lateinit var paint: Paint
    private lateinit var bgPaint: Paint
    var maxValue: Float = 0f
    var currentPercent = 0f
    private var startDegree = 155f
    private var maxDegree = 230f

    var trackWidth = Converters.convertIntToDP(6 , context)
    var delegate: StepperDelegate? = null
    var fontTextSize = Converters.convertIntToSP(48, context)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        oval.right = w.toFloat() - trackWidth
        oval.bottom = h.toFloat()

        bgOval.right = w.toFloat() - trackWidth
        bgOval.bottom = h.toFloat()
    }

    private fun init() {
        paint = Paint()
        bgPaint = Paint()
        paint.isAntiAlias = true
        paint.strokeWidth = trackWidth
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = ContextCompat.getColor(context, R.color.blue)
        oval = RectF(trackWidth, trackWidth, width.toFloat() - trackWidth, height.toFloat())

        bgPaint.isAntiAlias = true
        bgPaint.strokeWidth = trackWidth
        bgPaint.style = Paint.Style.STROKE
        bgPaint.strokeCap = Paint.Cap.ROUND
        bgPaint.color = ContextCompat.getColor(context, R.color.white_darker_3X)
        bgOval = RectF(trackWidth, trackWidth, width.toFloat() - trackWidth, height.toFloat())

        textCenterPaint = Paint()
        textCenterPaint.isAntiAlias = true
        textCenterPaint.strokeWidth = trackWidth
        textCenterPaint.style = Paint.Style.FILL
        textCenterPaint.strokeCap = Paint.Cap.ROUND
        textCenterPaint.textAlign = Paint.Align.CENTER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textCenterPaint.typeface = context.resources.getFont(R.font.sf_pro_rounded_bold)
        } else {
            textCenterPaint.typeface = ResourcesCompat.getFont(context, R.font.sf_pro_rounded_bold)
        }
        textCenterPaint.textSize = fontTextSize
        textCenterPaint.color = ContextCompat.getColor(context, R.color.black_text_bold_color)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.drawArc(bgOval, startDegree, maxDegree, false, bgPaint)
        canvas?.drawArc(oval, startDegree, computedDegree, false, paint)
        canvas?.drawText(currentPercent.toInt().toString(), width / 2f, height / 2f + (fontTextSize / 4f), textCenterPaint)
        canvas?.restore()
    }

    private val computedDegree: Float
        get() {
            return (currentPercent / 100) * maxDegree
        }

    fun setProgress(progress: Int) {
        currentPercent = progress.toFloat()
        invalidateOnMain()
    }

    fun increaseOnHold() {
        if (currentPercent >= 100) return
        currentPercent += 1
        invalidateOnMain()
    }

    fun decreaseOnHold() {
        if (currentPercent <= 0) return
        currentPercent -= 1
        invalidateOnMain()
    }

    fun increase() {
        if (currentPercent >= 100) return
        currentPercent += 1
        invalidate()
    }

    fun decrease() {
        if (currentPercent <= 0) return
        currentPercent -= 1
        invalidateOnMain()
    }

    private fun invalidateOnMain() {
        GlobalScope.launch(Main) {
            invalidate()
        }
    }

    fun updateDelegate() {
        delegate?.onNewValue(currentPercent.toInt())
    }
}

