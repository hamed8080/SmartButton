package ir.amozkade.advancedAsisstiveTouche.helper.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ir.amozkade.advancedAsisstiveTouche.R
import ir.mobitrain.applicationcore.helper.Converters.Companion.convertIntToDP
import ir.mobitrain.applicationcore.helper.Converters.Companion.convertIntToSP
import java.util.*

class LevelsCircle : View {

    private lateinit var textPaint: TextPaint
    private lateinit var paint: Paint
    private var circleColorBg: Int = ContextCompat.getColor(context, R.color.blue)
    private var circleColorBorder: Int = ContextCompat.getColor(context, R.color.blue)
    private val isFa = Locale.getDefault().language.contains("fa")
    private val padding = convertIntToDP(2 , context)
    private var circleText: String = ""
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.LevelsCircle))
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTypedArrayValues(context.obtainStyledAttributes(attrs, R.styleable.LevelsCircle))
        init()
    }

    private fun setTypedArrayValues(ta: TypedArray) {
        circleColorBg = ta.getColor(R.styleable.LevelsCircle_levels_circle_color_bg, ContextCompat.getColor(context, R.color.blue))
        circleColorBorder = ta.getColor(R.styleable.LevelsCircle_levels_circle_color_border, ContextCompat.getColor(context, R.color.blue))
        circleText = ta.getString(R.styleable.LevelsCircle_levels_circle_color_text) ?: ""
    }

    private fun init() {
        paint = Paint()
        paint.isAntiAlias = true
        paint.strokeWidth = 1f
        paint.style = Paint.Style.FILL
        paint.color = circleColorBg

        textPaint = TextPaint()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.isAntiAlias = true
        textPaint.color = ContextCompat.getColor(context, R.color.white)
        textPaint.textSize = convertIntToSP(16 , context)
        val fontId = if (isFa) R.font.iransans_bold else R.font.sf_pro_rounded_bold
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textPaint.typeface = context.resources.getFont(fontId)
        } else {
            textPaint.typeface = ResourcesCompat.getFont(context, fontId)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        textPaint.textSize = h / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        paint.style = Paint.Style.FILL
        paint.color = circleColorBg
        canvas?.drawCircle(width / 2f, height / 2f, (height / 2f) - padding, paint)

        paint.style = Paint.Style.STROKE
        paint.color = circleColorBorder
        canvas?.drawCircle(width / 2f, height / 2f, (height / 2f) - padding, paint)

        canvas?.drawText(circleText, width / 2f, (height / 2f ) + (textPaint.textSize / 3f), textPaint)
        canvas?.restore()
    }

    fun setTitle(title:String){
        circleText = title
    }
}